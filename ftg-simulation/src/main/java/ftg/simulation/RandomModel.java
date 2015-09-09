package ftg.simulation;

import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.simulation.configuration.Country;

import java.util.Random;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public final class RandomModel {

    private final Random random = new Random();

    private final RandomChoice randomChoice;

    @Inject
    public RandomModel(RandomChoice randomChoice) {
        this.randomChoice = randomChoice;
    }


    public Person newPerson(Country country, Surname surname, IntegerRange age, TredecimalDate currentDate) {

        final Person.Sex sex = randomChoice.from(Person.Sex.class);
        final int days = randomChoice.between(age.getFirst() * DAYS_IN_YEAR, age.getLast() * DAYS_IN_YEAR);

        final Person newPerson = new Person(
                country.getNamingSystem().getNames(sex).get(),
                surname,
                sex,
                currentDate.minusDays(days));

        newPerson.addState(new Residence(country.getName()));

        return newPerson;
    }

}
