package hashing.bench;

import com.google.common.collect.ImmutableList;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Schema;
import hashing.Dataset;
import hashing.Datasets;
import hashing.FakeBloomFilter;
import hashing.MultiHasherProducer;
import hashing.MultiHasherProducers;
import java.util.Arrays;

public class BloomBench {
    enum Vars {
        /** The number of hashes per element */
        K,
        /** The multihasher */
        MULTIHASHER,
        /** Number of insertions */
        N,
        /** Bits per element */
        b,
        /** The dataset that is inserted (see Datasets enum) */
        DATASET,
        /** False positive ratio derived experimentally */
        FALSE_POSITIVES,
        /** False positive ratio derived theoretically */
        EXPECTED_FALSE_POSITIVES,
    }

    private static Schema schema() {
        return new Schema().
            add(Vars.K, DataTypes.INTEGER).
            add(Vars.MULTIHASHER, DataTypes.string(20)).
            add(Vars.N, DataTypes.INTEGER).
            add(Vars.b, DataTypes.INTEGER).
            add(Vars.DATASET, DataTypes.SMALL_STRING).
            add(Vars.FALSE_POSITIVES, DataTypes.DOUBLE).
            add(Vars.EXPECTED_FALSE_POSITIVES, DataTypes.DOUBLE);
    }

    public static void runBenchmark(Aggregator aggr,
            Iterable<MultiHasherProducer> multihasherProducers,
            Iterable<Integer> insertionsIterable,
            Iterable<Integer> bitsPerElementIterable,
            Iterable<Dataset> datasets) {
        for (MultiHasherProducer multihasherProducer : multihasherProducers) {
            for (int insertions : insertionsIterable) {
                for (int bitsPerElement : bitsPerElementIterable) {
                    for (Dataset dataset : datasets) {
                        FakeBloomFilter bloomFilter = FakeBloomFilter.createWithCapacity(insertions, bitsPerElement, multihasherProducer);
                        for (int i = 0; i < insertions; i++) {
                            bloomFilter.put(dataset.create(i));
                        }

                        int positives = 0;
                        int len = insertions * 10;

                        for (int i = insertions; i < len; i++) {
                            if (bloomFilter.mightContain(dataset.create(i))) {
                                positives++;
                            }
                        }
                        double falsePositiveRate = (double)positives / (len - insertions);

                        Record record = new Record().
                                add(Vars.K, bloomFilter.getHashesCount()).
                                add(Vars.MULTIHASHER, multihasherProducer).
                                add(Vars.N, insertions).
                                add(Vars.b, bitsPerElement).
                                add(Vars.DATASET, dataset).
                                add(Vars.FALSE_POSITIVES, falsePositiveRate).
                                add(Vars.EXPECTED_FALSE_POSITIVES, bloomFilter.computeFalsePositiveRate());
                        System.out.println(record);
                        aggr.record(record);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Database db = DbFactories.localDerby().getOrCreate(Db.BLOOM_DB);
        Aggregator aggr = db.forceCreate(schema(), Db.BLOOM_AGGR);

        runBenchmark(aggr,
                Arrays.<MultiHasherProducer>asList(MultiHasherProducers.values()),
                ImmutableList.of(1 << 8, 1 << 10, 1 << 12, 1 << 14, 1 << 16),
                ImmutableList.of(4, 6, 8),
                Arrays.<Dataset>asList(Datasets.values()));
        db.shutDown();
    }
}
