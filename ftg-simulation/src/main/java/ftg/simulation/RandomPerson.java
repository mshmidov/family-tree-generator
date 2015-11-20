package ftg.simulation;

import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.event.PersonIntroductionEvent;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.time.TredecimalCalendar;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateRange;
import ftg.model.world.country.Country;

import java.util.function.Supplier;

public final class RandomPerson {

    private final Country country;

    private Supplier<String> name;
    private Supplier<Surname> surname;
    private Supplier<Person.Sex> sex;
    private Supplier<TredecimalDate> birthDate;

    public RandomPerson(Country country) {
        this.country = country;
        randomName();
        randomSurname();
        randomSex();
    }

    public RandomPerson name(String name) {
        this.name = () -> name;
        return this;
    }

    public RandomPerson randomName() {
        this.name = () -> country.getNativeNames().randomNames(getSexSupplier().get()).get();
        return this;
    }

    public RandomPerson surname(Surname surname) {
        this.surname = () -> surname;
        return this;
    }

    public RandomPerson randomSurname() {
        this.surname = () -> country.getNativeNames().randomSurnames().get();
        return this;
    }

    public RandomPerson sex(Person.Sex sex) {
        this.sex = () -> sex;
        return this;
    }

    public RandomPerson randomSex() {
        this.sex = () -> RandomChoice.from(Person.Sex.class);
        return this;
    }

    public RandomPerson birthDate(TredecimalDate date) {
        this.birthDate = () -> date;
        return this;
    }

    public RandomPerson randomBirthDate(TredecimalDateRange range) {
        this.birthDate = () -> RandomChoice.from(range);
        return this;
    }

    public RandomPerson randomAge(IntegerRange range, TredecimalDate origin) {
        this.birthDate = () -> origin.minusDays(RandomChoice.between(
            range.getFirst() * TredecimalCalendar.DAYS_IN_YEAR,
            range.getLast() * TredecimalCalendar.DAYS_IN_YEAR));

        return this;
    }

    public PersonIntroductionEvent introduce(TredecimalDate date, EventFactory eventFactory) {
        return eventFactory.newPersonIntroductionEvent(date,
                                                       name.get(),
                                                       surname.get(),
                                                       sex.get(),
                                                       birthDate.get(),
                                                       country);
    }

    private Supplier<Person.Sex> getSexSupplier() { return sex; }
}
