package ftg.graph.model.event;

import static ftg.commons.MorePreconditions.checkedArgument;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.Queries;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonFactory;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Woman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.annotation.Property;

public final class ConceptionEvent extends Event<Pregnancy> {

    private static final Logger LOGGER = LogManager.getLogger(ConceptionEvent.class);

    @Property
    private String fatherId;

    @Property
    private String motherId;

    @Property
    private Person.Sex childSex;

    public ConceptionEvent() {
    }

    ConceptionEvent(String id, String namespace, TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        super(id, namespace, date);
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
    public Pregnancy apply(SimulatedWorld world, PersonFactory personFactory) {
        final Queries queries = world.getQueries();

        final Woman mother = checkedArgument(queries.getWoman(motherId), p -> p.getPregnancy() == null, "Mother should be non-pregnant");
        final Man father = queries.getMan(fatherId);

        LOGGER.info("[{}] {} is pregnant from {}", TredecimalDateFormat.ISO.format(getDate()), mother, father);

        mother.setPregnancy(personFactory.newPregnancy(getDate(), father, childSex));

        world.getOperations().save(mother, father);

        return mother.getPregnancy();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conceptionDate", getDate())
                .add("fatherId", fatherId)
                .add("motherId", motherId)
                .add("childSex", childSex)
                .toString();
    }
}
