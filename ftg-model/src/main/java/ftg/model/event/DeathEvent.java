package ftg.model.event;

import static ftg.commons.MorePreconditions.checked;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;

import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.relation.Marriage;
import ftg.model.relation.Widowhood;
import ftg.model.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DeathEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    private final String id;

    private final TredecimalDate date;

    private final String deceasedId;

    DeathEvent(String id, TredecimalDate date, String deceasedId) {
        this.id = id;
        this.date = date;
        this.deceasedId = deceasedId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public void apply(World world, PersonFactory personFactory) {

        final Person deceased = checked(world.getPerson(deceasedId), p -> !p.state(Death.class).isPresent(), IllegalArgumentException::new);

        deceased.relations(Marriage.class).forEach(marriage -> {
                marriage.remove();
                Widowhood.create(marriage.getHusband(), marriage.getWife());
        });

        deceased.addState(new Death(date));

        LOGGER.info("[{}] {} dies at age of {}", TredecimalDateFormat.ISO.format(date), deceased, intervalBetween(date, deceased.getBirthDate()).getYears());
    }
}
