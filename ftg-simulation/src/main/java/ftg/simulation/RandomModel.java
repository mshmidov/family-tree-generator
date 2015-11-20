package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.max;

import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Pregnancy;
import ftg.model.time.TredecimalCalendar;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.model.world.country.Country;

public final class RandomModel {

    private final EventFactory eventFactory;

    public RandomModel(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public void introduceRandomFamily(World world, Country country, Surname surname, TredecimalDate currentDate) {

        // baseline adults

        final IntegerRange baselineGeneration = IntegerRange.inclusive(25, 40);

        final Person man = world.submitEvent(new RandomPerson(country).surname(surname).sex(Person.Sex.MALE).randomAge(baselineGeneration, currentDate)
                                                 .introduce(currentDate, eventFactory));

        final Person woman = world.submitEvent(new RandomPerson(country).surname(surname).sex(Person.Sex.FEMALE).randomAge(baselineGeneration, currentDate)
                                                   .introduce(currentDate, eventFactory));

        final TredecimalDate husbandAdulthood = man.getBirthDate().plusYears(17);
        final TredecimalDate wifeAdulthood = woman.getBirthDate().plusYears(17);
        final TredecimalDate marriageDate = max(husbandAdulthood, wifeAdulthood);
        world.submitEvent(eventFactory.newMarriageEvent(marriageDate, man.getId(), woman.getId()));

        // children

        for (long i = 0; i <= (currentDate.getYear() - marriageDate.getYear()); i++) {
            final TredecimalDate year = marriageDate.plusYears(i);

            if (RandomChoice.byChance(0.9)) {
                final TredecimalDate conceptionDate = RandomChoice.between(year, year.plusDays(TredecimalCalendar.DAYS_IN_YEAR - 310));

                final Pregnancy pregnancy = world.submitEvent(
                    eventFactory.newConceptionEvent(conceptionDate, man.getId(), woman.getId(), RandomChoice.from(Person.Sex.class)));

                world.submitEvent(
                    eventFactory.newBirthEvent(conceptionDate.plusDays(280), woman.getId(), country.getNativeNames().childName(woman, pregnancy), surname));
            }
        }
    }
}
