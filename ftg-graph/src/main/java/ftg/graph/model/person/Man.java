package ftg.graph.model.person;

import ftg.commons.time.TredecimalDate;
import org.neo4j.ogm.annotation.Relationship;

public class Man extends Person {

    @Relationship(type = "MARRIED", direction = Relationship.UNDIRECTED)
    private Woman wife;

    public Man() {
    }

    Man(String id, String name, Surname surname, TredecimalDate birthDate) {
        super(id, name, surname, birthDate);
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
}
