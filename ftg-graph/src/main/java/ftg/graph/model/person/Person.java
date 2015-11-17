package ftg.graph.model.person;

import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateInterval;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.DomainObject;
import ftg.graph.model.world.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public abstract class Person extends DomainObject {

    @Property
    private String name;

    @Relationship
    private Family family;

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate birthDate;

    @Relationship(type = "IS_CHILD_OF")
    private Man father;

    @Relationship(type = "IS_CHILD_OF")
    private Woman mother;

    @Relationship(type = "IS_CHILD_OF", direction = Relationship.INCOMING)
    private Set<Person> children = new HashSet<>();

    @Property
    private boolean alive = true;

    @Relationship(type = "LIVES_IN")
    private Country countryOfResidence;

    public Person() {
    }

    Person(String id, String namespace, String name, Family family, TredecimalDate birthDate) {
        super(id, namespace);
        this.name = name;
        this.family = family;
        this.birthDate = birthDate;
    }

    public abstract Sex getSex();

    public abstract Person getSpouse();

    public abstract void setSpouse(Person spouse);

    public String getName() {
        return name;
    }

    public Family getFamily() {
        return family;
    }

    public TredecimalDate getBirthDate() {
        return birthDate;
    }

    public TredecimalDateInterval getAgeAt(TredecimalDate date) {
        return TredecimalDateInterval.intervalBetween(date, birthDate);
    }

    public Man getFather() {
        return father;
    }

    public void setFather(Man father) {
        this.father = father;
    }

    public Woman getMother() {
        return mother;
    }

    public void setMother(Woman mother) {
        this.mother = mother;
    }


    public Set<Person> getChildren() {
        return children;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Country getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(Country countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public enum Sex {MALE, FEMALE}
}
