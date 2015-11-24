package ftg.simulation;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.event.MarriageEvent;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import ftg.model.world.country.Country;
import ftg.simulation.lineage.Lineages;
import javaslang.collection.HashSet;
import javaslang.collection.Set;
import javaslang.collection.Vector;

final class Matchmaker {

    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final EventFactory eventFactory;

    private final Lineages lineages;

    public Matchmaker(EventFactory eventFactory, Lineages lineages) {
        this.eventFactory = eventFactory;
        this.lineages = lineages;
    }

    public Set<MarriageEvent> decide(long year, Country country, Set<Person> males, Set<Person> females) {
        Set<MarriageEvent> result = HashSet.empty();
        final TredecimalDate zeroDay = new TredecimalDate(year, 0);

        // marriages
        final Set<Person> grooms = males
            .filter(man -> fertileAge.includes(man.getAge(zeroDay).getYears()));

        Set<Person> brides = females
            .filter(woman -> fertileAge.includes(woman.getAge(zeroDay).getYears()));

        final double chance = 60D / 1000D;

        for (Person groom : grooms) {
            if (RandomChoice.byChance(chance)) {

                final Vector<Person> candidates = brides
                    .filter(f -> lineages.isDirectAncestor(groom, f).isEmpty()) // male is not descendant of female
                    .filter(f -> lineages.isDirectAncestor(f, groom).isEmpty()) // female is not descendant of male
                    .filter(f -> lineages.findClosestRelation(groom, f, 2).orElse(2) >= 2) // no siblings
                    .toVector();

                if (candidates.isDefined()) {
                    int index = RandomChoice.fromRangeByGaussian(candidates.length());
                    final Person bride = candidates.get(index);
                    brides = brides.remove(bride);

                    final TredecimalDate date = RandomChoice.between(zeroDay, zeroDay.plusDays(DAYS_IN_YEAR));
                    result = result.add(eventFactory.newMarriageEvent(date, groom.getId(), bride.getId()));
                }
            }
        }

        return result;
    }
}
