package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.World;
import ftg.model.person.Person;
import ftg.model.relation.Parentage;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BirthEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(BirthEvent.class);

    private final TredecimalDate date;

    private final Person.Sex childSex;

    private final String childName;

    private final Person mother;

    private final Person father;

    public BirthEvent(TredecimalDate date, Person mother, Pregnancy pregnancy, String childName) {
        this.date = date;
        this.childSex = pregnancy.getChildSex();
        this.mother = mother;
        this.father = pregnancy.getFather();
        this.childName = childName;
    }

    @Override
    public void apply(World world) {
        mother.removeState(Pregnancy.class);
        final Person child = new Person(childName, father.getSurnameObject(), childSex, date);
        Parentage.create(father, mother, child);
        child.addState(mother.getState(Residence.class));
        world.addLivingPerson(child);
        LOGGER.info("{} is born of {} and {}", child, father, mother);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("date", TredecimalDateFormat.ISO.format(date))
                .add("childSex", childSex)
                .add("childName", childName)
                .add("mother", mother)
                .add("father", father)
                .toString();
    }
}
