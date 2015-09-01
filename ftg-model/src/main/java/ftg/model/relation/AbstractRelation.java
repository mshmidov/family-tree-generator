package ftg.model.relation;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import ftg.model.person.Person;

import java.util.List;
import java.util.Objects;
import java.util.Set;

abstract class AbstractRelation implements Relation {

    private final ImmutableSet<Role> possibleRoles;
    protected final ImmutableBiMap<Role, Person> participants;

    protected AbstractRelation(Role r1, Person p1, Role r2, Person p2) {
        this.possibleRoles = ImmutableSet.of(r1, r2);
        this.participants = ImmutableBiMap.of(r1, r1.check(p1), r2, r2.check(p2));
    }

    protected AbstractRelation(Role r1, Person p1, Role r2, Person p2, Role r3, Person p3) {
        this.possibleRoles = ImmutableSet.of(r1, r2, r3);
        this.participants = ImmutableBiMap.of(r1, r1.check(p1), r2, r2.check(p2), r3, r3.check(p3));
    }

    @Override
    public final Set<Role> possibleRoles() {
        return possibleRoles;
    }

    @Override
    public final Set<Person> participants() {
        return ImmutableSet.copyOf(participants.values());
    }

    @Override
    public final Role getRole(Person person) {
        if (participants.containsValue(person)) {
            return participants.inverse().get(person);
        }

        throw new IllegalArgumentException(String.format("%s does not participate in %s", person, this.getClass().getSimpleName()));
    }

    @Override
    public Person getParticipant(Role role) {
        if (possibleRoles.contains(role)) {
            return participants.get(role);
        }

        throw new IllegalArgumentException(String.format("%s is not valid role for %s", role, this.getClass().getSimpleName()));
    }

    @Override
    public List<Participant> getParticipants() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRelation that = (AbstractRelation) o;
        return Objects.equals(possibleRoles, that.possibleRoles) &&
                Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleRoles, participants);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("participants", participants)
                .toString();
    }
}
