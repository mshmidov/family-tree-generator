package ftg.graph.model.person;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SurnameConverter;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public class Woman extends Person {

    @Relationship(type = "MARRIED", direction = Relationship.UNDIRECTED)
    private Man husband;

    @Property
    @Convert(SurnameConverter.class)
    private Surname currentSurname;

    @Relationship(type = "PREGNANT")
    private Pregnancy pregnancy;

    public Woman() {
    }

    public Woman(String name, Surname surname, TredecimalDate birthDate) {
        super(name, surname, birthDate);
        currentSurname = surname;
    }

    @Override
    public Sex getSex() {
        return Sex.FEMALE;
    }

    @Override
    public Person getSpouse() {
        return husband;
    }

    @Override
    public void setSpouse(Person spouse) {
        if(spouse instanceof Man) {
            husband = (Man) spouse;
        } else {
            throw new IllegalArgumentException("Woman's spouse should be a man");
        }
    }

    public Man getHusband() {
        return husband;
    }

    public void setHusband(Man husband) {
        this.husband = husband;
    }

    public Surname getCurrentSurname() {
        return currentSurname;
    }

    public void setCurrentSurname(Surname currentSurname) {
        this.currentSurname = currentSurname;
    }

    public Pregnancy getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(Pregnancy pregnancy) {
        this.pregnancy = pregnancy;
    }
}
