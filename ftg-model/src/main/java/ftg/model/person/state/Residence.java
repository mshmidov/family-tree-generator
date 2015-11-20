package ftg.model.person.state;

import com.google.common.base.MoreObjects;
import ftg.model.world.country.Country;

public final class Residence implements State {

    private final Country country;

    public Residence(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("country", country)
            .toString();
    }
}
