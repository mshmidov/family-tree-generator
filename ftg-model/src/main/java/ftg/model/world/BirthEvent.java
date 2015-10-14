package ftg.model.world;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonData;
import ftg.model.relation.Parentage;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BirthEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(BirthEvent.class);

    private final String id;

    private final PersonData childData;

    private final String motherId;

    private final String fatherId;

    public BirthEvent(String id, PersonData childData, String motherId, String fatherId) {
        this.id = id;
        this.childData = childData;
        this.motherId = motherId;
        this.fatherId = fatherId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TredecimalDate getDate() {
        return childData.getBirthDate();
    }

    public PersonData getChildData() {
        return childData;
    }

    public String getMotherId() {
        return motherId;
    }

    public String getFatherId() {
        return fatherId;
    }

    @Override
    public void apply(World world) {

        final Person mother = world.getPerson(motherId);
        final Person father = world.getPerson(fatherId);

        mother.removeState(Pregnancy.class);
        final Person child = childData.newPerson();
        Parentage.create(father, mother, child);
        child.addState(mother.getState(Residence.class));

        world.addPerson(child);

        LOGGER.info("[{}] {} is born of {} and {}", TredecimalDateFormat.ISO.format(getDate()), child, father, mother);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("childData", childData)
                .add("motherId", motherId)
                .add("fatherId", fatherId)
                .toString();
    }
}
