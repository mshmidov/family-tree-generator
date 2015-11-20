package ftg.simulation.configuration.naming;

import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Pregnancy;
import ftg.model.world.country.Names;
import ftg.simulation.RandomChoice;
import javaslang.collection.Array;
import javaslang.collection.Stream;
import javaslang.control.Match;

public final class NamesImpl implements Names {

    private final NamingLogic namingLogic;
    private final Array<String> maleNames;
    private final Array<String> femaleNames;
    private final Array<String> surnames;


    public NamesImpl(NamingLogic namingLogic, Array<String> maleNames, Array<String> femaleNames, Array<String> surnames) {
        this.namingLogic = namingLogic;
        this.maleNames = maleNames;
        this.femaleNames = femaleNames;
        this.surnames = surnames;
    }

    @Override
    public Stream<String> randomNames(Person.Sex sex) {
        return Match.of(sex)
            .whenIs(Person.Sex.MALE).then(Stream.gen(() -> RandomChoice.from(maleNames)))
            .whenIs(Person.Sex.FEMALE).then(Stream.gen(() -> RandomChoice.from(femaleNames)))
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Stream<Surname> randomSurnames() {
        return Stream.gen(() -> namingLogic.newSurname(RandomChoice.from(surnames)));
    }

    @Override
    public Surname surnameFrom(String canonicalForm) {
        return namingLogic.newSurname(canonicalForm);
    }

    @Override
    public String childName(Person mother, Pregnancy pregnancy) {
        return namingLogic.getNameForNewborn(mother, pregnancy);
    }
}
