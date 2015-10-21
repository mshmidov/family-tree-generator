package ftg.simulation.configuration.naming;

import ftg.graph.model.person.Person;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Surname;

public interface NamingLogic {

    Surname newSurname(String surname);

    String getNameForNewborn(Person mother, Pregnancy pregnancy, NamingSystem namingSystem);
}
