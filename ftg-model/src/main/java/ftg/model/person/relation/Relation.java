package ftg.model.person.relation;

import ftg.model.person.Person;
import javaslang.collection.Set;

public interface Relation {

    Set<Role> roles();

    Set<Person> participants();

    Role getRole(Person person);

    Person getParticipant(Role role);
}
