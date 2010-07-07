package hashing;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FakeOpenAddressingHashtable {
    private final int tableSize;
    private final List<Integer> buckets;
    private final Prober prober;
    private int collisions;

    public FakeOpenAddressingHashtable(int tableSize, Prober prober) {
        this.tableSize = tableSize;
        this.buckets = Lists.newArrayListWithCapacity(tableSize);
        this.prober = prober;
        buckets.addAll(Collections.<Integer>nCopies(tableSize, null));
    }

    public void put(Object o) {
        int hashCode = o.hashCode();

        Iterator<Integer> probeSequence = prober.probeSequence(hashCode, tableSize);
        int position = -1;
        while (buckets.get(position = probeSequence.next()) != null)
            collisions++;

        buckets.set(position, hashCode);
    }

    public int getCollisionCount() {
        return collisions;
    }

    public ProbeCounter newProbeCounter() {
        return new ProbeCounter();
    }

    public class ProbeCounter {
        private final ImmutableList<Integer> hashCodes;

        private ProbeCounter() {
            this.hashCodes = ImmutableList.copyOf(Iterables.filter(buckets, Predicates.notNull()));
            Preconditions.checkArgument(!hashCodes.isEmpty());
        }

        public double computeAverageProbesOnSuccess() {
            //for each stored hashCode, try to find it and count the probes
            long totalProbes = countProbes(Functions.<Integer>identity());

            return (double)totalProbes / hashCodes.size();
        }

        public double computeAverageProbesOnCollisions() {
            //for each stored hashCode, pretend we have a collision, and see how many probes
            //we do to find out the element is not there
            long totalProbes = countProbes(Functions.constant(null));

            return (double)totalProbes / hashCodes.size();
        }

        long countProbes(Function<? super Integer, ?> endMarkerFunction) {
            long totalProbes = 0;
            for (Integer hashCode : hashCodes) {
                Iterator<Integer> probeSequence = prober.probeSequence(hashCode, tableSize);
                final Object endMarker = endMarkerFunction.apply(hashCode);
                totalProbes += 1 + Iterators.indexOf(probeSequence, new Predicate<Integer>() {
                    public boolean apply(Integer index) {
                        return Objects.equal(buckets.get(index), endMarker);
                    }
                });
            }
            return totalProbes;
        }

    }
}
