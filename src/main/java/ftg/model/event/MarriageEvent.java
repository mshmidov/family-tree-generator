package ftg.model.event;

import ftg.model.person.Person;
import ftg.model.relation.Marriage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class MarriageEvent implements Event {

    private final Person husband;

    private final Person wife;

    public static MarriageEvent createAndApply(Person husband, Person wife) {
        final MarriageEvent event = new MarriageEvent(husband, wife);
        event.apply();
        return event;
    }

    public MarriageEvent(Person husband, Person wife) {
        checkArgument(husband.getSex() == Person.Sex.MALE, "Husband should be male");
        checkArgument(wife.getSex() == Person.Sex.FEMALE, "Wife should be female");
        this.husband = husband;
        this.wife = wife;
    }

    public void apply() {
        checkState(!husband.getRelations().get(Marriage.class).isEmpty(), "Person can participate in only one marriage at a time");
        checkState(!wife.getRelations().get(Marriage.class).isEmpty(), "Person can participate in only one marriage at a time");

        Marriage.create(husband, wife);
    }
}
