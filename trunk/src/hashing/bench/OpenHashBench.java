package hashing.bench;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Schema;
import hashing.Dataset;
import hashing.Datasets;
import hashing.FakeOpenAddressingHashtable;
import hashing.FakeOpenAddressingHashtable.ProbeCounter;
import hashing.Prober;
import hashing.Probers;
import java.util.Arrays;

public class OpenHashBench {
    enum Vars {
        /** The prober function */
        PROBER,
        /** Size of table (power of two, filled up to 75%) */
        N,
        /** The dataset that is inserted (see Datasets enum) */
        DATASET,
        /** Average probes for successful query */
        AVG_PROBES_SUCCESS,
        /** Average probes for unsuccessful query when we have a hashCode() collision */
        AVG_PROBES_FAIL,
    }

    private static Schema schema() {
        return new Schema().
            add(Vars.PROBER, DataTypes.string(21)).
            add(Vars.N, DataTypes.INTEGER).
            add(Vars.DATASET, DataTypes.SMALL_STRING).
            add(Vars.AVG_PROBES_SUCCESS, DataTypes.DOUBLE).
            add(Vars.AVG_PROBES_FAIL, DataTypes.DOUBLE);
    }

    public static void runBenchmark(Aggregator aggr,
            Iterable<Prober> probers,
            Iterable<Integer> bitLengths,
            Iterable<Dataset> datasets) {
        for (Prober prober : probers) {
            for (int bits : bitLengths) {
                int tableSize = 1 << bits;
                for (Dataset dataset : datasets) {
                    int threshold = (tableSize >> 1) - 1; //less than half full
                    FakeOpenAddressingHashtable hashtable = new FakeOpenAddressingHashtable(tableSize, prober);
                    for (int i = 0; i < threshold; i++) {
                        Object o = dataset.create(i);
                        hashtable.put(o);
                    }

                    ProbeCounter probeCounter = hashtable.newProbeCounter();
                    double avgProbs_success = probeCounter.computeAverageProbesOnSuccess();
                    double avgProbs_fail = probeCounter.computeAverageProbesOnCollisions();

                    Record record = new Record().
                            add(Vars.PROBER, prober).
                            add(Vars.N, bits).
                            add(Vars.DATASET, dataset).
                            add(Vars.AVG_PROBES_SUCCESS, avgProbs_success).
                            add(Vars.AVG_PROBES_FAIL, avgProbs_fail);
                    System.out.println(record);
                    aggr.record(record);
                }
            }
        }
    }

    public static void main(String[] args) {
        Database db = DbFactories.localDerby().getOrCreate(Db.OPENHASH_DB);
        Aggregator aggr = db.forceCreate(schema(), Db.OPENHASH_AGGR);

        runBenchmark(aggr,
                Arrays.<Prober>asList(Probers.values()),
                ImmutableList.of(4, 8, 12, 16),
                Arrays.<Dataset>asList(Datasets.values()));
        db.shutDown();
    }
}
