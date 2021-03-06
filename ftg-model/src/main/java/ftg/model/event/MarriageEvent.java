package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Checked;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.Marriage;
import ftg.model.person.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import javaslang.collection.Array;
import javaslang.collection.Set;

public final class MarriageEvent extends Event<Marriage> {

    private final String husbandId;
    private final String wifeId;

    MarriageEvent(String id, TredecimalDate date, String husbandId, String wifeId) {
        super(id, date);
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }

    public String getHusbandId() {
        return husbandId;
    }

    public String getWifeId() {
        return wifeId;
    }

    @Override
    public Marriage apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {

        final Person husband = world.getPerson(husbandId);
        final Person wife = world.getPerson(wifeId);

        Checked.argument(husband.getSex(), Person.Sex.MALE::equals, "Husband should be male");
        Checked.argument(wife.getSex(), Person.Sex.FEMALE::equals, "Wife should be female");

        Array.ofAll(husband, wife).forEach(person -> Checked.argument(person.relations(Marriage.class), Set::isEmpty,
                                                                      "Person can participate in only one marriage at a time"));

        wife.setSurname(husband.getSurnameObject());
        return relationFactory.createMarriage(husband, wife);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue("[" + getId() + "," + getDate() + "]")
            .add("husbandId", husbandId)
            .add("wifeId", wifeId)
            .toString();
    }
}
