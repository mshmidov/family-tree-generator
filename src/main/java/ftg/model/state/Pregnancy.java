package ftg.model.state;

import ftg.model.Person;

public final class Pregnancy implements State {

    // TODO: conception date

    private final Person father;

    private final Person.Sex childSex;

    public Pregnancy(Person father, Person.Sex childSex) {
        this.father = father;
        this.childSex = childSex;
    }

    public Person getFather() {
        return father;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }
}
