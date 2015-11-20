package ftg.simulation.configuration.naming;

public enum CulturalNaming {
    RUSSIAN(new RussianNamingLogic());

    public final NamingLogic namingLogic;

    CulturalNaming(NamingLogic namingLogic) {
        this.namingLogic = namingLogic;
    }
}
