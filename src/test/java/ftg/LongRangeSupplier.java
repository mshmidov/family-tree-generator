package ftg;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;

public class LongRangeSupplier extends ParameterSupplier {

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        final LongRange annotation = sig.getAnnotation(LongRange.class);

        final List<PotentialAssignment> list = new ArrayList<>();

        for (long i = annotation.from(); i <= annotation.to(); i += annotation.step()) {
            list.add(PotentialAssignment.forValue(String.valueOf(i), i));
        }

        return list;
    }
}