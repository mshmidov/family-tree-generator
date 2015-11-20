package ftg.model.event;

import static ftg.model.time.TredecimalDateInterval.intervalBetween;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Checked;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.Marriage;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DeathEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    private final String deceasedId;

    DeathEvent(String id, TredecimalDate date, String deceasedId) {
        super(id, date);
        this.deceasedId = deceasedId;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person deceased = Checked.argument(world.getPerson(deceasedId), p -> p.state(Death.class).isEmpty(),
                                                 "With strange aeons even death may die. But this is not the case ;)");

        deceased.addState(new Death(getDate()));

        deceased.relations(Marriage.class).forEach(marriage -> {
            marriage.remove();
            relationFactory.createWidowhood(deceased, marriage.getSpouse(deceased));
        });


        LOGGER.info("[{}] {} dies at age of {}", TredecimalDateFormat.ISO.format(getDate()), deceased,
                    intervalBetween(getDate(), deceased.getBirthDate()).getYears());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue("[" + getId() + "," + getDate() + "]")
            .add("deceasedId", deceasedId)
            .toString();
    }
}
