package hashing;

class Modulo {
    private Modulo() { }
    
    /**
     * Maps via modulo arithmetic an arbitrary integer i to an integer in [0...M).
     *
     * @param i an arbitrary integer
     * @param M a positive integer (the modulo)
     * @return {@code i % M}, if i is positive, or {@code (i % M) + i} otherwise
     */
    static int mod(int i, int M) {
        return (i %= M) >= 0 ? i : i + M;
    }
}
