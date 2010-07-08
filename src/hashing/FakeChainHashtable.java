package hashing;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;

/**
 * A fake hashtable with chaining, useful for benchmarking the effects of {@code Scrambler}
 * functions in chain lengths.
 */
public class FakeChainHashtable {
    private final Map<Integer, Integer> bucketToLength;
    private final Scrambler scrambler;
    private final int mask;

    /**
     * Constructs a FakeChainHashtable of the given (power of two) table size and
     * {@code Scrambler} function.
     *
     * @param tableSize the (power of two) table size of the hashtable
     * @param scrambler the scrambler function to use, to scramble the {@code hashCode()}
     * of inserted elements upon insertion.
     */
    public FakeChainHashtable(int tableSize, Scrambler scrambler) {
        Preconditions.checkArgument((tableSize & (tableSize - 1)) == 0, "Table size must be a power of two");
        this.mask = tableSize - 1;
        this.bucketToLength = Maps.newHashMap();
        this.scrambler = scrambler;
    }

    /**
     * Puts an element to this hashtable. This method gets the element's {@code hashCode()},
     * scrambles it via the scrambler of this hashtable, maps the result to a bucket by selecting
     * as many as required low-end bits, and increases the counter of the resulting bucket.
     */
    public void put(Object o) {
        int bucket = scrambler.scramble(o.hashCode()) & mask;

        Integer chainLength = Objects.firstNonNull(bucketToLength.get(bucket), Integer.valueOf(0));
        bucketToLength.put(bucket, chainLength + 1);
    }

    /**
     * Returns the scrambler used by this hashtable.
     */
    public Scrambler getScrambler () {
        return scrambler;
    }

    /**
     * Returns the average chain length (ignoring empty chains) and the standard deviation
     * of that, as a two-element double array.
     */
    public double[] computeAverageChainLengthAndStddev() {
        int totalChainLength = 0;
        for (int chainLength : bucketToLength.values()) {
            totalChainLength += chainLength;
        }
        double avgChainLength = (double)totalChainLength / bucketToLength.size();

        double variance = 0.0;
        //this accumulates the variance of non-empty chains
        for (int chainLength : bucketToLength.values()) {
            double diff = Math.abs(chainLength - avgChainLength);
            variance += diff * diff;
        }
        variance /= bucketToLength.size();
        double stddev = Math.sqrt(variance);

        return new double[] { avgChainLength, stddev };
    }
}

