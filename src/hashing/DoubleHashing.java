package hashing;

/**
 * A prober based on double hashing. This only works properly in power-of-two tables.
 */
class DoubleHashing extends LinearProber {
    private final Scrambler scrambler;

    /**
     * Constructs a DoubleHashing instance where the specified {@code Scrambler} plays the
     * role of the second hash function (the first being the {@code hashCode()} of an element).
     */
    DoubleHashing(Scrambler scrambler) {
        this.scrambler = scrambler;
    }

    /**
     * Scrambles the hashCode and ORs the result with 1 to compute the probing step.
     * Ensuring the step odd is necessary, so that the step is a relative prime with
     * a power-of-two table size, which guarantees the proper function of this prober.
     */
    @Override protected int step(int hashCode) {
        return scrambler.scramble(hashCode) | 1; 
    }
}