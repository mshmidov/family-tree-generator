package ftg.commons;

public final class Average {

    private double last = 0;

    private double sum = 0;
    private long count = 0;

    public void add(long value) {
        last = value;
        sum += value;
        count++;
    }

    public double get() {
        return sum / count;
    }

    public long getCount() {
        return count;
    }

    public double getLast() {
        return last;
    }
}
