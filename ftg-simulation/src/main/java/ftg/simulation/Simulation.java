package ftg.simulation;

import static ftg.model.person.PersonUtil.ALIVE;
import static ftg.model.person.PersonUtil.MARRIED;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.world.PersonBucket.ALL_LIVING;
import static ftg.model.world.PersonBucket.SINGLE_FEMALES;
import static ftg.model.world.PersonBucket.SINGLE_MALES;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import ftg.commons.AveragingStopwatch;
import ftg.commons.range.IntegerRange;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.Event;
import ftg.model.event.EventFactory;
import ftg.model.event.MarriageEvent;
import ftg.model.person.Person;
import ftg.model.person.relation.Marriage;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import ftg.model.world.country.Country;
import ftg.simulation.lineage.Lineages;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.collection.TreeMap;
import javaslang.control.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    public final Measurements measurements = new Measurements();

    private final Lineages lineages = new Lineages();
    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);
    private final double pregnancyChance = 60D / 1000D / DAYS_IN_YEAR;

    private final EventFactory eventFactory;
    private final Reaper reaper;
    private final Matchmaker matchmaker;

    private Map<TredecimalDate, Map<String, DeathEvent>> reaperPlan = TreeMap.empty(TredecimalDate::compareTo);
    private Map<TredecimalDate, Map<String, MarriageEvent>> matchmakerPlan = TreeMap.empty(TredecimalDate::compareTo);
    private Stats stats = new Stats();


    @Inject
    public Simulation(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        this.reaper = new Reaper(eventFactory);
        this.matchmaker = new Matchmaker(eventFactory, lineages);
    }

    public void nextDay(World world) {
        measurements.simulationDay.start();
        final TredecimalDate currentDate = world.nextDay();
        ThreadContext.put("date", TredecimalDateFormat.ISO.format(currentDate));

        if (currentDate.isZeroDay()) {
            yearlyPlanning(world);
        }

        measurements.dailyCalculations.start();
        final Map<String, MarriageEvent> marriages = matchmakerPlan.get(currentDate).orElseGet(HashMap::empty);
        matchmakerPlan = matchmakerPlan.remove(currentDate);

        final Map<String, DeathEvent> deaths = reaperPlan.get(currentDate).orElseGet(HashMap::empty);
        reaperPlan = reaperPlan.remove(currentDate);

        // Decision cycle
        for (Person person : world.persons(ALL_LIVING)) {

            // marriage
            marriages.get(person.getId())
                .filter(event -> HashSet.ofAll(event.getHusbandId(), event.getWifeId()).map(world::getPerson).forAll(ALIVE))
                .filter(event -> HashSet.ofAll(event.getHusbandId(), event.getWifeId()).map(world::getPerson).forAll(MARRIED.negate()))
                .peek(event -> stats.marriages++)
                .forEach(world::submitEvent);

            if (person.getSex() == Person.Sex.FEMALE) {

                final Option<Pregnancy> pregnancy = person.state(Pregnancy.class);

                if (pregnancy.isEmpty()) {
                    world.submitEvents(decidePregnancy(currentDate, person));
                } else {
                    world.submitEvents(decideBirth(currentDate, person, pregnancy.get())
                        .peek(event -> stats.peopleBorn += 1));
                }
            }


            // death
            deaths.get(person.getId())
                .peek(event -> stats.peopleDied += 1)
                .forEach(world::submitEvent);
        }

        measurements.dailyCalculations.stop();
        measurements.simulationDay.stop();
    }

    private void yearlyPlanning(World world) {
        final TredecimalDate currentDate = world.getCurrentDate();

        LOGGER.info(stats.toString() + String.format(", total alive: %s", world.persons(ALL_LIVING).length()));
        stats = new Stats();

        measurements.yearlyMatchmaker.start();
        for (Country country : world.countries()) {
            final Set<MarriageEvent> decision = matchmaker.decide(currentDate.getYear(),
                country,
                world.persons(SINGLE_MALES),
                world.persons(SINGLE_FEMALES));// TODO choose people by country

            for (MarriageEvent marriage : decision) {
                final Map<String, MarriageEvent> planForDay = matchmakerPlan.get(marriage.getDate()).orElseGet(HashMap::empty);
                matchmakerPlan = matchmakerPlan.put(marriage.getDate(), planForDay.put(marriage.getHusbandId(), marriage).put(marriage.getWifeId(), marriage));
            }
        }
        measurements.yearlyMatchmaker.stop();

        measurements.yearlyReaper.start();
        world.persons(ALL_LIVING).forEach(person -> reaper.decide(currentDate.getYear(), person)
            .peek(event -> {
                    final Map<String, DeathEvent> planForDay = reaperPlan.get(event.getDate()).orElseGet(HashMap::empty);
                    reaperPlan = reaperPlan.put(event.getDate(), planForDay.put(event.getDeceasedId(), event));
                }
            ));
        measurements.yearlyReaper.stop();
    }

    private Option<ConceptionEvent> decidePregnancy(TredecimalDate currentDate, Person person) {
        final Set<Marriage> marriage = person.relations(Marriage.class);
        if (marriage.isDefined() && RandomChoice.byChance(pregnancyChance)) {

            return Option.of(eventFactory.newConceptionEvent(
                currentDate,
                marriage.head().getHusband().getId(),
                person.getId(),
                RandomChoice.from(Person.Sex.class)));
        }

        return Option.none();
    }


    private Option<Event> decideBirth(TredecimalDate currentDate, Person mother, Pregnancy pregnancy) {

        final Country country = mother.state(Residence.class).get().getCountry();
        final String childName = country.getNativeNames().childName(mother, pregnancy);

        return Option.of(eventFactory.newBirthEvent(currentDate, mother.getId(), childName, pregnancy.getFather().getSurnameObject()));
    }

    private static final class Stats {

        private long peopleBorn = 0;
        private long peopleDied = 0;
        private long marriages = 0;

        @Override
        public String toString() {
            return String.format("born: %s, died: %s, marriages: %s", peopleBorn, peopleDied, marriages);
        }
    }


    public static final class Measurements {

        final AveragingStopwatch simulationDay = new AveragingStopwatch();
        final AveragingStopwatch dailyCalculations = new AveragingStopwatch();
        final AveragingStopwatch yearlyMatchmaker = new AveragingStopwatch();
        final AveragingStopwatch yearlyReaper = new AveragingStopwatch();

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("simulationDay", simulationDay)
                .add("dailyCalculations", dailyCalculations)
                .add("yearlyMatchmaker", yearlyMatchmaker)
                .add("yearlyReaper", yearlyReaper)
                .toString();
        }
    }
}
