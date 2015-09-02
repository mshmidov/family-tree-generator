package ftg.application.configuration.naming;

import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.state.Pregnancy;

public interface NamingLogic {

    Surname newSurname(String surname);

    String getNameForNewborn(Person mother, Pregnancy pregnancy, NamingSystem namingSystem);
}
