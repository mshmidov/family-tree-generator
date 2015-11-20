package ftg.simulation.configuration.naming;

import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.world.country.Country;

public final class RussianNamingLogic implements NamingLogic {

    @Override
    public Surname newSurname(String canonicalForm) {
        return new Surname(canonicalForm, calculateFemaleSurname(canonicalForm));
    }

    @Override
    public String getNameForNewborn(Person mother, Pregnancy pregnancy) {
        // primitive implementation
        final Country birthCountry = mother.state(Residence.class).get().getCountry();
        return birthCountry.getNativeNames().randomNames(pregnancy.getChildSex()).take(1).get();
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
