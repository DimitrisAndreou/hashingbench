package hashing.bench;

import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Filters;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import hashing.bench.ChainHashBench.Vars;

public class ChainHashAnalysis {
    public static void main(String[] args) {
        Database db = DbFactories.localDerby().get(Db.CHAINHASH_DB);
        Aggregator aggr = db.get(Db.CHAINHASH_AGGR);

        Records records = aggr.
                ordered(Orders.asc(Vars.DATASET), Orders.asc(Vars.AVG_CHAIN)).
                filtered(Filters.eq(Vars.N, 20)).
                averageOf(Vars.AVG_CHAIN).
                per(Vars.SCRAMBLER, Vars.DATASET, Vars.N);
        for (Record record : records) {
            System.out.printf("%20s %15s N=%s avgChain=%1.3f%n",
                    record.get(Vars.SCRAMBLER), record.get(Vars.DATASET), record.get(Vars.N), record.getValue());
        }
        db.shutDown();
    }
}
