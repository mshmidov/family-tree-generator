package ftg.simulation.configuration.naming;


import ftg.graph.model.person.Person;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Surname;

public final class RussianNamingLogic implements NamingLogic {

    @Override
    public Surname newSurname(String surname) {
        return new Surname(surname, calculateFemaleSurname(surname));
    }

    @Override
    public String getNameForNewborn(Person mother, Pregnancy pregnancy, NamingSystem namingSystem) {
        // primitive implementation
        return namingSystem.getNames(pregnancy.getChildSex()).get();
    }

    private String calculateFemaleSurname(String maleForm) {

        if (maleForm.endsWith("ов") || maleForm.endsWith("ев") || maleForm.endsWith("ин")) {
            return maleForm + "а";
        }

        if (maleForm.endsWith("ий") || maleForm.endsWith("ый") || maleForm.endsWith("ой")) {
            return maleForm.substring(0, maleForm.length() - 2) + "ая";
        }

        return maleForm;
    }
}
