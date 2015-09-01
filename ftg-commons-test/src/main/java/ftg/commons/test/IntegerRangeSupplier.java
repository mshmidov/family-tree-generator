package ftg.commons.test;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;

public class IntegerRangeSupplier extends ParameterSupplier {

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        final IntegersInRange annotation = sig.getAnnotation(IntegersInRange.class);

        final List<PotentialAssignment> list = new ArrayList<>();

        for (int i = annotation.from(); i <= annotation.to(); i += annotation.step()) {
            list.add(PotentialAssignment.forValue(String.valueOf(i), i));
        }

        return list;
    }
}