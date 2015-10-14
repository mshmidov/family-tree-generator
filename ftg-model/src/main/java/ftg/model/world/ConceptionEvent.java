package ftg.model.world;

import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.model.person.Person.Sex.FEMALE;
import static ftg.model.person.Person.Sex.MALE;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConceptionEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(ConceptionEvent.class);

    private final String id;

    private final TredecimalDate date;

    private final String fatherId;

    private final String motherId;

    private final Person.Sex childSex;

    public ConceptionEvent(String id, TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        this.id = id;
        this.date = date;
        this.fatherId = fatherId;
        this.motherId = motherId;
        this.childSex = childSex;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
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
    public void apply(World world) {
        final Person mother = checkedArgument(world.getPerson(motherId), p -> p.getSex() == FEMALE && !p.hasState(Pregnancy.class), "Mother should be non-pregnant female");
        final Person father = checkedArgument(world.getPerson(fatherId), p -> p.getSex() == MALE, "Father should be male");

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
