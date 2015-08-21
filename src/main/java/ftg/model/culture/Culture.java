package ftg.model.culture;

import ftg.generator.Generator;
import ftg.generator.LimitedGenerator;
import ftg.model.person.Person;
import ftg.model.culture.surname.Surname;

public interface Culture {

    Generator<String> maleNames();

    Generator<String> femaleNames();

    Generator<String> names(Person.Sex sex);

    LimitedGenerator<Surname> surnames();
}
