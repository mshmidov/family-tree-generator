package ftg.commons;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import static ftg.commons.test.ThrowableAssertion.assertThrown;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class UniquePairsTest {

    @Test
    public void shouldReturnUniquePairsOfSingleList() {

        // given
        final ImmutableList<String> list = ImmutableList.of("A", "B", "C", "D");

        // when
        final Set<List<String>> result = UniquePairs.of(list);


        // then
        assertThat(result.size(), is(equalTo(6)));
        assertThat(result, hasItem(ImmutableList.of("A", "B")));
        assertThat(result, hasItem(ImmutableList.of("A", "C")));
        assertThat(result, hasItem(ImmutableList.of("A", "D")));
        assertThat(result, hasItem(ImmutableList.of("B", "C")));
        assertThat(result, hasItem(ImmutableList.of("B", "D")));
        assertThat(result, hasItem(ImmutableList.of("C", "D")));
    }

    @Test
    public void shouldReturnNPairsOfSingleListShorterThanTwo() {

        // given
        final ImmutableList<String> list = ImmutableList.of("A");
        final Set<List<String>> result = UniquePairs.of(list);

        // then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnUniquePairsOfTwoLists() {

        // given
        final ImmutableList<String> listA = ImmutableList.of("A", "B", "C");

        final ImmutableList<Integer> listB = ImmutableList.of(1, 2, 3);

        // when
        final Set<Pair<String, Integer>> result = UniquePairs.of(listA, listB);


        // then
        assertThat(result.size(), is(equalTo(9)));
        assertThat(result, hasItem(new ImmutablePair<>("A", 1)));
        assertThat(result, hasItem(new ImmutablePair<>("A", 2)));
        assertThat(result, hasItem(new ImmutablePair<>("A", 3)));
        assertThat(result, hasItem(new ImmutablePair<>("B", 1)));
        assertThat(result, hasItem(new ImmutablePair<>("B", 2)));
        assertThat(result, hasItem(new ImmutablePair<>("B", 3)));
        assertThat(result, hasItem(new ImmutablePair<>("C", 1)));
        assertThat(result, hasItem(new ImmutablePair<>("C", 2)));
        assertThat(result, hasItem(new ImmutablePair<>("C", 3)));
    }

    @Test
    public void shouldThrowEmptyListofTwoLists() {

        assertThrown(() -> UniquePairs.of(ImmutableList.of(), ImmutableList.of("A"))).isInstanceOf(IllegalArgumentException.class);
        assertThrown(() -> UniquePairs.of(ImmutableList.of("A"), ImmutableList.of())).isInstanceOf(IllegalArgumentException.class);
    }
}