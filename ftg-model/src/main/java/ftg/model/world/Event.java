package ftg.model.world;

import ftg.model.time.TredecimalDate;

public interface Event {

    TredecimalDate getDate();

    void apply(World world);
}
