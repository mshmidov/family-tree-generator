package ftg.graph.model.event;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Woman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MarriageEvent extends Event {

    private static final Logger LOGGER = LogManager.getLogger(MarriageEvent.class);

    private String husbandId;

    private String wifeId;

    public MarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        super(date);
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }

    @Override
    public void apply(SimulatedWorld world) {

        final Man husband = world.getQueries().getMan(husbandId);
        final Woman wife = world.getQueries().getWoman(wifeId);

        checkState(husband.getWife() == null, "Person can participate in only one marriage at a time");
        checkState(wife.getHusband() == null, "Person can participate in only one marriage at a time");

        LOGGER.info("[{}] {} marries {}", TredecimalDateFormat.ISO.format(getDate()), husband, wife);

        husband.setWife(wife);
        wife.setHusband(husband);
        wife.setCurrentSurname(husband.getSurname());

        world.getOperations().save(husband, wife);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("husbandId", husbandId)
            .add("wifeId", wifeId)
            .toString();
    }
}
