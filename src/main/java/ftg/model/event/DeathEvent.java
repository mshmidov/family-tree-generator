package ftg.model.event;

import ftg.model.World;
import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import ftg.model.relation.Widowhood;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ftg.model.time.TredecimalDateInterval.intervalBetween;

public final class DeathEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    private final Person deceased;

    public DeathEvent(Person deceased) {
        this.deceased = deceased;
    }

    @Override
    public void apply(World world) {
        deceased.getStates().removeAll();

        deceased.getRelations().getSingle(Marriage.class).ifPresent(marriage -> {
            marriage.remove();
            Widowhood.create(marriage.getHusband(), marriage.getWife());
        });

        world.killPerson(deceased);

        LOGGER.info("{} dies at age of {}", deceased, intervalBetween(world.getCurrentDate(), deceased.getBirthDate()).getYears());
    }
}
