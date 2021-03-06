package ftg.commons;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public final class AveragingStopwatch {

    private final Average measurement = new Average();

    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    public void start() {
        stopwatch.start();
    }

    public void stop() {
        measurement.add(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();
    }

    public double getMeasurement() {
        return measurement.get();
    }

    public double getLast() {
        return measurement.getLast();
    }

    @Override
    public String toString() {
        return String.format("%.2f (%s)", measurement.get(), measurement.getCount());
    }
}
