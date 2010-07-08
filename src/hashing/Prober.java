package hashing;

import java.util.Iterator;

/**
 * A prober defines a probing sequence for an open-addressing hashtable.
 * @author Jim
 */
public interface Prober {
    /**
     * Returns an iterator that visits each table position <em>once</em>, potentially
     * (but not necessarily) based on the specified hashCode.
     *
     * @param hashCode the hashCode (on which the probing sequence might depend)
     * @param tableSize the table size - the returned iterator must yield all numbers in the
     * range {@code 0} (inclusive) and {@code tableSize} (exclusive)
     * @return the probing sequence
     */
    Iterator<Integer> probeSequence(int hashCode, int tableSize);
}
