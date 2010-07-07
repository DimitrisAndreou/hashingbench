package hashing;

import java.security.SecureRandom;
import java.util.List;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import com.google.common.primitives.Ints;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;
import static org.junit.Assert.*;

@RunWith(Theories.class)
public class MultiHasherTest {

    @DataPoints
    public static final MultiHasherProducer[] multiHasherProducers = MultiHasherProducers.values();

    @DataPoints
    public static final Dataset[] dataSets = Datasets.values();

    @DataPoints
    public static final int[] tableSizes = { 1, 2, 4, 8, 16, 32, 64, 128 };

    @Theory
    public void testConsistency(MultiHasherProducer multiHasherProducer, Dataset dataset, int tableSize) {
        for (int k = 1; k < 6; k++) {
            MultiHasher multiHasher = multiHasherProducer.produce(k);

            int[] out1 = new int[k];
            int[] out2 = new int[k];
            List<Integer> view1 = Ints.asList(out1);
            List<Integer> view2 = Ints.asList(out2);
            for (int i = 0; i < 1000; i++) {
                Object o = dataset.create(i);
                multiHasher.multihash(o, out1, tableSize);
                multiHasher.multihash(o, out2, tableSize);
                assertEquals(view1, view2);
            }
        }
    }
}
