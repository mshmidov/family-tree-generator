package ftg.simulation.configuration.demography;


import ftg.graph.model.person.Person;

public final class Demography {

    private final DemographyTable deathRiskTable;

    public Demography(DemographyTable deathRiskTable) {
        this.deathRiskTable = deathRiskTable;
    }

    public double getDeathRisk(long age, Person.Sex sex) {
        return deathRiskTable.get(age, sex);
    }
}
