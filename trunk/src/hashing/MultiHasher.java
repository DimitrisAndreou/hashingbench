package hashing;

public interface MultiHasher {
    void multihash(Object o, int[] output, int tableSize);
}
