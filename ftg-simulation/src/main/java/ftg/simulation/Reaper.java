package ftg.simulation;

import static ftg.model.person.PersonUtil.ALIVE;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;

import ftg.model.event.DeathEvent;
import ftg.model.event.EventFactory;
import ftg.model.person.Person;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.country.Country;
import javaslang.control.Option;

final class Reaper {

    private final EventFactory eventFactory;

    public Reaper(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public Option<DeathEvent> decide(long year, Person person) {
        if (!ALIVE.test(person)) {
            return Option.none();
        }

        final TredecimalDate zeroDay = new TredecimalDate(year, 0);
        final long age = intervalBetween(person.getBirthDate(), zeroDay).getYears();
        final Country country = person.state(Residence.class).get().getCountry();

        final double chance = 1 / country.getDemography().getDeathRisk(age, person.getSex());

        return RandomChoice.byChance(chance)
               ? Option.of(eventFactory.newDeathEvent(RandomChoice.between(zeroDay, zeroDay.plusDays(DAYS_IN_YEAR)), person.getId()))
               : Option.none();
    }

}
