package ftg.model.world.country;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class Country {

    private final String name;
    private final Names nativeNames;
    private final Demography demography;

    public Country(String name, Names nativeNames,Demography demography) {
        this.name = name;
        this.nativeNames = nativeNames;
        this.demography = demography;
    }

    public String getName() {
        return name;
    }

    public Names getNativeNames() {
        return nativeNames;
    }

    public Demography getDemography() {
        return demography;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Country country = (Country) o;
        return Objects.equals(name, country.name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .toString();
    }
}
