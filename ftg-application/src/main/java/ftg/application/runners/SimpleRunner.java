package ftg.application.runners;

import ftg.commons.AveragingStopwatch;
import ftg.model.time.TredecimalCalendar;
import ftg.model.time.TredecimalDateFormat;
import ftg.model.world.World;
import ftg.simulation.Simulation;

public final class SimpleRunner implements SimulationRunner {

    @Override
    public void run(Simulation simulation, World world, int years) {

        final AveragingStopwatch yearTime = new AveragingStopwatch();

        for (int year = 0; year < years; year++) {

            yearTime.start();
            for (int day = 0; day < TredecimalCalendar.DAYS_IN_YEAR; day++) {
                simulation.nextDay(world);
            }
            yearTime.stop();

            System.out.println(TredecimalDateFormat.ISO.format(world.getCurrentDate()) + ", total alive: " + world.buckets().livingPersons().length());
        }

        System.out.println(simulation.measurements.toString());
        System.out.println("yearTime=" + yearTime);
    }
}
