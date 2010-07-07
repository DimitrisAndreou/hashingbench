package hashing.bench;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Schema;
import hashing.Dataset;
import hashing.Datasets;
import hashing.FakeChainHashtable;
import hashing.Scrambler;
import hashing.Scramblers;
import java.util.Arrays;
import java.util.Map;

public class ChainHashBench {
    enum Vars {
        /** The scrambling function (which scrambles the hashCode) */
        SCRAMBLER,
        /** Size of table (power of two, filled up to 75%) */
        N,
        /** The dataset that is inserted (see Datasets enum) */
        DATASET,
        /** Average chain length (ignoring empty chains) */
        AVG_CHAIN,
        /** Standard deviation of chain length (ignoring empty chains)*/
        STDDEV_CHAIN
    }

    private static Schema schema() {
        return new Schema().
            add(Vars.SCRAMBLER, DataTypes.string(20)).
            add(Vars.N, DataTypes.INTEGER).
            add(Vars.DATASET, DataTypes.SMALL_STRING).
            add(Vars.AVG_CHAIN, DataTypes.DOUBLE).
            add(Vars.STDDEV_CHAIN, DataTypes.DOUBLE);
    }

    public static void runBenchmark(Aggregator aggr,
            Iterable<Scrambler> scramblers,
            Iterable<Integer> bitLengths,
            Iterable<Dataset> datasets) {
        for (Scrambler scrambler : scramblers) {
            for (int bits : bitLengths) {
                int tableSize = 1 << bits;
                for (Dataset dataset : datasets) {
                    int threshold = (int)(0.75 * tableSize) - 1;
                    FakeChainHashtable hashtable = new FakeChainHashtable(tableSize, threshold, scrambler);
                    for (int i = 0; i < threshold; i++) {
                        Object o = dataset.create(i);
                        hashtable.put(o);
                    }

                    double[] results = hashtable.computeAverageChainLengthAndStddev();
                    double avgChainLength = results[0];
                    double stddev = results[1];

                    Record record = new Record().
                            add(Vars.SCRAMBLER, scrambler).
                            add(Vars.N, bits).
                            add(Vars.DATASET, dataset).
                            add(Vars.AVG_CHAIN, avgChainLength).
                            add(Vars.STDDEV_CHAIN, stddev);
                    System.out.println(record);
                    aggr.record(record);
                }
            }
        }
    }

    public static void main(String[] args) {
        Database db = DbFactories.localDerby().getOrCreate(Db.CHAINHASH_DB);
        Aggregator aggr = db.forceCreate(schema(), Db.CHAINHASH_AGGR);

        runBenchmark(aggr,
                Arrays.<Scrambler>asList(Scramblers.values()),
                ImmutableList.of(4, 8, 12, 16, 20),
                Arrays.<Dataset>asList(Datasets.values()));
        db.shutDown();
    }
}
