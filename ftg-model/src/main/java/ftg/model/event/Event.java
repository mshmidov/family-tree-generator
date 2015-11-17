package ftg.model.event;

import ftg.model.Identified;
import ftg.model.person.PersonFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;

public interface Event extends Identified {

    TredecimalDate getDate();

    void apply(World world, PersonFactory personFactory
    );
}
