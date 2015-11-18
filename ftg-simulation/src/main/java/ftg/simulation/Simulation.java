package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;
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
import ftg.model.relation.Marriage;
import ftg.model.state.Death;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
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
        final Seq<Person> unmarriedFemales = world.persons()
            .filter(person -> person.state(Death.class).isEmpty())
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .filter(female -> female.relations(Marriage.class).isEmpty())
            .sortBy(Person::getBirthDate);

        world.persons()
            .filter(person -> person.state(Death.class).isEmpty())
            .filter(person -> person.getSex() == Person.Sex.MALE)
            .filter(male -> male.relations(Marriage.class).isEmpty())
            .map(male -> decideMarriage(male, unmarriedFemales, eventFactory))
            .flatMap(Value::toSet)
            .peek(events::add)
            .forEach(world::submitEvent);


        // pregnancies
        world.persons()
            .filter(person -> person.state(Death.class).isEmpty())
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .filter(female -> female.state(Pregnancy.class).isEmpty())
            .filter(female -> female.relations(Marriage.class).isDefined())
            .filter(female -> fertileAge.includes(intervalBetween(female.getBirthDate(), currentDate).getYears()))
            .map(female -> decidePregnancyInMarriage(female, eventFactory))
            .flatMap(Value::toSet)
            .peek(events::add)
            .forEach(world::submitEvent);

        // births
        world.persons()
            .filter(person -> person.state(Death.class).isEmpty())
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .filter(person -> person.state(Pregnancy.class).isDefined())
            .filter(person -> intervalBetween(currentDate, person.state(Pregnancy.class).get().getConceptionDate()).getDays() == 280)
            .map(person -> decideBirth(person, eventFactory))
            .peek(events::add)
            .forEach(world::submitEvent);

        // deaths
        world.persons()
            .filter(person -> person.state(Death.class).isEmpty())
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
