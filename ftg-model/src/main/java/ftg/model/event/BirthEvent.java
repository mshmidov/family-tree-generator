package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.Surname;
import ftg.model.person.relation.Parentage;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;

public final class BirthEvent extends Event<Parentage> {

    private final String motherId;
    private final String childName;
    private final Surname childSurname;

    BirthEvent(String id, TredecimalDate birthDate, String motherId, String childName, Surname childSurname) {
        super(id, birthDate);
        this.motherId = motherId;
        this.childName = childName;
        this.childSurname = childSurname;
    }

    @Override
    public Parentage apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person mother = world.getPerson(motherId);
        final Pregnancy pregnancy = mother.state(Pregnancy.class).get();
        final Person father = pregnancy.getFather();

        mother.removeState(Pregnancy.class);
        final Person child = personFactory.newPerson(childName, childSurname, pregnancy.getChildSex(), getDate(), mother.state(Residence.class).get());

        return relationFactory.createParentage(father, mother, child);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue("[" + getId() + "," + getDate() + "]")
            .add("motherId", motherId)
            .add("name", childName)
            .add("surname", childSurname)
            .toString();
    }
}
