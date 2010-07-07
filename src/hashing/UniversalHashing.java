package hashing;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Random;

public class UniversalHashing implements MultiHasher {
    /**
     * Prime smaller than Integer.MAX_VALUE. (Normally must be chosen so that is greater than largest table size.)
     */
    private static final int p = 1999999973;

    private final int[] a;
    private final int[] b;

    UniversalHashing(int[] a, int[] b) {
        this.a = a;
        this.b = b;
    }

    public void multihash(Object o, int[] output, int tableSize) {
        int h = o.hashCode();
        for (int i = 0; i < output.length; i++) {
            output[i] = Modulo.mod(Modulo.mod(a[i] * h + b[i], p), tableSize);
        }
    }

    @Override
    public String toString() {
        return "UniversalHashing[a=" + Arrays.toString(a) + ", b=" + Arrays.toString(b) + "]";
    }

    public static UniversalHashing create(int[] a, int[] b) {
        Preconditions.checkArgument(a.length == b.length);
        for (int x : a) {
            Preconditions.checkArgument(x != 0, "The 'a' constant must not be zero");
        }
        return new UniversalHashing(a, b);
    }

    public static UniversalHashing create(int k, Random random) {
        int[] a = new int[k];
        int[] b = new int[k];
        for (int i = 0; i < k; i++) {
            while ((a[i] = random.nextInt()) == 0)
                continue;
            b[i] = random.nextInt();
        }
        return new UniversalHashing(a, b);
    }
}
