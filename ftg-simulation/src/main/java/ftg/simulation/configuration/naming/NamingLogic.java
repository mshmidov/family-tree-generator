package ftg.simulation.configuration.naming;

import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Pregnancy;

public interface NamingLogic {

    Surname newSurname(String surname);

    String getNameForNewborn(Person mother, Pregnancy pregnancy, NamingSystem namingSystem);
}
