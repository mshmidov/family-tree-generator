package ftg.graph.model.event;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Pair;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.PersonFactory;
import ftg.graph.model.person.Woman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MarriageEvent extends Event<Pair<Man, Woman>> {

    private static final Logger LOGGER = LogManager.getLogger(MarriageEvent.class);

    private String husbandId;

    private String wifeId;

    MarriageEvent(String id, String namespace, TredecimalDate date, String husbandId, String wifeId) {
        super(id, namespace, date);
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }

    @Override
    public Pair<Man, Woman> apply(SimulatedWorld world, PersonFactory personFactory) {

        final Man husband = world.getQueries().getMan(husbandId);
        final Woman wife = world.getQueries().getWoman(wifeId);

        checkState(husband.getWife() == null, "Person can participate in only one marriage at a time");
        checkState(wife.getHusband() == null, "Person can participate in only one marriage at a time");

        LOGGER.info("[{}] {} marries {}", TredecimalDateFormat.ISO.format(getDate()), husband, wife);

        husband.setWife(wife);
        wife.setHusband(husband);
        wife.setCurrentFamily(husband.getFamily());

        world.getOperations().save(husband, wife);

        return Pair.of(husband, wife);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("husbandId", husbandId)
            .add("wifeId", wifeId)
            .toString();
    }
}
