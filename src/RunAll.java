import hashing.bench.BloomBench;
import hashing.bench.ChainHashBench;
import hashing.bench.OpenHashBench;

public class RunAll {
    public static void main(String[] args) throws Exception {
        ChainHashBench.main(args);
        OpenHashBench.main(args);
        BloomBench.main(args);
    }
}
