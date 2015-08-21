package ftg.model.culture.surname;

import ftg.model.person.Person;

import java.util.Objects;

public final class RussianSurname implements Surname {

    private final String maleForm;

    private final String femaleForm;

    private final String toString;

    public RussianSurname(String maleForm) {
        this.maleForm = maleForm;
        femaleForm = calculateFemaleSurname(maleForm);
        toString = String.format("%s / %s", maleForm, femaleForm);
    }

    @Override
    public String getMaleForm() {
        return maleForm;
    }

    @Override
    public String getFemaleForm() {
        return femaleForm;
    }

    @Override
    public String getForm(Person.Sex sex) {
        return (sex == Person.Sex.MALE)
               ? maleForm
               : femaleForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RussianSurname that = (RussianSurname) o;
        return Objects.equals(maleForm, that.maleForm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maleForm);
    }

    @Override
    public String toString() {
        return toString;
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
