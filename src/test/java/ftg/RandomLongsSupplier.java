package ftg;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLongsSupplier extends ParameterSupplier {

    private final Random random = new Random();

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        final RandomLongs annotation = sig.getAnnotation(RandomLongs.class);

        final List<PotentialAssignment> list = new ArrayList<>();

        for (long i = 0; i < annotation.value(); i++) {
            final long v = random.nextLong();
            list.add(PotentialAssignment.forValue(String.valueOf(v), v));
        }

        return list;
    }
}