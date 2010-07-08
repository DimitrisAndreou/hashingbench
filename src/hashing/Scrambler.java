package hashing;

/**
 * An object that can scramble (=arbitrarily transform) a hash code.
 */
public interface Scrambler {
    /**
     * Scrambles a hashCode in an arbitrary way. 
     */
    int scramble(int hashCode);
}
