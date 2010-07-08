package hashing;

import java.util.Arrays;
import java.util.Random;

/**
 * Various {@link MultiHasherProducer} implementations.
 */
public enum MultiHasherProducers implements MultiHasherProducer {
    /**
     * A {@code MultiHasherProducer} that creates {@code MultiHasher}s that
     * seeds a {@link Random} object with the {@code hashCode()} of an object, 
     * and then generates as many random integers as required.
     */
    RANDOM() {
        public MultiHasher produce(int k) {
            return new MultiHasher() {
                private final Random random = new Random(0);

                public void multihash(Object o, int[] output, int tableSize) {
                    random.setSeed(o.hashCode());
                    for (int i = 0; i < output.length; i++) {
                        output[i] = Modulo.mod(random.nextInt(), tableSize);
                    }
                }
            };
        }
    },

    /**
     * A {@code MultiHasherProducer} that creates {@code MultiHasher}s based on
     * universal hashing. This implementation can only serve up to 6 hashes per object.
     */
    UNIVERSAL() {
        private final int a[] = { -334383531, -1748844831, -2116204994, -568712013, 523495494, -264127263 };
        private final int b[] = { 1992541061, -1336925333, -1141303018, 1175233895, 645101469, -1128518908 };

        public MultiHasher produce(int k) {
            return UniversalHashing.create(Arrays.copyOf(a, k), Arrays.copyOf(b, k));
        }
    },

    /**
     * A {@code MultiHasherProducer} similar to {@link #RANDOM}, with the difference
     * that the random numbers generated are also scrambled by {@link Scramblers#JENKINS}.
     */
    RANDOM_JENKINS() {
        public MultiHasher produce(int k) {
            return new MultiHasher() {
                private final Random random = new Random(0);

                public void multihash(Object o, int[] output, int tableSize) {
                    random.setSeed(o.hashCode());
                    for (int i = 0; i < output.length; i++) {
                        output[i] = Modulo.mod(Scramblers.JENKINS.scramble(random.nextInt()), tableSize);
                    }
                }
            };
        }
    },

    /**
     * A {@code MultiHasherProducer} suggested by Ben Manes. It uses
     * the scrambled (via {@link Scramblers#CONCURRENTHASHMAP}) hashCode()
     * as the start position, then adds the hashCode() (modulo table size) to the last
     * position to yield as many hashes as required.
     */
    RANDOM_MANES() {
        private final MultiHasher instance = new MultiHasher() {
            public void multihash(Object o, int[] output, int tableSize) {
                int hashCode = o.hashCode();
                int probe = 1 + Math.abs(hashCode % tableSize);

                int h = Scramblers.CONCURRENTHASHMAP.scramble(hashCode);
                for (int i=0; i < output.length; i++) {
                    output[i] = Math.abs(h ^ i*probe) % tableSize;
                }
            }
        };
        public MultiHasher produce(int k) {
            return instance;
        }
    }

    ;
}
