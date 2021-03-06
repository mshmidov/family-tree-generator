package ftg.model.person.state;

import com.google.common.base.MoreObjects;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;

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

    public TredecimalDateInterval getAge(TredecimalDate now) {
        return TredecimalDateInterval.intervalBetween(now, conceptionDate);
    }

    public Person getFather() {
        return father;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conceptionDate", conceptionDate)
                .add("father", father)
                .add("childSex", childSex)
                .toString();
    }
}
