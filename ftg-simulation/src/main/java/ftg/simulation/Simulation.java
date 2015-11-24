package ftg.simulation;

import static ftg.model.person.PersonUtil.ALIVE;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;
import static ftg.model.world.PersonBucket.ALL_LIVING;
import static ftg.model.world.PersonBucket.MARRIED_NON_PREGNANT_FEMALES;
import static ftg.model.world.PersonBucket.MARRIED_PREGNANT_FEMALES;
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
import javaslang.Value;
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
    private final EventFactory eventFactory;
    private final Reaper reaper;
    private final Matchmaker matchmaker;

    private TredecimalDate currentDate = new TredecimalDate(-1);
    private Map<TredecimalDate, Set<DeathEvent>> reaperPlan = TreeMap.empty(TredecimalDate::compareTo);
    private Map<TredecimalDate, Set<MarriageEvent>> matchmakerPlan = TreeMap.empty(TredecimalDate::compareTo);
    private Stats stats = new Stats();


    @Inject
    public Simulation(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        this.reaper = new Reaper(eventFactory);
        this.matchmaker = new Matchmaker(eventFactory, lineages);
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public void nextDay(World world) {

        currentDate = currentDate.plusDays(1);
        ThreadContext.put("date", TredecimalDateFormat.ISO.format(currentDate));

        if (currentDate.isZeroDay()) {
            yearlyPlanning(world);
        }

        measurements.dailyMatchmaker.start();
        final Set<MarriageEvent> marriages = matchmakerPlan.get(currentDate).orElseGet(HashSet::empty);
        matchmakerPlan = matchmakerPlan.remove(currentDate);

        marriages.filter(event -> ALIVE.test(world.getPerson(event.getHusbandId())) && ALIVE.test(world.getPerson(event.getWifeId())))
            .peek(event -> stats.marriages++)
            .forEach(world::submitEvent);
        measurements.dailyMatchmaker.stop();

        // pregnancies
        measurements.dailyPregnancies.start();
        world.persons(MARRIED_NON_PREGNANT_FEMALES)
            .filter(female -> fertileAge.includes(female.getAge(currentDate).getYears()))
            .map(female -> decidePregnancyInMarriage(female, eventFactory))
            .flatMap(Value::toSet)
            .forEach(world::submitEvent);
        measurements.dailyPregnancies.stop();

        // births
        measurements.dailyBirths.start();
        world.persons(MARRIED_PREGNANT_FEMALES)
            .filter(person -> person.state(Pregnancy.class).map(pregnancy -> pregnancy.getAge(currentDate).getDays()).orElse(0L) == 280)
            .map(person -> decideBirth(person, eventFactory))
            .peek(event -> stats.peopleBorn++)
            .forEach(world::submitEvent);
        measurements.dailyBirths.stop();

        // deaths
        measurements.dailyReaper.start();
        final Set<DeathEvent> deaths = reaperPlan.get(currentDate).orElseGet(HashSet::empty);
        reaperPlan = reaperPlan.remove(currentDate);
        deaths.forEach(world::submitEvent);
        measurements.dailyReaper.stop();

        stats.peopleDied += deaths.length();
    }

    private void yearlyPlanning(World world) {
        LOGGER.info(stats.toString() + String.format(", total alive: %s", world.persons(ALL_LIVING).length()));
        stats = new Stats();

        measurements.yearlyMatchmaker.start();
        world.countries().forEach(country ->
                                      matchmaker.decide(currentDate.getYear(), country, world.persons(SINGLE_MALES),
                                                        world.persons(SINGLE_FEMALES)) // TODO choose people by country
                                          .forEach(event -> matchmakerPlan =
                                              matchmakerPlan
                                                  .put(event.getDate(), matchmakerPlan.get(event.getDate()).orElseGet(HashSet::empty).add(event))));
        measurements.yearlyMatchmaker.stop();

        measurements.yearlyReaper.start();
        world.persons(ALL_LIVING)
            .forEach(person -> reaper.decide(currentDate.getYear(), person)
                .peek(event -> reaperPlan = reaperPlan.put(event.getDate(), reaperPlan.get(event.getDate()).orElseGet(HashSet::empty).add(event))));
        measurements.yearlyReaper.stop();
    }

    private Option<ConceptionEvent> decidePregnancyInMarriage(Person female, EventFactory eventFactory) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (RandomChoice.byChance(chance)) {

            final Person.Sex sex = RandomChoice.from(Person.Sex.class);

            return Option.of(eventFactory.newConceptionEvent(currentDate,
                                                             female.relations(Marriage.class).head().getHusband().getId(),
                                                             female.getId(),
                                                             sex));
        }
        return Option.none();
    }

    private Event decideBirth(Person mother, EventFactory eventFactory) {
        final Pregnancy pregnancy = mother.state(Pregnancy.class).get();

        final Country country = mother.state(Residence.class).get().getCountry();
        final String childName = country.getNativeNames().childName(mother, pregnancy);

        return eventFactory.newBirthEvent(currentDate, mother.getId(), childName, pregnancy.getFather().getSurnameObject());
    }

    private Option<DeathEvent> decideDeath(Person person, EventFactory eventFactory) {
        final long age = intervalBetween(person.getBirthDate(), currentDate).getYears();
        final Country country = person.state(Residence.class).get().getCountry();
        final double chance = 1 / country.getDemography().getDeathRisk(age, person.getSex()) / DAYS_IN_YEAR;

        return RandomChoice.byChance(chance)
               ? Option.of(eventFactory.newDeathEvent(currentDate, person.getId()))
               : Option.none();
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

        final AveragingStopwatch yearlyMatchmaker = new AveragingStopwatch();
        final AveragingStopwatch yearlyReaper = new AveragingStopwatch();
        final AveragingStopwatch dailyMatchmaker = new AveragingStopwatch();
        final AveragingStopwatch dailyPregnancies = new AveragingStopwatch();
        final AveragingStopwatch dailyBirths = new AveragingStopwatch();
        final AveragingStopwatch dailyReaper = new AveragingStopwatch();

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("yearlyMatchmaker", yearlyMatchmaker)
                .add("yearlyReaper", yearlyReaper)
                .add("dailyMatchmaker", dailyMatchmaker)
                .add("dailyPregnancies", dailyPregnancies)
                .add("dailyBirths", dailyBirths)
                .add("dailyReaper", dailyReaper)
                .toString();
        }
    }
}
