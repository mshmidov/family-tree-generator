package ftg.graph.model.event;

import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.commons.time.TredecimalDateInterval.intervalBetween;

import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.annotation.Property;

public final class DeathEvent extends Event<Person> {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    @Property
    private String deceasedId;

    public DeathEvent() {
    }

    DeathEvent(String id, String namespace, TredecimalDate date, String deceasedId) {
        super(id, namespace, date);
        this.deceasedId = deceasedId;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public Person apply(SimulatedWorld world, PersonFactory personFactory) {
        Person deceased = checkedArgument(world.getQueries().getPerson(deceasedId), Person::isAlive, "Only alive person can die");

        final Person spouse = deceased.getSpouse();
        if (spouse != null) {
            deceased.setSpouse(null);
            spouse.setSpouse(null);
            world.getOperations().save(spouse);
        }

        deceased.setAlive(false);

        world.getOperations().save(deceased);

        LOGGER.info("[{}] {} dies at age of {}",
            TredecimalDateFormat.ISO.format(getDate()),
            deceased,
            intervalBetween(getDate(), deceased.getBirthDate()).getYears());

        return deceased;
    }
}
