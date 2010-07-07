package hashing;

public enum Scramblers implements Scrambler {
    /**
     * Simply returns the argument's hashCode.
     */
    IDENTITY() {
        public int scramble(int h) {
            return h;
        }
    },

    /**
     * Taken from {@code java.util.HashMap}.
     */
    HASHMAP() {
        public int scramble(int h) {
            h ^= (h >>> 20) ^ (h >>> 12);
            return h ^ (h >>> 7) ^ (h >>> 4);
        }
    },

    HASHMAP_OLD() {
        public int scramble(int h) {
		    h += ~(h << 9);
		    h ^= (h >>> 14);
		    h += (h << 4);
		    h ^= (h >>> 10);
            return h;
		}
	},

    /**
     * Taken from {@code java.util.IdentityHashMap}.
     */
    IDENTITYHASHMAP() {
        public int scramble(int h) {
            return ((h << 1) - (h << 8));
        }
    },

    /**
     * Taken from {@code java.util.ConcurrentHashMap}.
     */
    CONCURRENTHASHMAP() {
        public int scramble(int h) {
            h += (h << 15) ^ 0xffffcd7d;
            h ^= (h >>> 10);
            h += (h << 3);
            h ^= (h >>> 6);
            h += (h << 2) + (h << 14);
            return h ^ (h >>> 16);
        }
    },

    /**
     * Taken from <a href="http://www.concentric.net/~Ttwang/tech/inthash.htm">Integer Hash Function</a>
     */
    WANG() {
        public int scramble(int h) {
            h = ~h + (h << 15); // key = (key << 15) - key - 1;
            h = h ^ (h >>> 12);
            h = h + (h << 2);
            h = h ^ (h >>> 4);
            h = h * 2057; // key = (key + (key << 3)) + (key << 11);
            h = h ^ (h >>> 16);
            return h;
        }
    },

    /**
     * Taken from <a href="http://www.burtleburtle.net/bob/hash/doobs.html>http://www.burtleburtle.net/bob/c/lookup2.c</a>
     */
    JENKINS() {
        static final int b4 = 255 << 24;
        static final int b3 = 255 << 16;
        static final int b2 = 255 << 8;
        static final int b1 = 255;

        public int scramble(int h) {
            int a = 0x9e3779b9;  /* the golden ratio; an arbitrary value */
            int b = a;

            a += h & b4;
            a += h & b3;
            a += h & b2;
            a += h & b1;
            return mix(a, b, 1013);
        }

        private int mix(int a, int b, int c) {
            a -= b; a -= c; a ^= (c >> 13);
            b -= c; b -= a; b ^= (a << 8);
            c -= a; c -= b; c ^= (b >> 13);
            a -= b; a -= c; a ^= (c >> 12);
            b -= c; b -= a; b ^= (a << 16);
            c -= a; c -= b; c ^= (b >> 5);
            a -= b; a -= c; a ^= (c >> 3);
            b -= c; b -= a; b ^= (a << 10);
            c -= a; c -= b; c ^= (b >> 15);
            return c;
        }
    }
    ;
}