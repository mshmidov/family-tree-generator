package ftg.graph.model.state;

import com.google.common.base.MoreObjects;

public final class ResidentOfCountry extends State {

    private String country;

    public ResidentOfCountry() {
    }

    public ResidentOfCountry(String country) {
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
