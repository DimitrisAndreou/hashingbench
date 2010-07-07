package hashing;

import java.util.Iterator;
import java.util.BitSet;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Theories.class)
public class ProberTest {
    @DataPoints
    public static final Prober[] probers = Probers.values();
    
    @DataPoints
    public static final int[] tableSizes = { 1, 2, 4, 8, 16, 32, 64, 128 };

    @Theory
    public void testProbeAllPositions(Prober prober, int tableSize) {
        for (int h = 0; h < 10; h++) {
            BitSet bitSet = new BitSet(tableSize);
            bitSet.set(0, tableSize);
            Iterator<Integer> seq = prober.probeSequence(h, tableSize);

            for (int i = 0; i < tableSize; i++) {
                bitSet.set(seq.next(), false);
            }
            assertEquals(0, bitSet.cardinality()); //all positions have been probed
        }
    }

    @Theory public void testProbeConsistently(Prober prober, int tableSize) {
        for (int h = 0; h < 10; h++) {
            Iterator<Integer> seq1 = prober.probeSequence(h, tableSize);
            Iterator<Integer> seq2 = prober.probeSequence(h, tableSize);

            for (int i = 0; i < tableSize; i++) {
                assertEquals(seq1.next(), seq2.next());
            }
        }
    }
}
