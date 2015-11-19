package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BirthEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(BirthEvent.class);

    private final PersonData childData;

    private final String motherId;

    private final String fatherId;

    BirthEvent(String id, PersonData childData, String motherId, String fatherId) {
        super(id, childData.getBirthDate());
        this.childData = childData;
        this.motherId = motherId;
        this.fatherId = fatherId;
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
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person mother = world.getPerson(motherId);
        final Person father = world.getPerson(fatherId);

        mother.removeState(Pregnancy.class);
        final Person child = personFactory.newPerson(childData);
        relationFactory.createParentage(father, mother, child);
        mother.state(Residence.class).peek(child::addState);

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
