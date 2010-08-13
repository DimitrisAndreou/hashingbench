package hashing.bench;

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
import hashing.bench.BloomBench.Vars;
import hashing.Datasets;
import java.io.File;
import java.io.IOException;

public class BloomAnalysis {
    public static void main(String[] args) throws IOException {
        Database db = DbFactories.localDerby().get(Db.BLOOM_DB);
        Aggregator aggr = db.get(Db.BLOOM_AGGR);

        for (int N : aggr.domainOf(Vars.N, Integer.class)) {
            Records records = aggr.
                    ordered(Orders.asc(Vars.FALSE_POSITIVES)).
                    filtered(Filters.eq(Vars.b, 8)).
                    filtered(Filters.eq(Vars.N, N)).
                    filtered(Filters.eq(Vars.DATASET, Datasets.STRINGS)).
                    averageOf(Vars.FALSE_POSITIVES).
                    per(Vars.MULTIHASHER);
            Diagram diagram = DiagramFactory.newDiagram(records);
            new ChartFactory(diagram).newBarChart().write(800, 800, "png", new File("bloom_" + N + ".png"));
        }

        Records records = aggr.
                ordered(Orders.asc(Vars.DATASET), Orders.asc(Vars.N), Orders.asc(Vars.b)).
                averageOf(Vars.FALSE_POSITIVES).
                per(Vars.MULTIHASHER, Vars.b, Vars.N, Vars.DATASET);
        for (Record record : records) {
            System.out.printf("%15s bitsPerElement =%2s N = %5s %22s falsePositives = %s%n",
                    record.get(Vars.DATASET), record.get(Vars.b), record.get(Vars.N), record.get(Vars.MULTIHASHER), record.getValue());
        }

        db.shutDown();
    }
}
