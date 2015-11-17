package ftg.model.event;

import static com.google.common.base.Preconditions.checkArgument;
import static ftg.commons.MorePreconditions.shouldNotPresent;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.relation.Marriage;
import ftg.model.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
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

        checkArgument(husband.getSex() == Person.Sex.MALE, "Husband should be male");
        checkArgument(wife.getSex() == Person.Sex.FEMALE, "Wife should be female");

        shouldNotPresent(husband.relations(Marriage.class).findAny(), () -> new IllegalStateException("Person can participate in only one marriage at a time"));
        shouldNotPresent(wife.relations(Marriage.class).findAny(), () -> new IllegalStateException("Person can participate in only one marriage at a time"));

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
