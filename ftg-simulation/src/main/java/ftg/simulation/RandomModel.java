package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.event.PersonIntroductionEvent;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.time.TredecimalDate;
import ftg.model.world.country.Country;

public final class RandomModel {

    private final EventFactory eventFactory;

    public RandomModel(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public PersonIntroductionEvent introduceRandomPerson(Country country, Surname surname, IntegerRange age, TredecimalDate currentDate) {
        final Person.Sex sex = RandomChoice.from(Person.Sex.class);
        final int days = RandomChoice.between(age.getFirst() * DAYS_IN_YEAR, age.getLast() * DAYS_IN_YEAR);

        return eventFactory.newPersonIntroductionEvent(currentDate,
                                                       country.getNativeNames().randomNames(sex).get(),
                                                       surname,
                                                       sex,
                                                       currentDate.minusDays(days),
                                                       country);
    }

}
