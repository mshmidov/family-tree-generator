package ftg.model.event;

import ftg.model.World;

public interface Event {

    void apply(World world);
}
