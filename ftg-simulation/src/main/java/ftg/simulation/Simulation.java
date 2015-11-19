package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;
import static ftg.model.world.PersonBucket.ALL_LIVING;
import static ftg.model.world.PersonBucket.MARRIED_NON_PREGNANT_FEMALES;
import static ftg.model.world.PersonBucket.MARRIED_PREGNANT_FEMALES;
import static ftg.model.world.PersonBucket.SINGLE_FEMALES;
import static ftg.model.world.PersonBucket.SINGLE_MALES;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.Event;
import ftg.model.event.EventFactory;
import ftg.model.event.MarriageEvent;
import ftg.model.event.PersonData;
import ftg.model.person.Person;
import ftg.model.person.relation.Marriage;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.lineage.Lineages;
import javaslang.Value;
import javaslang.collection.Seq;
import javaslang.control.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final RandomChoice randomChoice = new RandomChoice();
    private final Lineages lineages = new Lineages();
    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final Configuration configuration;

    private final Map<String, Country> countries;

    private TredecimalDate currentDate = new TredecimalDate(0);

    @Inject
    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        this.countries = ImmutableMap.copyOf(configuration.getCountries().stream().collect(Collectors.toMap(Country::getName, c -> c)));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public List<Event> nextDay(World world, EventFactory eventFactory) {

        currentDate = currentDate.plusDays(1);

        final List<Event> events = new ArrayList<>();

        // marriages
        world.persons(SINGLE_MALES)
            .map(male -> decideMarriage(male, world.persons(SINGLE_FEMALES).toVector(), eventFactory))
            .flatMap(Value::toSet)
            .peek(events::add)
            .forEach(world::submitEvent);

        // pregnancies
        world.persons(MARRIED_NON_PREGNANT_FEMALES)
            .filter(female -> fertileAge.includes(female.getAge(currentDate).getYears()))
            .map(female -> decidePregnancyInMarriage(female, eventFactory))
            .flatMap(Value::toSet)
            .peek(events::add)
            .forEach(world::submitEvent);

        // births
        world.persons(MARRIED_PREGNANT_FEMALES)
            .filter(person -> person.state(Pregnancy.class).map(pregnancy -> pregnancy.getAge(currentDate).getDays()).orElse(0L) == 280)
            .map(person -> decideBirth(person, eventFactory))
            .peek(events::add)
            .forEach(world::submitEvent);

        // deaths
        world.persons(ALL_LIVING)
            .map(person -> decideDeath(person, eventFactory))
            .flatMap(Value::toSet)
            .peek(events::add)
            .forEach(world::submitEvent);

        return events;
    }

    private Option<MarriageEvent> decideMarriage(Person male, Seq<Person> unmarriedFemales, EventFactory eventFactory) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final Seq<Person> candidates = unmarriedFemales
                .filter(f -> lineages.findClosestAncestry(male, f).isEmpty()) // male is not descendant of female
                .filter(f -> lineages.findClosestAncestry(f, male).isEmpty()) // female is not descendant of male
                .filter(f -> lineages.findClosestRelation(male, f, 2).orElse(2) >= 2); // no siblings

            if (candidates.isEmpty()) {
                // TODO introduce new random female
            } else {
                int index = randomChoice.fromRangeByGaussian(candidates.length());
                return Option.of(eventFactory.newMarriageEvent(currentDate, male.getId(), unmarriedFemales.get(index).getId()));
            }

        }

        return Option.none();
    }

    private Option<ConceptionEvent> decidePregnancyInMarriage(Person female, EventFactory eventFactory) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final Person.Sex sex = randomChoice.from(Person.Sex.class);

            return Option.of(eventFactory.newConceptionEvent(currentDate,
                                                             female.relations(Marriage.class).head().getHusband().getId(),
                                                             female.getId(),
                                                             sex));
        }
        return Option.none();
    }

    private Event decideBirth(Person mother, EventFactory eventFactory) {
        final Pregnancy pregnancy = mother.state(Pregnancy.class).get();
        final Residence residence = mother.state(Residence.class).get();

        final String childName = countries.get(residence.getCountry()).getNamingSystem().getNameForNewborn(mother, pregnancy);

        final PersonData childData = eventFactory.newPersonData(
            childName,
            pregnancy.getFather().getSurnameObject(),
            pregnancy.getChildSex(),
            currentDate,
            residence
        );

        return eventFactory.newBirthEvent(currentDate, childData, mother.getId(), pregnancy.getFather().getId());
    }

    private Option<DeathEvent> decideDeath(Person person, EventFactory eventFactory) {
        final long age = intervalBetween(person.getBirthDate(), currentDate).getYears();
        final Country country = requireNonNull(countries.get(person.state(Residence.class).get().getCountry()));
        final double chance = 1 / country.getDemography().getDeathRisk(age, person.getSex()) / DAYS_IN_YEAR;

        return randomChoice.byChance(chance)
               ? Option.of(eventFactory.newDeathEvent(currentDate, person.getId()))
               : Option.none();
    }
}
