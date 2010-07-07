package hashing.bench;

import com.google.common.collect.ImmutableSet;
import hashing.Dataset;
import hashing.Datasets;
import hashing.FakeBloomFilter;
import hashing.MultiHasher;
import hashing.MultiHasherProducer;
import hashing.MultiHasherProducers;
import hashing.UniversalHashing;
import java.security.SecureRandom;
import java.util.Random;

public class FindBestUniversalFamily {
    public static void main(String[] args) {
        UniversalFamilyProducer producer = new UniversalFamilyProducer();
        final int repeats = 100000;
        final int insertions = 10240;
        final int totalInsertionTests = insertions * 9;
        int minPositives = Integer.MAX_VALUE;
        double ratio = 1.0;
        MultiHasher best = null;
        for (Dataset dataset : ImmutableSet.of(Datasets.OBJECTS)) {
            FakeBloomFilter baseline = FakeBloomFilter.createWithCapacity(insertions, 8, MultiHasherProducers.RANDOM);
            for (int j = 0; j < insertions; j++) {
                baseline.put(dataset.create(j));
            }
            double fp;
            {
                int positives = findFPRate(baseline, dataset, insertions, totalInsertionTests);
                System.out.println("Baseline: " + positives);
                fp = (double)positives / totalInsertionTests;
            }

            for (int i = 0; i < repeats; i++) {
                FakeBloomFilter filter = FakeBloomFilter.createWithCapacity(insertions, 8, producer);
                for (int j = 0; j < insertions; j++) {
                    filter.put(dataset.create(j));
                }

                int positives = findFPRate(filter, dataset, insertions, totalInsertionTests);
                if (positives < minPositives) {
                    minPositives = positives;
                    ratio = (double)minPositives / totalInsertionTests;
                    best = filter.getMultiHasher();
                }
                if (i % (repeats / 100) == 0) {
                    System.out.println(i / (repeats / 100) + "%");
                    System.out.println("Current best: " + best);
                    System.out.println("With f.p. ratio: " + ratio);
                    System.out.println("Difference to optimal: " + Math.abs(ratio - filter.computeExpectedFalsePositiveRate()));
                    System.out.println("Difference to baseline: " + (ratio - fp));
                    System.out.println("positives: " + minPositives + " of " + totalInsertionTests);
                }
            }
            System.out.println("*** OVERALL Best: " + best);
            System.out.println("With f.p. ratio: " + ratio);
        }
    }

    static int findFPRate(FakeBloomFilter filter, Dataset dataset, int insertions, int tests) {
        int positives = 0;
        for (int j = insertions; j < insertions + tests; j++) {
            if (filter.mightContain(dataset.create(j))) positives++;
        }
        return positives;
    }

    static class UniversalFamilyProducer implements MultiHasherProducer {
        final Random random = new SecureRandom();

        public MultiHasher produce(int k) {
            int[] a = new int[k];
            int[] b = new int[k];
            for (int i = 0; i < k; i++) {
                do {
                    a[i] = random.nextInt();
                } while (a[i] == 0);
                b[i] = random.nextInt();
            }
            return UniversalHashing.create(a, b);
        }
    }
}
