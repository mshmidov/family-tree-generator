package ftg.model.culture;

import com.google.common.base.Function;
import ftg.model.culture.surname.Surname;
import ftg.generator.*;
import ftg.model.person.Person;

import java.util.List;

public class SimpleCulture implements Culture {

    private final Generator<String> maleNames;
    private final Generator<String> femaleNames;
    private final LimitedGenerator<Surname> surnames;

    public SimpleCulture(List<String> maleNames, List<String> femaleNames, List<String> surnames, Function<String, Surname> surnameFactory)  {
        this.maleNames = new RandomElementGenerator<>(maleNames);
        this.femaleNames = new RandomElementGenerator<>(femaleNames);
        this.surnames = new TransformingLimitedGenerator<>(new RandomUniqueElementGenerator<>(surnames), surnameFactory);

    }

    @Override
    public Generator<String> maleNames() {
        return maleNames;
    }

    @Override
    public Generator<String> femaleNames() {
        return femaleNames;
    }

    @Override
    public Generator<String> names(Person.Sex sex) {
        return (sex == Person.Sex.MALE)
               ? maleNames
               : femaleNames;
    }

    @Override
    public LimitedGenerator<Surname> surnames() {
        return surnames;
    }
}
