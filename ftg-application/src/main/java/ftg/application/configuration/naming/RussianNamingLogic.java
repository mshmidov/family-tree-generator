package ftg.application.configuration.naming;

import ftg.model.person.Surname;

public final class RussianNamingLogic implements NamingLogic {

    @Override
    public Surname newSurname(String surname) {
        return new Surname(surname, calculateFemaleSurname(surname));
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
