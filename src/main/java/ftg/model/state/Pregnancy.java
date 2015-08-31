package ftg.model.state;

import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;

public final class Pregnancy implements State {

    private final TredecimalDate conceptionDate;

    private final Person father;

    private final Person.Sex childSex;

    public Pregnancy(TredecimalDate conceptionDate, Person father, Person.Sex childSex) {
        this.conceptionDate = conceptionDate;
        this.father = father;
        this.childSex = childSex;
    }

    public TredecimalDate getConceptionDate() {
        return conceptionDate;
    }

    public Person getFather() {
        return father;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }
}
