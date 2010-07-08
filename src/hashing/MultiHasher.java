package hashing;

/**
 * An object representing multiple hash functions.
 */
public interface MultiHasher {
    /**
     * Produces {@code output.length} number of hashes for a given object. All
     * hashes must be in the range {@code 0} (inclusive) to {@code tableSize} (exclusive).
     */
    void multihash(Object o, int[] output, int tableSize);
}
