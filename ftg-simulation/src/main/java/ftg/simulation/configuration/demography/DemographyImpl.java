package ftg.simulation.configuration.demography;

import ftg.model.person.Person;
import ftg.model.world.country.Demography;

public final class DemographyImpl implements Demography {

    private final DemographyTable deathRiskTable;

    public DemographyImpl(DemographyTable deathRiskTable) {
        this.deathRiskTable = deathRiskTable;
    }

    @Override
    public double getDeathRisk(long age, Person.Sex sex) {
        return deathRiskTable.get(age, sex);
    }
}
