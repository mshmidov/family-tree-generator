package ftg.model.relation;

import com.google.common.base.MoreObjects;
import ftg.commons.functional.Checked;
import ftg.commons.functional.ReverseTuple;
import ftg.model.person.Person;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Set;

import java.util.Objects;
import java.util.function.Predicate;

abstract class AbstractRelation implements Relation {

    private static final Predicate<Seq<Person>> NO_DUPLICATES = values -> values.distinct().equals(values);

    private final Map<Role, Person> participants;
    private final Map<Person, Role> reverseParticipants;

    protected AbstractRelation(Map<Role, Person> participants) {
        Checked.argument(participants.values(), NO_DUPLICATES, "Person cannot be related to himself");

        this.participants = participants;
        this.reverseParticipants = HashMap.ofAll(participants.map(ReverseTuple::of2));
    }

    @Override
    public final Set<Role> roles() {
        return participants.keySet();
    }

    @Override
    public final Set<Person> participants() {
        return reverseParticipants.keySet();
    }

    @Override
    public final Role getRole(Person person) {
        return reverseParticipants.get(person)
            .orElseThrow(() -> new IllegalArgumentException(String.format("%s does not participate in %s", person, this.getClass().getSimpleName())));
    }

    @Override
    public Person getParticipant(Role role) {
        return participants.get(role)
            .orElseThrow(() -> new IllegalArgumentException(String.format("%s is not valid role for %s", role, this.getClass().getSimpleName())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(participants);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractRelation that = (AbstractRelation) o;
        return Objects.equals(participants, that.participants);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("participants", participants)
                .toString();
    }
}
