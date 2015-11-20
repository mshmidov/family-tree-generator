package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.Surname;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.model.world.country.Country;

public final class PersonIntroductionEvent extends Event<Person> {

    private final String name;
    private final Surname surname;
    private final Person.Sex sex;
    private final TredecimalDate birthDate;
    private final Country country;

    PersonIntroductionEvent(String id, TredecimalDate date,
        String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, Country country) {

        super(id, date);

        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.birthDate = birthDate;
        this.country = country;
    }

    @Override
    public Person apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {
        final Person person = personFactory.newPerson(name, surname, sex, birthDate, new Residence(country));
        world.addPerson(person);
        return person;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue("[" + getId() + "," + getDate() + "]")
            .add("name", name)
            .add("surname", surname)
            .add("sex", sex)
            .add("birthDate", birthDate)
            .add("country", country)
            .toString();
    }
}
