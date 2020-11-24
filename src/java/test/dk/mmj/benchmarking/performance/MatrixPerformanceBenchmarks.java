package dk.mmj.benchmarking.performance;


import org.openjdk.jmh.annotations.*;

public class MatrixPerformanceBenchmarks {

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 2, batchSize = 5)
    @Warmup(iterations = 5)
    public void init(){

    }

}
