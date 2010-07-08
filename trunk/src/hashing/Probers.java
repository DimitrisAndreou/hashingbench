package hashing;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

/**
 * Various {@link Prober} implementations.
 */
public enum Probers implements Prober {
    /**
     * Linear probing.
     */
    LINEAR(new LinearProber()),
    
    /**
     * Quadratic probing.
     */
    QUADRATIC(new Prober() {
        public Iterator<Integer> probeSequence(final int hashCode, final int tableSize) {
            return new AbstractIterator<Integer>() {
                int i = Modulo.mod(hashCode, tableSize);
                int j = 0;
                protected Integer computeNext() {
                    try {
                        return i;
                    } finally {
                        j = (j + 1) % tableSize;
                        i = (i + j) % tableSize;
                    }
                }
            };
        }
    }),

    /**
     * Double hashing, using {@link Scramblers#HASHMAP} as the second hash function.
     */
    DBL_HASHMAP(new DoubleHashing(Scramblers.HASHMAP)),

    /**
     * Double hashing, using {@link Scramblers#HASHMAP_OLD} as the second hash function.
     */
    DBL_HASHMAP_OLD(new DoubleHashing(Scramblers.HASHMAP_OLD)),

    /**
     * Double hashing, using {@link Scramblers#CONCURRENTHASHMAP} as the second hash function.
     */
    DBL_CONCURRENTHASHMAP(new DoubleHashing(Scramblers.CONCURRENTHASHMAP)),

    /**
     * Double hashing, using {@link Scramblers#IDENTITYHASHMAP} as the second hash function.
     */
    DBL_IDENTITYHASHMAP(new DoubleHashing(Scramblers.IDENTITYHASHMAP)),

    /**
     * Double hashing, using {@link Scramblers#JENKINS} as the second hash function.
     */
    DBL_JENKINS(new DoubleHashing(Scramblers.JENKINS)),

    /**
     * Double hashing, using {@link Scramblers#WANG} as the second hash function.
     */
    DBL_WANG(new DoubleHashing(Scramblers.WANG))
    ;

    private final Prober delegate;

    Probers(Prober delegate) {
        this.delegate = delegate;
    }

    public Iterator<Integer> probeSequence(int hashCode, int tableSize) {
        return delegate.probeSequence(hashCode, tableSize);
    }
}

