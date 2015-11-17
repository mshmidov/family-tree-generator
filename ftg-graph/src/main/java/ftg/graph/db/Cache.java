package ftg.graph.db;

import com.google.common.collect.ImmutableList;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Woman;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class Cache {

    private final Map<String, Person> allPersons = new HashMap<>();

    private final Map<String, Person> livingPersons = new HashMap<>();

    public Collection<Person> getAllPersons() {
        return ImmutableList.copyOf(allPersons.values());
    }

    public Collection<Person> getLivingPersons() {
        return ImmutableList.copyOf(livingPersons.values());
    }

    public void cachePerson(Person person) {
        allPersons.put(person.getId(), person);
        if (person.isAlive()) {
            livingPersons.put(person.getId(), person);
        } else {
            livingPersons.remove(person.getId());
        }
    }

    public Person getPerson(String id) {
        return allPersons.get(id);
    }

    public Man getMan(String id) {
        return (Man) allPersons.get(id);
    }

    public Woman getWoman(String id) {
        return (Woman) allPersons.get(id);
    }

}
