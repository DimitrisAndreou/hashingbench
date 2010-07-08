package hashing;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A fake open-addressing hashtable, useful to benchmark the effects of probing sequences
 * (defined by a {@code Prober}) on collisions and number of probes
 * in successful and unsuccessful lookups.
 */
public class FakeOpenAddressingHashtable {
    private final int tableSize;
    private final List<Integer> buckets;
    private final Prober prober;
    private int collisions;
    private int size;

    /**
     * Constructs a FakeOpenAddressingHashtable of the given (power of two) table size and
     * a {@code Prober}, which defines the probing sequence for each element.
     * 
     * @param tableSize the (power of two) table size of the hashtable
     * @param prober a prober, which defines the probing sequence for each element
     */
    public FakeOpenAddressingHashtable(int tableSize, Prober prober) {
        Preconditions.checkArgument((tableSize & (tableSize - 1)) == 0, "Table size must be a power of two");
        this.tableSize = tableSize;
        this.buckets = Lists.newArrayListWithCapacity(tableSize);
        this.prober = prober;
        buckets.addAll(Collections.<Integer>nCopies(tableSize, null));
    }

    /**
     * Inserts an element in this hashtable. To insert the element, the hashtable
     * tries each table position as indicated by the probing sequence for this element
     * (created by the Prober of this hashtable), until an empty position is found.
     */
    public void put(Object o) {
        if (size == tableSize) {
            throw new IllegalStateException("too many elements");
        }
        int hashCode = o.hashCode();

        Iterator<Integer> probeSequence = prober.probeSequence(hashCode, tableSize);
        int position = -1;
        while (buckets.get(position = probeSequence.next()) != null)
            collisions++;

        buckets.set(position, hashCode);
        size++;
    }

    /**
     * Returns the prober used by this hashtable.
     */
    public Prober getProber() {
        return prober;
    }

    /**
     * Returns the total number of collisions occured upon insertions.
     */
    public int getCollisionCount() {
        return collisions;
    }

    /**
     * Returns an object that returns probing statistics.
     */
    public ProbeCounter newProbeCounter() {
        return new ProbeCounter();
    }

    /**
     * A probing statistics gathering object.
     */
    public class ProbeCounter {
        private final ImmutableList<Integer> hashCodes;

        private ProbeCounter() {
            this.hashCodes = ImmutableList.copyOf(Iterables.filter(buckets, Predicates.notNull()));
            Preconditions.checkArgument(!hashCodes.isEmpty());
        }

        /**
         * Returns the average number of probes per successful lookup in the hashtable.
         */
        public double computeAverageProbesOnSuccess() {
            //for each stored hashCode, try to find it and count the probes
            long totalProbes = countProbes(Functions.<Integer>identity());

            return (double)totalProbes / hashCodes.size();
        }

        /**
         * Returns the average number of probes per unsuccessful lookup in the hashtable.
         * This is computed by simulating looking up (non-existent) elements at each
         * position of the table.
         */
        public double computeAverageProbesOnCollisions() {
            //for each stored hashCode, pretend we have a collision, and see how many probes
            //we do to find out the element is not there
            long totalProbes = countProbes(Functions.constant(null));

            return (double)totalProbes / hashCodes.size();
        }

        private long countProbes(Function<? super Integer, ?> endMarkerFunction) {
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
