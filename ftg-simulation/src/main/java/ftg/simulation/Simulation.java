package ftg.simulation;

import static ftg.commons.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.commons.time.TredecimalDateInterval.intervalBetween;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import ftg.commons.Util;
import ftg.commons.range.IntegerRange;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.event.ConceptionEvent;
import ftg.graph.model.event.DeathEvent;
import ftg.graph.model.event.Event;
import ftg.graph.model.event.EventFactory;
import ftg.graph.model.event.MarriageEvent;
import ftg.graph.model.event.PersonData;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.simulation.configuration.SimulatedCountry;
import ftg.simulation.configuration.SimulationConfiguration;
import ftg.simulation.lineage.Lineages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final RandomChoice randomChoice = new RandomChoice();
    private final Lineages lineages = new Lineages();
    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final SimulationConfiguration configuration;

    private final Map<String, SimulatedCountry> countries;

    private TredecimalDate currentDate = new TredecimalDate(0);

    @Inject
    public Simulation(SimulationConfiguration configuration) {

        this.configuration = configuration;
        this.countries = ImmutableMap.copyOf(configuration.getCountries().stream().collect(Collectors.toMap(SimulatedCountry::getName, c -> c)));
    }

    public SimulationConfiguration getConfiguration() {
        return configuration;
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public List<Event> nextDay(SimulatedWorld world, EventFactory eventFactory) {

        currentDate = currentDate.plusDays(1);

        final List<Event> events = new ArrayList<>();

        // marriages
        final List<Woman> unmarriedFemales = world.getQueries().getUnmarriedWomen().stream()
                .sorted((o1, o2) -> o1.getBirthDate().compareTo(o2.getBirthDate()))
                .collect(Collectors.toList());

        world.getQueries().getUnmarriedMen().stream()
            .map(male -> decideMarriage(eventFactory, male, unmarriedFemales))
                .flatMap(Util::streamFromOptional)
                .peek(events::add)
                .forEach(world::submitEvent);


        // pregnancies
        world.getQueries().getMarriedNonPregnantWomen().stream()
                .filter(female -> fertileAge.includes(intervalBetween(female.getBirthDate(), currentDate).getYears()))
                .map(woman -> decidePregnancyInMarriage(eventFactory, woman))
                .flatMap(Util::streamFromOptional)
                .peek(events::add)
                .forEach(world::submitEvent);

        // births
        world.getQueries().getPregnantWomen().stream()
            .filter(woman -> intervalBetween(currentDate, woman.getPregnancy().getConceptionDate()).getDays() == 280)
                .map(woman -> decideBirth(eventFactory, woman))
                .peek(events::add)
                .forEach(world::submitEvent);

        // deaths
        world.getQueries().getLivingPersons().stream()
                .map(person -> decideDeath(eventFactory, person))
                .flatMap(Util::streamFromOptional)
                .peek(events::add)
                .forEach(world::submitEvent);

        return events;
    }


    private Optional<MarriageEvent> decideMarriage(EventFactory eventFactory, Person male, List<Woman> unmarriedFemales) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final List<Person> candidates = unmarriedFemales.stream()
                    .filter(f -> !lineages.findClosestAncestry(male, f).isPresent()) // male is not descendant of female
                    .filter(f -> !lineages.findClosestAncestry(f, male).isPresent()) // female is not descendant of male
                    .filter(f -> lineages.findClosestRelation(male, f, 2).orElse(2) >= 2) // no siblings
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) {
                // TODO introduce new random female
            } else {
                int index = randomChoice.fromRangeByGaussian(candidates.size());
                return Optional.of(
                    eventFactory.newMarriageEvent(currentDate, male.getId(), unmarriedFemales.remove(index).getId()));
            }


        }
        return Optional.empty();
    }

    private Optional<ConceptionEvent> decidePregnancyInMarriage(EventFactory eventFactory, Woman female) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final Person.Sex sex = randomChoice.from(Person.Sex.class);

            return Optional.of(eventFactory.newConceptionEvent(
                currentDate,
                female.getSpouse().getId(),
                female.getId(),
                sex));
        }
        return Optional.empty();
    }

    private Event decideBirth(EventFactory eventFactory, Woman mother) {
        final Pregnancy pregnancy = mother.getPregnancy();
        final Country country = mother.getCountryOfResidence();

        final String childName = countries.get(country.getName()).getNamingSystem().getNameForNewborn(mother, pregnancy);

        final PersonData childData = eventFactory.newPersonData(
            childName,
            pregnancy.getFather().getSurname(),
            pregnancy.getChildSex(),
            currentDate,
            country.getName()
        );

        return eventFactory.newBirthEvent(currentDate, childData, mother.getId(), pregnancy.getFather().getId());
    }

    private Optional<DeathEvent> decideDeath(EventFactory eventFactory, Person person) {
        final long age = intervalBetween(person.getBirthDate(), currentDate).getYears();
        final SimulatedCountry simulatedCountry = requireNonNull(countries.get(person.getCountryOfResidence().getName()));
        final double chance = 1 / simulatedCountry.getDemography().getDeathRisk(age, person.getSex()) / DAYS_IN_YEAR;

        return randomChoice.byChance(chance)
               ? Optional.of(eventFactory.newDeathEvent(currentDate, person.getId()))
               : Optional.empty();
    }
}
