package hashing;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

/**
 * A linear prober (each probing position differs from the previous one by a constant).
 */
class LinearProber implements Prober {
    public LinearProber() { }

    /**
     * Defines the step of this linear prober, perhaps depending on the given hashCode.
     * (A step of 1 defines the usual linear probing).
     */
    protected int step(int hashCode) {
        return 1;
    }
    
    public Iterator<Integer> probeSequence(final int hashCode, final int tableSize) {
        return new AbstractIterator<Integer>() {
            int current = Modulo.mod(hashCode, tableSize);
            final int step = Modulo.mod(step(hashCode), tableSize);
            @Override protected Integer computeNext() {
                try {
                    return current;
                } finally {
                    current = (current + step) % tableSize;
                }
            }
        };
    }
}
