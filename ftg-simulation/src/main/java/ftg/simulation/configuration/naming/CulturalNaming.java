package ftg.simulation.configuration.naming;

public enum CulturalNaming {
    RUSSIAN(new RussianNamingLogic());

    private final NamingLogic namingLogic;

    CulturalNaming(NamingLogic namingLogic) {
        this.namingLogic = namingLogic;
    }

    public NamingLogic getNamingLogic() {
        return namingLogic;
    }
}
