package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.event.PersonData;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.simulation.configuration.Country;

public final class RandomModel {

    private final RandomChoice randomChoice = new RandomChoice();

    private final EventFactory eventFactory;

    public RandomModel(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public PersonData newPersonData(Country country, Surname surname, IntegerRange age, TredecimalDate currentDate) {

        final Person.Sex sex = randomChoice.from(Person.Sex.class);
        final int days = randomChoice.between(age.getFirst() * DAYS_IN_YEAR, age.getLast() * DAYS_IN_YEAR);


        return eventFactory.newPersonData(
            country.getNamingSystem().getNames(sex).get(),
            surname,
            sex,
            currentDate.minusDays(days),
            new Residence(country.getName()));
    }

}
