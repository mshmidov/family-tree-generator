package ftg.simulation.lineage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ftg.model.event.EventFactory;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.Surname;
import ftg.model.relation.Parentage;
import ftg.model.relation.RelationFactory;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@RunWith(Theories.class)
public class LineagesTest {

    private final AtomicLong personCounter = new AtomicLong(0);

    private final EventFactory eventFactory = new EventFactory(() -> String.valueOf(personCounter.incrementAndGet()));
    private final PersonFactory personFactory = new PersonFactory(() -> String.valueOf(personCounter.incrementAndGet()));
    private final RelationFactory relationFactory = new RelationFactory();

    @Test
    public void shouldFindFather() {

        // given
        final Parentage parentage = relationFactory.createParentage(newMale("F"), newFemale("M"), newMale("P"));

        // when
        final Optional<Person> result = new Lineages().getFather(parentage.getChild());


        // then
        assertThat(result.get(), is(equalTo(parentage.getFather())));
    }

    @Test
    public void shouldFindMother() {

        // given
        final Parentage parentage = relationFactory.createParentage(newMale("F"), newFemale("M"), newMale("P"));

        // when
        final Optional<Person> result = new Lineages().getMother(parentage.getChild());


        // then
        assertThat(result.get(), is(equalTo(parentage.getMother())));
    }

    @Test
    public void shouldFindClosestRelation() {

        // given

        final Person grandfather1 = newMale("grandfather1");
        final Person grandmother1 = newFemale("grandmother1");
        final Person grandfather2 = newMale("grandfather2");
        final Person grandmother2 = newFemale("grandmother2");
        final Person grandfather3 = newMale("grandfather3");
        final Person grandmother3 = newFemale("grandmother3");

        final Person father1 = newMale("father1");
        final Person mother1 = newFemale("mother1");
        final Person father2 = newMale("father2");
        final Person mother2 = newFemale("mother2");

        final Person child1 = newMale("child1");
        final Person child2 = newFemale("child2");

        relationFactory.createParentage(grandfather1, grandmother1, father1);

        relationFactory.createParentage(grandfather2, grandmother2, mother1);
        relationFactory.createParentage(grandfather2, grandmother2, father2);

        relationFactory.createParentage(grandfather3, grandmother3, mother2);

        relationFactory.createParentage(father1, mother1, child1);
        relationFactory.createParentage(father2, mother2, child2);

        final Lineages lineages = new Lineages();

        // then
        assertThat(lineages.findClosestRelation(child1, child2, 10), is(equalTo(Optional.of(2))));

    }

    @Test
    public void shouldFindAncestry() {

        // given

        final Person grandfather1 = newMale("grandfather1");
        final Person grandmother1 = newFemale("grandmother1");
        final Person grandfather2 = newMale("grandfather2");
        final Person grandmother2 = newFemale("grandmother2");
        final Person grandfather3 = newMale("grandfather3");
        final Person grandmother3 = newFemale("grandmother3");

        final Person father1 = newMale("father1");
        final Person mother1 = newFemale("mother1");
        final Person father2 = newMale("father2");
        final Person mother2 = newFemale("mother2");

        final Person child1 = newMale("child1");
        final Person child2 = newFemale("child2");

        relationFactory.createParentage(grandfather1, grandmother1, father1);

        relationFactory.createParentage(grandfather2, grandmother2, mother1);
        relationFactory.createParentage(grandfather2, grandmother2, father2);

        relationFactory.createParentage(grandfather3, grandmother3, mother2);

        relationFactory.createParentage(father1, mother1, child1);
        relationFactory.createParentage(father2, mother2, child2);

        final Lineages lineages = new Lineages();

        // then
        assertThat(lineages.findClosestAncestry(child1, grandfather1), is(equalTo(Optional.of(2))));
        assertThat(lineages.findClosestAncestry(child1, grandmother2), is(equalTo(Optional.of(2))));
        assertThat(lineages.findClosestAncestry(child1, grandmother3), is(equalTo(Optional.empty())));
        assertThat(lineages.findClosestAncestry(child1, father1), is(equalTo(Optional.of(1))));
        assertThat(lineages.findClosestAncestry(child1, mother1), is(equalTo(Optional.of(1))));

    }

    private Person newMale(String name) {
        return personFactory.newPerson(eventFactory.newPersonData(name, new Surname("A", "A"), Person.Sex.MALE, new TredecimalDate(0), new Residence("country")));
    }

    private Person newFemale(String name) {
        return personFactory.newPerson(eventFactory.newPersonData(name, new Surname("A", "A"), Person.Sex.FEMALE, new TredecimalDate(0), new Residence("country")));
    }
}
