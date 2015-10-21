package ftg.graph.model.person;

import java.util.Objects;

public final class Surname {

    private final String maleForm;

    private final String femaleForm;

    public Surname(String maleForm, String femaleForm) {
        this.maleForm = maleForm;
        this.femaleForm = femaleForm;
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
    public int hashCode() {
        return Objects.hash(maleForm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Surname)) {
            return false;
        }
        Surname that = (Surname) o;
        return Objects.equals(maleForm, that.maleForm);
    }

    @Override
    public String toString() {
        return maleForm + " / " + femaleForm;
    }
}
