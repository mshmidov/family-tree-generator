package ftg.commons;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Via <a href="http://stackoverflow.com/a/11123951/1312698">this cool answer on SO</a>.
 */
public final class PermutationsOfN {

    public static void main(String[] args) {
        List<String> f = Lists.newArrayList("A", "B", "C", "D");
        System.out.println(String.format("n=1 subsets %s", PermutationsOfN.subsets(f, 1)));
        System.out.println(String.format("n=1 permutations %s", PermutationsOfN.permutations(f, 1)));
        System.out.println(String.format("n=2 subsets %s", PermutationsOfN.subsets(f, 2)));
        System.out.println(String.format("n=2 permutations %s", PermutationsOfN.permutations(f, 2)));
        System.out.println(String.format("n=3 subsets %s", PermutationsOfN.subsets(f, 3)));
        System.out.println(String.format("n=3 permutations %s", PermutationsOfN.permutations(f, 3)));
        System.out.println(String.format("n=4 subsets %s", PermutationsOfN.subsets(f, 4)));
        System.out.println(String.format("n=4 permutations %s", PermutationsOfN.permutations(f, 4)));
        System.out.println(String.format("n=5 subsets %s", PermutationsOfN.subsets(f, 5)));
        System.out.println(String.format("n=5 permutations %s", PermutationsOfN.permutations(f, 5)));
    }

    public static <T> Collection<List<T>> permutations(List<T> list, int size) {
        final Collection<List<T>> all = new ArrayList<>();
        if (list.size() < size) {
            size = list.size();
        }
        if (list.size() == size) {
            all.addAll(Collections2.permutations(list));
        } else {
            subsets(list, size).stream().flatMap(p -> Collections2.permutations(p).stream()).forEach(all::add);
        }
        return all;
    }

    public static <T> List<List<T>> subsets(List<T> set, int k) {
        if (k > set.size()) {
            k = set.size();
        }
        final List<List<T>> result = new ArrayList<>();
        final List<T> subset = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            subset.add(null);
        }
        return processLargerSubsets(result, set, subset, 0, 0);
    }

    private static <T> List<List<T>> processLargerSubsets(List<List<T>> result, List<T> set, List<T> subset, int subsetSize, int nextIndex) {
        if (subsetSize == subset.size()) {
            result.add(ImmutableList.copyOf(subset));
        } else {
            for (int j = nextIndex; j < set.size(); j++) {
                subset.set(subsetSize, set.get(j));
                processLargerSubsets(result, set, subset, subsetSize + 1, j + 1);
            }
        }
        return result;
    }
}