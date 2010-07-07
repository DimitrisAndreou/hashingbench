package hashing;

import java.util.Iterator;

public interface Prober {
    Iterator<Integer> probeSequence(int hashCode, int tableSize);
}
