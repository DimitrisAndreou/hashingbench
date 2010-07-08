package hashing;

/**
 * An (unbounded) index-based object supplier.
 */
public interface Dataset {
    /**
     * Returns the ith object of this dataset. 
     */
    Object create(int index);
}
