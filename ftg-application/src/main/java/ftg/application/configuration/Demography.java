package ftg.application.configuration;

import ftg.application.configuration.naming.DemographyTable;
import ftg.model.person.Person;

public final class Demography {

    private final DemographyTable deathRiskTable;

    public Demography(DemographyTable deathRiskTable) {
        this.deathRiskTable = deathRiskTable;
    }

    public double getDeathRisk(int age, Person.Sex sex) {
        return deathRiskTable.get(age, sex);
    }
}
