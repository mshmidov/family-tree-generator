package ftg.model.world;

import ftg.model.person.Person;

import java.util.List;
import java.util.Set;

public interface World {

    void submitEvent(Event event);

    List<Event> getEvents();

    void addPerson(Person person);

    Person getPerson(String id);

    List<Person> getPersons();

    Set<Person> getLivingPersons();

    Set<Person> getLivingMales();

    Set<Person> getLivingFemales();

    Set<Person> getDeadPersons();
}
