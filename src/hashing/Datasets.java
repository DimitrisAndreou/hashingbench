package hashing;

import java.util.Random;

public enum Datasets implements Dataset {
    OBJECTS() {
        public Object create(int index) { return new Object(); }
    },
    INTEGERS() {
        public Object create(int index) { return Integer.valueOf(index); }
    },
    RND_INTEGERS() {
        private final Random random = new Random(0);
        public Object create(int index) {
            if (index == 0) random.setSeed(0);
            return random.nextInt();
        }
    },
    STRINGS() {
        public Object create(int index) { return Integer.toString(index); }
    };
}

