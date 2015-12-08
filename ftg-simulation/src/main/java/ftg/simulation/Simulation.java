package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import ftg.commons.AveragingStopwatch;
import ftg.model.event.BirthEvent;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.EventFactory;
import ftg.model.event.MarriageEvent;
import ftg.model.person.Person;
import ftg.model.person.relation.Marriage;
import ftg.model.person.state.Death;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.model.world.country.Country;
import ftg.simulation.lineage.Lineages;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Set;
import javaslang.collection.TreeMap;
import javaslang.collection.Vector;
import javaslang.control.Option;

public final class Simulation {

    public final Measurements measurements = new Measurements();

    private final Lineages lineages = new Lineages();
    private final double pregnancyChance = 60D / 1000D / DAYS_IN_YEAR;

    private final EventFactory eventFactory;
    private final Reaper reaper;
    private final Matchmaker matchmaker;

    private Map<TredecimalDate, Seq<DeathEvent>> reaperPlan = TreeMap.empty(TredecimalDate::compareTo);
    private Map<TredecimalDate, Seq<MarriageEvent>> matchmakerPlan = TreeMap.empty(TredecimalDate::compareTo);


    @Inject
    public Simulation(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        this.reaper = new Reaper(eventFactory);
        this.matchmaker = new Matchmaker(eventFactory, lineages);
    }

    public void nextDay(World world) {
        measurements.simulationDay.start();
        final TredecimalDate currentDate = world.nextDay();

        if (currentDate.isZeroDay()) {
            yearlyPlanning(world);
        }

        measurements.dailyCalculations.start();

        // Decision cycles

        measurements.dailyMarriages.start();

        final Seq<MarriageEvent> marriages = matchmakerPlan.get(currentDate).orElseGet(Vector::empty);
        matchmakerPlan = matchmakerPlan.remove(currentDate);

        for (MarriageEvent marriage : marriages) {
            final Person husband = world.getPerson(marriage.getHusbandId());
            final Person wife = world.getPerson(marriage.getWifeId());

            if (husband.relations(Marriage.class).isEmpty() && wife.relations(Marriage.class).isEmpty()) {
                world.submitMarriage(marriage);
            }
        }
        measurements.dailyMarriages.stop();

        measurements.dailyReproduction.start();
        for (Person person : world.buckets().livingFemales()) {
            world.submitPregnancy(decidePregnancy(currentDate, person));
            world.submitBirth(decideBirth(currentDate, person));
        }
        measurements.dailyReproduction.stop();

        measurements.dailyDeaths.start();

        final Seq<DeathEvent> deaths = reaperPlan.get(currentDate).orElseGet(Vector::empty);
        reaperPlan = reaperPlan.remove(currentDate);

        for (DeathEvent death : deaths) {
            if (world.getPerson(death.getDeceasedId()).state(Death.class).isEmpty()) {
                world.submitDeath(death);
            }
        }

        measurements.dailyDeaths.stop();

        measurements.dailyCalculations.stop();
        measurements.simulationDay.stop();
    }

    private void yearlyPlanning(World world) {
        final TredecimalDate currentDate = world.getCurrentDate();

        measurements.yearlyMatchmaker.start();
        for (Country country : world.countries()) {
            final Set<MarriageEvent> decision = matchmaker.decide(currentDate.getYear(),
                country,
                world.buckets().singleMales(),
                world.buckets().singleFemales());// TODO choose people by country

            for (MarriageEvent marriage : decision) {
                final Seq<MarriageEvent> planForDay = matchmakerPlan.get(marriage.getDate()).orElseGet(Vector::empty);
                matchmakerPlan = matchmakerPlan.put(marriage.getDate(), planForDay.append(marriage));
            }
        }
        measurements.yearlyMatchmaker.stop();

        measurements.yearlyReaper.start();
        for (Person person : world.buckets().livingPersons()) {
            final Option<DeathEvent> decision = reaper.decide(currentDate.getYear(), person);

            if (decision.isDefined()) {
                final DeathEvent event = decision.get();

                final Seq<DeathEvent> planForDay = reaperPlan.get(event.getDate()).orElseGet(Vector::empty);
                reaperPlan = reaperPlan.put(event.getDate(), planForDay.append(event));
            }

        }
        measurements.yearlyReaper.stop();
    }

    private Option<ConceptionEvent> decidePregnancy(TredecimalDate currentDate, Person person) {
        final Set<Marriage> marriage = person.relations(Marriage.class);
        if (marriage.isDefined() && person.state(Pregnancy.class).isEmpty() && RandomChoice.byChance(pregnancyChance)) {

            return Option.of(eventFactory.newConceptionEvent(
                currentDate,
                marriage.head().getHusband().getId(),
                person.getId(),
                RandomChoice.from(Person.Sex.class)));
        }

        return Option.none();
    }


    private Option<BirthEvent> decideBirth(TredecimalDate currentDate, Person mother) {

        final Option<Pregnancy> pregnancy = mother.state(Pregnancy.class);

        if (pregnancy.isDefined()) {

        final Country country = mother.state(Residence.class).get().getCountry();
            final String childName = country.getNativeNames().childName(mother, pregnancy.get());

            return Option.of(eventFactory.newBirthEvent(currentDate, mother.getId(), childName, pregnancy.get().getFather().getSurnameObject()));
        }
        return Option.none();
    }

    public static final class Measurements {

        final AveragingStopwatch simulationDay = new AveragingStopwatch();
        final AveragingStopwatch dailyCalculations = new AveragingStopwatch();
        final AveragingStopwatch dailyMarriages = new AveragingStopwatch();
        final AveragingStopwatch dailyReproduction = new AveragingStopwatch();
        final AveragingStopwatch dailyDeaths = new AveragingStopwatch();
        final AveragingStopwatch yearlyMatchmaker = new AveragingStopwatch();
        final AveragingStopwatch yearlyReaper = new AveragingStopwatch();

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("simulationDay", simulationDay).addValue('\n')
                .add("dailyCalculations", dailyCalculations).addValue('\n')
                .add("dailyMarriages", dailyMarriages).addValue('\n')
                .add("dailyReproduction", dailyReproduction).addValue('\n')
                .add("dailyDeaths", dailyDeaths).addValue('\n')
                .add("yearlyMatchmaker", yearlyMatchmaker).addValue('\n')
                .add("yearlyReaper", yearlyReaper).addValue('\n')
                .toString();
        }
    }
}
