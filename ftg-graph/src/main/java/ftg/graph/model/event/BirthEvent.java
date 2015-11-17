package ftg.graph.model.event;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.Operations;
import ftg.graph.db.Queries;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonFactory;
import ftg.graph.model.person.Woman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.annotation.Property;

public final class BirthEvent extends Event<Person> {

    private static final Logger LOGGER = LogManager.getLogger(BirthEvent.class);

    @Property
    private PersonData childData;

    @Property
    private String motherId;

    @Property
    private String fatherId;

    public BirthEvent() {
    }

    BirthEvent(String id, String namespace, TredecimalDate date, PersonData childData, String motherId, String fatherId) {
        super(id, namespace, date);
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
    public Person apply(SimulatedWorld world, PersonFactory personFactory) {

        final Queries queries = world.getQueries();
        final Operations operations = world.getOperations();

        final Woman mother = queries.getWoman(motherId);
        final Man father = queries.getMan(fatherId);

        final Person child = personFactory.newPerson(childData, operations.getOrCreateFamily(childData.getSurname()));
        child.setFather(father);
        child.setMother(mother);
        child.setCountryOfResidence(queries.getCountry(childData.getResidence()));

        final Person savedChild = operations.createPerson(child);

        father.getChildren().add(savedChild);
        mother.getChildren().add(savedChild);

        operations.save(father, mother);
        operations.removePregnancy(mother);

        LOGGER.info("[{}] {} is born of {} and {}", TredecimalDateFormat.ISO.format(getDate()), child, father, mother);
        return child;
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
