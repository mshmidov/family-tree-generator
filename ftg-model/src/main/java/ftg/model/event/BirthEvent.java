package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.Surname;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BirthEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(BirthEvent.class);

    private final String motherId;
    private final String childName;
    private final Surname childSurname;
    private final Person.Sex childSex;

    BirthEvent(String id, TredecimalDate birthDate, String motherId, String childName, Surname childSurname, Person.Sex childSex) {
        super(id, birthDate);
        this.motherId = motherId;
        this.childName = childName;
        this.childSurname = childSurname;
        this.childSex = childSex;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person mother = world.getPerson(motherId);
        final Person father = mother.state(Pregnancy.class).get().getFather();

        mother.removeState(Pregnancy.class);
        final Person child = personFactory.newPerson(childName, childSurname, childSex, getDate(), mother.state(Residence.class).get());
        relationFactory.createParentage(father, mother, child);

        world.addPerson(child);

        LOGGER.info("[{}] {} is born of {} and {}", TredecimalDateFormat.ISO.format(getDate()), child, father, mother);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue("[" + getId() + "," + getDate() + "]")
            .add("motherId", motherId)
            .add("name", childName)
            .add("surname", childSurname)
            .add("sex", childSex)
            .toString();
    }
}
