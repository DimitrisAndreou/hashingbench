package hashing;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

public enum Probers implements Prober {
    /**
     * Linear probing.
     */
    LINEAR(new LinearProbing()),
    
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
     * Double hashing, using Scramblers.HASHMAP as the second hash function.
     */
    DBL_HASHMAP(new DoubleHashing(Scramblers.HASHMAP)),

    /**
     * Double hashing, using Scramblers.HASHMAP_OLD as the second hash function.
     */
    DBL_HASHMAP_OLD(new DoubleHashing(Scramblers.HASHMAP_OLD)),

    /**
     * Double hashing, using Scramblers.CONCURRENTHASHMAP as the second hash function.
     */
    DBL_CONCURRENTHASHMAP(new DoubleHashing(Scramblers.CONCURRENTHASHMAP)),

    /**
     * Double hashing, using Scramblers.IDENTITYHASHMAP as the second hash function.
     */
    DBL_IDENTITYHASHMAP(new DoubleHashing(Scramblers.IDENTITYHASHMAP)),

    /**
     * Double hashing, using Scramblers.JENKINS as the second hash function.
     */
    DBL_JENKINS(new DoubleHashing(Scramblers.JENKINS)),

    /**
     * Double hashing, using Scramblers.WANG as the second hash function.
     */
    DBL_WANG(new DoubleHashing(Scramblers.WANG))
    ;

    private static class LinearProbing implements Prober {
        LinearProbing() { }

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

    private static class DoubleHashing extends LinearProbing {
        private final Scrambler scrambler;

        DoubleHashing(Scrambler scrambler) {
            this.scrambler = scrambler;
        }

        @Override protected int step(int hashCode) {
            return scrambler.scramble(hashCode) | 1; //in order to be relative prime with a power-of-two table size
        }
    }

    private final Prober delegate;

    Probers(Prober delegate) {
        this.delegate = delegate;
    }

    public Iterator<Integer> probeSequence(int hashCode, int tableSize) {
        return delegate.probeSequence(hashCode, tableSize);
    }
}

