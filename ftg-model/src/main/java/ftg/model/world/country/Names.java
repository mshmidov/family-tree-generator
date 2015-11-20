package ftg.model.world.country;

import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Pregnancy;
import javaslang.collection.Stream;

public interface Names {

    Stream<String> randomNames(Person.Sex sex);

    Stream<Surname> randomSurnames();

    Surname surnameFrom(String canonicalForm);

    String childName(Person mother, Pregnancy pregnancy);
}
