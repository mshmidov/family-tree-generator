package ftg.model.world;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class MarriageEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(MarriageEvent.class);

    private final TredecimalDate date;

    private final String husbandId;

    private final String wifeId;

    public MarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        this.date = date;
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
    }

    @Override
    public void apply(World world) {

        final Person husband = world.getPerson(husbandId);
        final Person wife = world.getPerson(wifeId);

        checkArgument(husband.getSex() == Person.Sex.MALE, "Husband should be male");
        checkArgument(wife.getSex() == Person.Sex.FEMALE, "Wife should be female");

        checkState(!husband.hasRelation(Marriage.class), "Person can participate in only one marriage at a time");
        checkState(!wife.hasRelation(Marriage.class), "Person can participate in only one marriage at a time");

        Marriage.create(husband, wife);
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
