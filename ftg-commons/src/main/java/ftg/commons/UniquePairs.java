package ftg.commons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public final class UniquePairs {

    private UniquePairs() {
    }

    public static <T> Set<List<T>> of(List<T> list) {

        final ImmutableSet.Builder<List<T>> result = ImmutableSet.<List<T>>builder();

        for (int i = 1; i < list.size(); i++) {
            result.add(ImmutableList.of(list.get(0), list.get(i)));
        }

        if (list.size() > 2) {
            result.addAll(of(list.subList(1, list.size())));
        }

        return result.build();
    }

    public static <A, B> Set<Pair<A, B>> of(Collection<A> listA, Collection<B> listB) {
        checkArgument(listA.size() > 0);
        checkArgument(listB.size() > 0);

        final ImmutableSet.Builder<Pair<A, B>> result = ImmutableSet.<Pair<A, B>>builder();

        for (A a : listA) {
            for (B b : listB) {
                result.add(new ImmutablePair<>(a, b));
            }
        }

        return result.build();
    }


}
