package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.World;
import ftg.model.person.Person;
import ftg.model.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.model.person.Person.Sex.FEMALE;
import static ftg.model.person.Person.Sex.MALE;

public final class ConceptionEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(ConceptionEvent.class);

    private final TredecimalDate conceptionDate;

    private final Person father;

    private final Person mother;

    private final Person.Sex childSex;

    public ConceptionEvent(TredecimalDate conceptionDate, Person father, Person mother, Person.Sex childSex) {
        this.conceptionDate = checkNotNull(conceptionDate);
        this.father = checkedArgument(father, p -> p.getSex() == MALE, "Father should be male");
        this.mother = checkedArgument(mother, p -> p.getSex() == FEMALE && !p.hasState(Pregnancy.class), "Mother should be non-pregnant female");
        this.childSex = checkNotNull(childSex);
    }

    public TredecimalDate getConceptionDate() {
        return conceptionDate;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }

    @Override
    public void apply(World world) {
        LOGGER.info("{} is pregnant from {}", mother, father);
        mother.getStates().add(new Pregnancy(conceptionDate, father, childSex));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conceptionDate", TredecimalDateFormat.ISO.format(conceptionDate))
                .add("father", father)
                .add("mother", mother)
                .add("childSex", childSex)
                .toString();
    }
}
