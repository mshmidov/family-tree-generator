package ftg.model.world;

import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import ftg.model.relation.Widowhood;
import ftg.model.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ftg.commons.MorePreconditions.checked;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;

public final class DeathEvent implements Event {

    private static final Logger LOGGER = LogManager.getLogger(DeathEvent.class);

    private final TredecimalDate date;

    private final String deceasedId;

    public DeathEvent(TredecimalDate date, String deceasedId) {
        this.date = date;
        this.deceasedId = deceasedId;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
    }

    public String getDeceasedId() {
        return deceasedId;
    }

    @Override
    public void apply(World world) {
        final Person deceased = checked(world.getPerson(deceasedId), p -> !p.hasState(Death.class), IllegalArgumentException::new);

        deceased.getRelations().getSingle(Marriage.class).ifPresent(marriage -> {
            marriage.remove();
            Widowhood.create(marriage.getHusband(), marriage.getWife());
        });

        deceased.addState(new Death(date));

        LOGGER.info("[{}] {} dies at age of {}", TredecimalDateFormat.ISO.format(date), deceased, intervalBetween(date, deceased.getBirthDate()).getYears());
    }
}