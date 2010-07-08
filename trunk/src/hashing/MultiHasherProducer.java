package hashing;

/**
 * A producer of {@link MultiHasher} objects.
 */
public interface MultiHasherProducer {
    /**
     * Produces a {@code MultiHasher} capable of hashing any object {@code k} times.
     */
    MultiHasher produce(int k);
}
