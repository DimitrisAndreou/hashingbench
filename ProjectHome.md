Benchmarks for hashtables with chaining, open addressing, bloom filters.

Implemented bits:

### [Scramblers](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Scramblers.java) ###

  * Identity (e.g. hashCode() unchanged)
  * HashMap's
  * ConcurrentHashMap's
  * IdentityHashMap's
  * Wang's
  * Jenkin's.

### [Probe sequences](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Probers.java) ###

  * Linear probing
  * Quadratic probing
  * Double hashing (using various scramblers to produce the second hash, making sure that's an odd number, so a relative prime compared to a power-of-two table size, so the probing sequence is always a permutation of the table indexes).

### [Multihashers](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/MultiHasherProducers.java) ###

  * Universal hashing (randomly select K hash functions)
  * Seeding a java.util.Random with the hashCode(), then deriving k nextInt()s.
  * Others

## Benchmark on hashtables with chaining ##

Code: [ChainHashBench.java](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/bench/ChainHashBench.java)

**Inputs/outputs**:

[Scrambler](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Scrambler.java) x (size of table) x [Dataset](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Dataset.java) => (average chain length), (standard deviation of average chain length)

## Benchmark on open-addressing hashtables ##

Code: [OpenHashBench.java](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/bench/OpenHashBench.java)

**Inputs/outputs**:

[Prober](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Prober.java) x (size of table) x [Dataset](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Dataset.java) => (average probes in successful query), (average probes in unsuccessful query)

## Benchmark on Bloom filters ##

Code: [BloomBench.java](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/bench/BloomBench.java)

**Inputs/outputs**:


[MultiHasher](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/MultiHasher.java) x (number of insertions) x ( bits per inserted element ) x ( number of hashes - usually computed from the previous two parameters) x [Dataset](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/Dataset.java) => (theoretical false positive rate), (experimental false positive rate)


For the benchmarks, I am using one of my old pet projects, [JBenchy](http://code.google.com/p/jbenchy/), which stores the tuple of each performed experiment (all inputs and outputs) in a table (of a zero configuration local database), and most importantly gives me easy access to GROUP BY statements, so e.g. I can find the "best probing sequence grouped by (per) dataset", etc. It can also create simple diagrams, see [BloomAnalysis.java](http://code.google.com/p/hashingbench/source/browse/trunk/src/hashing/bench/BloomAnalysis.java).