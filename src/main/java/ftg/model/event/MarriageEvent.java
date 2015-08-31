package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.World;
import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import org.apache.logging.log4j.LogManager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class MarriageEvent implements Event {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MarriageEvent.class);

    private final Person husband;

    private final Person wife;

    public MarriageEvent(Person husband, Person wife) {
        checkArgument(husband.getSex() == Person.Sex.MALE, "Husband should be male");
        checkArgument(wife.getSex() == Person.Sex.FEMALE, "Wife should be female");
        this.husband = husband;
        this.wife = wife;
    }

    @Override
    public void apply(World world) {
        checkState(!husband.hasRelation(Marriage.class), "Person can participate in only one marriage at a time");
        checkState(!wife.hasRelation(Marriage.class), "Person can participate in only one marriage at a time");

        Marriage.create(husband, wife);
        LOGGER.info("{} marries {}", husband, wife);
        wife.setSurname(husband.getSurnameObject());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("husband", husband)
                .add("wife", wife)
                .toString();
    }
}
