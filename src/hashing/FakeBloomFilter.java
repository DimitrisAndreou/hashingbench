package hashing;

import java.util.BitSet;

/**
 * A simplistic Bloom filter implementation, useful for benchmarking {@code MultiHasher}s
 * regarding false positive rates.
 */
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

    /**
     * Creates a Bloom filter with specified capacity (that is, expected number of insertions),
     * bits per element, and a {@code MultiHasher} (derived from the supplied
     * {@code MultiHasherProducer}) with the "optimal" number of hashes
     * per insertion. The allocated bits will at least be {@code capacity * bitsPerElement}. 
     *
     * @param capacity the expected number of insertions for the created Bloom filter
     * @param bitsPerElement the number of bits per insertion to allocate
     * @param multihasherProducer the producer of the MultiHasher to be used
     * @return a new Bloom filter
     */
    public static FakeBloomFilter createWithCapacity(int capacity, int bitsPerElement,
            MultiHasherProducer multihasherProducer) {
        BitSet bits = new BitSet(capacity * bitsPerElement);

        return new FakeBloomFilter(bits, multihasherProducer, optimalHashesCount(bits.size(), capacity));
    }

    /**
     * Creates a Bloom filter with specified capacity (that is, expected number of insertions),
     * bits per element, and a {@code MultiHasher} (derived from the supplied
     * {@code MultiHasherProducer}) with the specified number of hashes
     * per insertion. The allocated bits will at least be {@code capacity * bitsPerElement}.
     *
     * @param capacity the expected number of insertions for the created Bloom filter
     * @param bitsPerElement the number of bits per insertion to allocate
     * @param multihasherProducer the producer of the MultiHasher to be used
     * @param hashesCount the number of hashes per insertion
     * @return a new Bloom filter
     */
    public static FakeBloomFilter createWithCapacity(int capacity, int bitsPerElement,
            MultiHasherProducer multihasherProducer, int hashesCount) {
        return new FakeBloomFilter(new BitSet(capacity * bitsPerElement), multihasherProducer, hashesCount);
    }

    /**
     * Returns the number of hashes per insertion.
     */
    public int getHashesCount() {
        return hashesCount;
    }

    /**
     * Returns the {@code MultiHasher} object used by this Bloom filter.
     */
    public MultiHasher getMultiHasher() {
        return multiHasher;
    }

    private static final double ln2 = Math.log(2);
    private static int optimalHashesCount(int bits, int insertions) {
        return Math.max(1, (int)Math.ceil(ln2 * bits / insertions));
    }

    /**
     * Puts an element in this Bloom filter.
     */
    public void put(Object o) {
        int[] hashes = new int[hashesCount];
        multiHasher.multihash(o, hashes, bits.size());
        for (int hash : hashes) {
            bits.set(hash);
        }
        insertions++;
    }

    /**
     * Returns whether it is possible that this Bloom filter contains the specified element
     * (might yield false positives, but never yields false negatives).
     */
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

    /**
     * Returns the <em>expected</em> estimate of false positive ratio, assuming that the hash functions
     * used are <em>independent</em> and have a <em>uniform distribution</em>
     * over the bits of this Bloom filter. This estimate is oblivious and invariant to any insertion performed
     * in this Bloom filter.
     * 
     * @return the expected false positive rate, from {@code 0.0} to {@code 1.0}
     */
    public double computeExpectedFalsePositiveRate() {
        return (double)Math.pow(1 - Math.exp(-hashesCount * ((double)insertions / bits.size())), hashesCount);
    }

    /**
     * Returns an estimate of false positive ratio of this Bloom filter, assuming that the hash functions
     * used are <em>independent</em> and have a <em>uniform distribution</em>
     * over the bits of this Bloom filter. This estimate is more informed than
     * {@link #computeExpectedFalsePositiveRate() } since this considers the ratio of 1s
     * in this Bloom filter, i.e. recognizes the effects of insertions.
     *
     * @return the expected false positive rate, from {@code 0.0} to {@code 1.0}
     */
    public double computeFalsePositiveRate() {
        return Math.pow((double)bits.cardinality() / bits.size(), hashesCount);
    }
}
