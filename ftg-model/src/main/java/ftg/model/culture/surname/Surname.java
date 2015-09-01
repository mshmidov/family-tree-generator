package ftg.model.culture.surname;

import ftg.model.culture.Culture;
import ftg.model.person.Person;

public interface Surname {

    Culture getCulture();

    String getMaleForm();

    String getFemaleForm();

    String getForm(Person.Sex sex);
}
