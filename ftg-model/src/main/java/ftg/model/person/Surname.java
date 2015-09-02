package ftg.model.person;


import java.util.Objects;

public final class Surname {

    private final String maleForm;

    private final String femaleForm;

    private final String toString;

    public Surname(String maleForm, String femaleForm) {
        this.maleForm = maleForm;
        this.femaleForm = femaleForm;
        toString = String.format("%s / %s", maleForm, femaleForm);
    }

    public String getMaleForm() {
        return maleForm;
    }

    public String getFemaleForm() {
        return femaleForm;
    }

    public String getForm(Person.Sex sex) {
        return (sex == Person.Sex.MALE)
                ? maleForm
                : femaleForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Surname)) return false;
        Surname that = (Surname) o;
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
}
