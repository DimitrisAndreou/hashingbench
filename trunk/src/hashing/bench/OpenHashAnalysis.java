package hashing.bench;

import com.google.common.collect.ImmutableList;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Filters;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import gr.forth.ics.jbenchy.diagram.Diagram;
import gr.forth.ics.jbenchy.diagram.DiagramFactory;
import gr.forth.ics.jbenchy.diagram.jfreechart.ChartFactory;
import hashing.bench.OpenHashBench.Vars;
import java.io.File;
import java.io.IOException;

public class OpenHashAnalysis {
    public static void main(String[] args) throws IOException {
        Database db = DbFactories.localDerby().get(Db.OPENHASH_DB);
        Aggregator aggr = db.get(Db.OPENHASH_AGGR);

        for (Object var : ImmutableList.of(Vars.AVG_PROBES_SUCCESS, Vars.AVG_PROBES_FAIL)) {
            System.out.println("*******");
            System.out.println("*******");
            Records records = aggr.
                    ordered(Orders.asc(Vars.DATASET), Orders.asc(Vars.N), Orders.asc(var)).
//                    filtered(Filters.eq(Vars.N, 16)).
                    averageOf(var).
                    per(Vars.DATASET, Vars.PROBER, Vars.N);
            for (Record record : records) {
                System.out.printf("%22s %15s %3s %15s=%1.3f%n",
                        record.get(Vars.PROBER), record.get(Vars.DATASET), record.get(Vars.N), var, record.getValue());
            }
        }
        db.shutDown();
    }
}
