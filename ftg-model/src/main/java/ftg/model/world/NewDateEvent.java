package ftg.model.world;

import ftg.model.time.TredecimalDate;

public final class NewDateEvent implements Event {

    private final TredecimalDate newDate;

    public NewDateEvent(TredecimalDate newDate) {
        this.newDate = newDate;
    }

    @Override
    public void apply(World world) {
        world.setDate(newDate);
    }
}
