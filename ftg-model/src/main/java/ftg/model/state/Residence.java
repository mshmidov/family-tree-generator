package ftg.model.state;

public final class Residence implements State {

    private final String country;

    public Residence(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
