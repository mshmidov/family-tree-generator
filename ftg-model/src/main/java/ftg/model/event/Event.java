package ftg.model.event;

import ftg.model.Identified;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;

public abstract class Event<R> implements Identified {

    private final String id;

    private final TredecimalDate date;

    protected Event(String id, TredecimalDate date) {
        this.id = id;
        this.date = date;
    }

    public abstract R apply(World world, PersonFactory personFactory, RelationFactory relationFactory);

    @Override
    public final String getId() {
        return id;
    }

    public final TredecimalDate getDate(){
        return date;
    }


}
