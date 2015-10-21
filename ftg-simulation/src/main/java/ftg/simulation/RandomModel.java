package ftg.simulation;

import static ftg.commons.time.TredecimalCalendar.DAYS_IN_YEAR;

import ftg.commons.range.IntegerRange;
import ftg.commons.time.TredecimalDate;
import ftg.graph.model.DomainObjectFactory;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonData;
import ftg.graph.model.person.Surname;
import ftg.simulation.configuration.SimulatedCountry;

public final class RandomModel {

    private final RandomChoice randomChoice = new RandomChoice();

    private final DomainObjectFactory domainObjectFactory;

    public RandomModel(DomainObjectFactory domainObjectFactory) {
        this.domainObjectFactory = domainObjectFactory;
    }

    public PersonData newPersonData(SimulatedCountry simulatedCountry, Surname surname, IntegerRange age, TredecimalDate currentDate) {

        final Person.Sex sex = randomChoice.from(Person.Sex.class);
        final int days = randomChoice.between(age.getFirst() * DAYS_IN_YEAR, age.getLast() * DAYS_IN_YEAR);


        return domainObjectFactory.newPersonData(
            simulatedCountry.getNamingSystem().getNames(sex).get(),
            surname,
            sex,
            currentDate.minusDays(days),
            simulatedCountry.getName());
    }

}
