package ftg.model.culture;

import ftg.commons.generator.Generator;
import ftg.commons.generator.LimitedGenerator;
import ftg.model.culture.surname.Surname;
import ftg.model.person.Person;

public interface Culture {

    Generator<String> maleNames();

    Generator<String> femaleNames();

    Generator<String> names(Person.Sex sex);

    LimitedGenerator<Surname> uniqueSurnames();

    Generator<Surname> surnames();
}
