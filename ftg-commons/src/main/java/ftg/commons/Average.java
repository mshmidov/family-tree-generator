package ftg.commons;

public final class Average {

    private double sum = 0;
    private long count = 0;

    public void add(long value) {
        sum += value;
        count++;
    }

    public double get() {
        return sum / count;
    }

    public long getCount() {
        return count;
    }
}
