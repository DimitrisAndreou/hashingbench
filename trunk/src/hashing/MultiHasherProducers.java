package hashing;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Random;

public enum MultiHasherProducers implements MultiHasherProducer {
    /**
     * Taken from {@code java.util.Random}.
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
     * Universal hashing.
     */
    UNIVERSAL() {
        private final int a[] = { -334383531, -1748844831, -2116204994, -568712013, 523495494, -264127263 };
        private final int b[] = { 1992541061, -1336925333, -1141303018, 1175233895, 645101469, -1128518908 };

        public MultiHasher produce(int k) {
            return UniversalHashing.create(Arrays.copyOf(a, k), Arrays.copyOf(b, k));
        }
    },

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
