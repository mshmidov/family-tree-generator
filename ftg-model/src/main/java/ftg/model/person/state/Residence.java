package ftg.model.person.state;

import com.google.common.base.MoreObjects;

public final class Residence implements State {

    private final String country;

    public Residence(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("country", country)
                .toString();
    }
}
