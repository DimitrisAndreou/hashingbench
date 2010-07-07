package hashing;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Map;

public class FakeChainHashtable {
    private final Map<Integer, Integer> bucketToLength;
    private final Scrambler scrambler;
    private final int mask;

    public FakeChainHashtable(int tableSize, int threshold, Scrambler hasher) {
        this.mask = tableSize - 1;
        this.bucketToLength = Maps.newHashMapWithExpectedSize(threshold);
        this.scrambler = hasher;
    }

    public void put(Object o) {
    int bucket = scrambler.scramble(o.hashCode()) & mask;

        Integer chainLength = Objects.firstNonNull(bucketToLength.get(bucket), Integer.valueOf(0));
        bucketToLength.put(bucket, chainLength + 1);
    }

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

