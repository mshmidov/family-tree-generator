package ftg.simulation.configuration.naming;

import ftg.commons.generator.Generator;
import ftg.commons.generator.LimitedGenerator;
import ftg.commons.generator.RandomElementGenerator;
import ftg.commons.generator.RandomUniqueElementGenerator;
import ftg.commons.generator.TransformingGenerator;
import ftg.commons.generator.TransformingLimitedGenerator;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Surname;

import java.util.List;

public final class NamingSystem {

    private final NamingLogic namingLogic;

    private final Generator<String> maleNames;
    private final Generator<String> femaleNames;
    private final Generator<Surname> commonSurnames;
    private final LimitedGenerator<Surname> uniqueSurnames;

    public NamingSystem(NamingLogic namingLogic,
                        List<String> maleNames,
                        List<String> femaleNames,
                        List<String> commonSurnames,
                        List<String> uniqueSurnames) {

        this.namingLogic = namingLogic;

        this.maleNames = new RandomElementGenerator<>(maleNames);
        this.femaleNames = new RandomElementGenerator<>(femaleNames);

        this.uniqueSurnames = new TransformingLimitedGenerator<>(new RandomUniqueElementGenerator<>(uniqueSurnames), namingLogic::newSurname);
        this.commonSurnames = new TransformingGenerator<>(new RandomElementGenerator<>(commonSurnames), namingLogic::newSurname);
    }

    public String getNameForNewborn(Person mother, Pregnancy pregnancy) {
        return namingLogic.getNameForNewborn(mother, pregnancy, this);
    }

    public Generator<String> getMaleNames() {
        return maleNames;
    }

    public Generator<String> getFemaleNames() {
        return femaleNames;
    }

    public Generator<String> getNames(Person.Sex sex) {
        return (sex == Person.Sex.MALE) ? maleNames : femaleNames;
    }

    public Generator<Surname> getCommonSurnames() {
        return commonSurnames;
    }

    public LimitedGenerator<Surname> getUniqueSurnames() {
        return uniqueSurnames;
    }
}
