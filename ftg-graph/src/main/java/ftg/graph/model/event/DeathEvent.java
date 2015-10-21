package ftg.graph.model.event;

import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.commons.time.TredecimalDateInterval.intervalBetween;

import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.annotation.Property;

public final class DeathEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    @Property
    private String deceasedId;

    public DeathEvent() {
    }

    public DeathEvent(TredecimalDate date, String deceasedId) {
        super(date);
        this.deceasedId = deceasedId;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public void apply(SimulatedWorld world) {
        Person deceased = checkedArgument(world.getQueries().getPerson(deceasedId), Person::isAlive);

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
    }
}
