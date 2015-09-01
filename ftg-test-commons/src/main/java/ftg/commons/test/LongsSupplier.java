package ftg.commons.test;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;

public class LongsSupplier extends ParameterSupplier {

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        final Longs annotation = sig.getAnnotation(Longs.class);

        final List<PotentialAssignment> list = new ArrayList<>();

        for (long i : annotation.value()) {
            list.add(PotentialAssignment.forValue(String.valueOf(i), i));
        }

        return list;
    }
}