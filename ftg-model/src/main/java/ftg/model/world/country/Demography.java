package ftg.model.world.country;

import ftg.model.person.Person;

public interface Demography {

    double getDeathRisk(long age, Person.Sex sex);
}
