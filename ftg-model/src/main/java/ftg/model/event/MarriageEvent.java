package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Checked;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.Marriage;
import ftg.model.person.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import javaslang.collection.Array;
import javaslang.collection.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MarriageEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(MarriageEvent.class);

    private final TredecimalDate date;

    private final String husbandId;

    private final String wifeId;

    MarriageEvent(String id, TredecimalDate date, String husbandId, String wifeId) {
        super(id, date);
        this.date = date;
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person husband = world.getPerson(husbandId);
        final Person wife = world.getPerson(wifeId);

        Checked.argument(husband.getSex(), Person.Sex.MALE::equals, "Husband should be male");
        Checked.argument(wife.getSex(), Person.Sex.FEMALE::equals, "Wife should be female");

        Array.ofAll(husband, wife).forEach(person -> Checked.argument(person.relations(Marriage.class), Set::isEmpty,
                                                                      "Person can participate in only one marriage at a time"));

        relationFactory.createMarriage(husband, wife);
        LOGGER.info("[{}] {} marries {}", TredecimalDateFormat.ISO.format(date), husband, wife);
        wife.setSurname(husband.getSurnameObject());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("husbandId", husbandId)
                .add("wifeId", wifeId)
                .toString();
    }
}
