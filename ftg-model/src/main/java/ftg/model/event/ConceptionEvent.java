package ftg.model.event;

import static ftg.model.person.Person.Sex.FEMALE;
import static ftg.model.person.Person.Sex.MALE;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Checked;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConceptionEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(ConceptionEvent.class);

    private final TredecimalDate date;

    private final String fatherId;

    private final String motherId;

    private final Person.Sex childSex;

    ConceptionEvent(String id, TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        super(id, date);
        this.date = date;
        this.fatherId = fatherId;
        this.motherId = motherId;
        this.childSex = childSex;
    }

    public String getFatherId() {
        return fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {
        final Person mother = Checked.argument(world.getPerson(motherId), p -> p.getSex() == FEMALE && p.state(Pregnancy.class).isEmpty(), "Mother should be non-pregnant female");
        final Person father = Checked.argument(world.getPerson(fatherId), p -> p.getSex() == MALE, "Father should be male");

        LOGGER.info("[{}] {} is pregnant from {}", TredecimalDateFormat.ISO.format(date), mother, father);
        mother.addState(new Pregnancy(date, father, childSex));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conceptionDate", date)
                .add("fatherId", fatherId)
                .add("motherId", motherId)
                .add("childSex", childSex)
                .toString();
    }
}
