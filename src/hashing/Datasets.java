package hashing;

import java.util.Random;

/**
 * Various {@link Dataset} implementations.
 */
public enum Datasets implements Dataset {
    /**
     * A dataset that returns a {@code new Object()} at each request (ignores the supplied index).
     */
    OBJECTS() {
        public Object create(int index) { return new Object(); }
    },

    /**
     * A dataset that returns the {@code Integer} object corresponding to the supplied index.
     */
    INTEGERS() {
        public Object create(int index) { return Integer.valueOf(index); }
    },

    /**
     * A dataset that returns a random Integer (ignores the supplied index).
     */
    RND_INTEGERS() {
        private final Random random = new Random(0);
        public Object create(int index) {
            if (index == 0) random.setSeed(0);
            return random.nextInt();
        }
    },
    /**
     * A dataset that returns the string representation of the supplied index.
     */
    STRINGS() {
        public Object create(int index) { return Integer.toString(index); }
    };
}

