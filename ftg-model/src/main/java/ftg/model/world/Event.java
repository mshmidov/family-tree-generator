package ftg.model.world;

import ftg.model.Identified;
import ftg.model.time.TredecimalDate;

public interface Event extends Identified {

    TredecimalDate getDate();

    void apply(World world);
}
