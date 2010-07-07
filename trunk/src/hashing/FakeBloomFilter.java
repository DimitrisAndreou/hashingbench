package hashing;

import java.util.BitSet;

public class FakeBloomFilter {
    private final BitSet bits;
    private final MultiHasher multiHasher;
    private final int hashesCount;
    private int insertions;
    
    private FakeBloomFilter(BitSet bits, MultiHasherProducer multihasherProducer, int hashesCount) {
        this.bits = bits;
        this.multiHasher = multihasherProducer.produce(hashesCount);
        this.hashesCount = hashesCount;
    }

    public static FakeBloomFilter createWithCapacity(int capacity, int bitsPerElement, MultiHasherProducer multihasherProducer) {
        BitSet bits = new BitSet(capacity * bitsPerElement);

        return new FakeBloomFilter(bits, multihasherProducer, optimalHashesCount(bits.size(), capacity));
    }

    public static FakeBloomFilter createWithCapacity(int capacity, int bitsPerElement, MultiHasherProducer multihasherProducer, int hashesCount) {
        return new FakeBloomFilter(new BitSet(capacity * bitsPerElement), multihasherProducer, hashesCount);
    }

    public int getHashesCount() {
        return hashesCount;
    }

    public MultiHasher getMultiHasher() {
        return multiHasher;
    }

    private static final double ln2 = Math.log(2);
    private static int optimalHashesCount(int bits, int insertions) {
        return Math.max(1, (int)Math.ceil(ln2 * bits / insertions));
    }

    public void put(Object o) {
        int[] hashes = new int[hashesCount];
        multiHasher.multihash(o, hashes, bits.size());
        for (int hash : hashes) {
            bits.set(hash);
        }
        insertions++;
    }

    public boolean mightContain(Object o) {
        int[] hashes = new int[hashesCount];
        multiHasher.multihash(o, hashes, bits.size());
        for (int hash : hashes) {
            if (!bits.get(hash)) {
                return false;
            }
        }
        return true;
    }

    //this is the expected false positive rate, assuming the hash functions are independent
    //and uniform (a big assumption!)
    public double computeExpectedFalsePositiveRate() {
        return (double)Math.pow(1 - Math.exp(-hashesCount * ((double)insertions / bits.size())), hashesCount);
    }

    //this is more informed than the former, since it considers the actual fraction of 1-bits,
    //instead of using the expected number of them
    public double computeActualFalsePositiveRate() {
        return Math.pow((double)bits.cardinality() / bits.size(), hashesCount);
    }
}
