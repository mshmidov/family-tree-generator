package ftg;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;

public class IntegersSupplier extends ParameterSupplier {

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        final Integers annotation = sig.getAnnotation(Integers.class);

        final List<PotentialAssignment> list = new ArrayList<>();

        for (int i : annotation.value()) {
            list.add(PotentialAssignment.forValue(String.valueOf(i), i));
        }

        return list;
    }
}