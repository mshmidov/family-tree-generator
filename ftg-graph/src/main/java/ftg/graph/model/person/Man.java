package ftg.graph.model.person;

import ftg.commons.time.TredecimalDate;
import org.neo4j.ogm.annotation.Relationship;

public class Man extends Person {

    @Relationship(type = "MARRIED", direction = Relationship.UNDIRECTED)
    private Woman wife;

    public Man() {
    }

    Man(String id, String namespace, String name, Family family, TredecimalDate birthDate) {
        super(id, namespace, name, family, birthDate);
    }

    @Override
    public Sex getSex() {
        return Sex.MALE;
    }

    @Override
    public Person getSpouse() {
        return wife;
    }

    @Override
    public void setSpouse(Person spouse) {
        if(spouse instanceof Woman) {
            wife = (Woman) spouse;
        } else if (spouse == null) {
            wife = null;
        } else {
            throw new IllegalArgumentException("Man's spouse should be a woman");
        }
    }

    public Woman getWife() {
        return wife;
    }

    public void setWife(Woman wife) {
        this.wife = wife;
    }

    @Override
    public String toString() {
        return getName() + " " + getFamily().getSurname().getMaleForm();
    }
}
