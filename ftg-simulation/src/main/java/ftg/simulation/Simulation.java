package ftg.simulation;

import static ftg.commons.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.commons.time.TredecimalDateInterval.intervalBetween;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import ftg.commons.Util;
import ftg.commons.functional.Either;
import ftg.commons.functional.Operators;
import ftg.commons.functional.Pair;
import ftg.commons.range.IntegerRange;
import ftg.commons.time.TredecimalCalendar;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.Queries;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.event.ConceptionEvent;
import ftg.graph.model.event.DeathEvent;
import ftg.graph.model.event.Event;
import ftg.graph.model.event.EventFactory;
import ftg.graph.model.event.MarriageEvent;
import ftg.graph.model.event.PersonData;
import ftg.graph.model.event.PersonIntroductionEvent;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.simulation.configuration.SimulatedCountry;
import ftg.simulation.configuration.SimulationConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final RandomChoice randomChoice = new RandomChoice();
    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final SimulationConfiguration configuration;

    private final Map<String, SimulatedCountry> countries;

    private TredecimalDate currentDate = new TredecimalDate(0);
    final Multimap<TredecimalDate, Pair<Either<String, PersonData>, Either<String, PersonData>>> marriagePlan = HashMultimap.create();

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
        world.newDay();

        if (currentDate.isZeroDay()) {
            countries.values().stream().
                map(country -> getMarriagePlan(world, country, eventFactory))
                .forEach(marriagePlan::putAll);
        }

        final List<Event> events = new ArrayList<>();

        // marriages
        final Function<String, Person> getPerson = id -> world.getQueries().getPerson(id);
        final Function<PersonData, Person> introducePerson = data -> {
            final PersonIntroductionEvent event = eventFactory.newPersonIntroductionEvent(currentDate, data);
            events.add(event);
            return world.submitEvent(event);
        };

        world.submitEvents(
            marriagePlan.removeAll(currentDate).stream()
                .map(pair -> pair.map(
                    e -> e.map(getPerson, introducePerson),
                    e -> e.map(getPerson, introducePerson)))
                .filter(pair -> pair.map(Person::isAlive, Person::isAlive).reduce(Operators.and()))
                .map(pair -> pair.map(Person::getId, Person::getId))
                .map(pair -> eventFactory.newMarriageEvent(currentDate, pair.getLeft(), pair.getRight()))
                .peek(events::add)
                .collect(Collectors.toList()));

        // pregnancies
        final long fertileBornAfter = currentDate.getDayOfEpoch() - fertileAge.getLast();
        final long fertileBornBefore = currentDate.getDayOfEpoch() - fertileAge.getFirst();

        world.submitEvents(
            world.getQueries().getMarriedNonPregnantWomen(fertileBornAfter, fertileBornBefore).stream()
                .map(woman -> decidePregnancyInMarriage(eventFactory, woman))
                .flatMap(Util::streamFromOptional)
                .peek(events::add)
                .collect(Collectors.toList()));

        // births
        final long conceptionAfter = currentDate.getDayOfEpoch() - 280;
        final long conceptionBefore = currentDate.getDayOfEpoch() - 280;

        world.submitEvents(
            world.getQueries().getPregnantWomen(conceptionAfter, conceptionBefore).stream()
                .map(woman -> decideBirth(eventFactory, woman))
                .peek(events::add)
                .collect(Collectors.toList()));

        // deaths
        world.submitEvents(
            world.getQueries().getLivingPersons().stream()
                .map(person -> decideDeath(eventFactory, person))
                .flatMap(Util::streamFromOptional)
                .peek(events::add)
                .collect(Collectors.toList()));

        return events;
    }

    private Multimap<TredecimalDate, Pair<Either<String, PersonData>, Either<String, PersonData>>> getMarriagePlan(SimulatedWorld world,
        SimulatedCountry country,
        EventFactory eventFactory) {

        final Multimap<TredecimalDate, Pair<Either<String, PersonData>, Either<String, PersonData>>> marriagePlan = HashMultimap.create();

        // marriages
        final List<Man> men = world.getQueries().getUnmarriedMen()
            .filter(man -> fertileAge.includes(man.getAgeAt(currentDate).getYears()))
            .sorted((m1, m2) -> {
                        final int children = Integer.compare(m1.getChildren().size(), m2.getChildren().size());
                        return (children != 0)
                               ? children
                               : Long.compare(m1.getAgeAt(currentDate).getYears(), m2.getAgeAt(currentDate).getYears());
                    }
            ).collect(Collectors.toList());


        final List<Woman> women = world.getQueries().getUnmarriedWomen()
            .filter(man -> fertileAge.includes(man.getAgeAt(currentDate).getYears()))
            .sorted((w1, w2) -> {
                        final int children = Integer.compare(w1.getChildren().size(), w2.getChildren().size());
                        return (children != 0)
                               ? children
                               : Long.compare(w1.getAgeAt(currentDate).getYears(), w2.getAgeAt(currentDate).getYears());
                    }
            ).collect(Collectors.toList());


        final double chance = 60D / 1000D;
        final RandomModel randomModel = new RandomModel(eventFactory);

        for (Man man : men) {
            if (randomChoice.byChance(chance)) {

                final List<Person> candidates = women.stream()
                    .filter(f -> !world.getQueries().haveDirectAncestry(man, f))
                    .filter(f -> !world.getQueries().haveCommonAncestor(man, f, 2))
                    .collect(Collectors.toList());

                if (candidates.isEmpty()) {
                    marriagePlan.put(currentDate.plusDays(randomChoice.between(0, TredecimalCalendar.DAYS_IN_YEAR)),
                                     Pair.of(Either.ofLeft(man.getId()),
                                             Either.ofRight(randomModel.newPersonData(country, country.getNamingSystem().getCommonSurnames().get(), fertileAge,
                                                                                      currentDate))));
                } else {
                    int index = randomChoice.fromRangeByGaussian(candidates.size());
                    marriagePlan.put(currentDate.plusDays(randomChoice.between(0, TredecimalCalendar.DAYS_IN_YEAR)),
                                     Pair.of(Either.ofLeft(man.getId()), Either.ofLeft(women.remove(index).getId())));
                }
            }
        }

        return marriagePlan;
    }


    private Optional<MarriageEvent> decideMarriage(EventFactory eventFactory, Queries queries, Person male, List<Woman> unmarriedFemales) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final List<Person> candidates = unmarriedFemales.stream()
                .filter(f -> !queries.haveDirectAncestry(male, f))
                .filter(f -> !queries.haveCommonAncestor(male, f, 2))
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
            pregnancy.getFather().getFamily().getSurname(),
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
