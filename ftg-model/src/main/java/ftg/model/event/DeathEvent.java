package ftg.model.event;

import static ftg.model.time.TredecimalDateInterval.intervalBetween;

import ftg.commons.functional.Checked;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.relation.Marriage;
import ftg.model.relation.RelationFactory;
import ftg.model.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DeathEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    private final TredecimalDate date;

    private final String deceasedId;

    DeathEvent(String id, TredecimalDate date, String deceasedId) {
        super(id, date);
        this.date = date;
        this.deceasedId = deceasedId;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person deceased = Checked.argument(world.getPerson(deceasedId), p -> p.state(Death.class).isEmpty(),
                                                 "With strange aeons even death may die. But this is not the case ;)");

        deceased.addState(new Death(date));

        deceased.relations(Marriage.class).forEach(marriage -> {
            marriage.remove();
            relationFactory.createWidowhood(deceased, marriage.getOther(deceased));
        });


        LOGGER.info("[{}] {} dies at age of {}", TredecimalDateFormat.ISO.format(date), deceased, intervalBetween(date, deceased.getBirthDate()).getYears());
    }
}
