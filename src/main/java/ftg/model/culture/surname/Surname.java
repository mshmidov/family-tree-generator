package ftg.model.culture.surname;

import ftg.model.person.Person;

public interface Surname {

    String getMaleForm();

    String getFemaleForm();

    String getForm(Person.Sex sex);
}
