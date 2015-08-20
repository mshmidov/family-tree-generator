package ftg.model.relation;

import ftg.model.Person;

import java.util.Set;

public interface Relation {

    Set<Role> possibleRoles();

    Set<Person> participants();

    Role getRole(Person person);

    Person getParticipant(Role role);
}
