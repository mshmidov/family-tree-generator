package ftg.graph.model.person;

import ftg.commons.time.TredecimalDate;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

public class Woman extends Person {

    @Relationship(type = "MARRIED", direction = Relationship.UNDIRECTED)
    private Man husband;

    @Relationship
    private Family currentFamily;

    @Relationship(type = "PREGNANT")
    private Pregnancy pregnancy;

    public Woman() {
    }

    public Woman(String id, String namespace, String name, Family family, TredecimalDate birthDate) {
        super(id, namespace, name, family, birthDate);
        currentFamily = family;
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
        } else if (spouse == null) {
            husband = null;
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

    public Family getCurrentFamily() {
        return currentFamily;
    }

    public void setCurrentFamily(Family currentFamily) {
        this.currentFamily = currentFamily;
    }

    public Pregnancy getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(Pregnancy pregnancy) {
        this.pregnancy = pregnancy;
    }

    @Override
    public String toString() {
        return (Objects.equals(getCurrentFamily(), getFamily()))
               ? getName() + " " + getFamily().getSurname().getFemaleForm()
               : String.format("%s %s (%s)", getName(), getCurrentFamily().getSurname().getFemaleForm(), getFamily().getSurname().getFemaleForm());

    }
}
