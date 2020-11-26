package dk.mmj.benchmarking.performance;


import dk.mmj.matrix.Matrix;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@SuppressWarnings("unused")
public class MatrixPerformanceBenchmarks {
    private static final BigInteger q = BigInteger.valueOf(10000);
    private static final BigInteger c = BigInteger.valueOf(523);

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void addition(Blackhole blackhole, MatrixBenchmarkState state) {
        state.a.disableConcurrency();
        Matrix add = state.a.add(state.a, q);
        blackhole.consume(add);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void additionConcurrent(Blackhole blackhole, MatrixBenchmarkState state) {
        Matrix add = state.a.add(state.a, q);
        blackhole.consume(add);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void multiplication(Blackhole blackhole, MatrixBenchmarkState state) {
        state.a.disableConcurrency();
        Matrix multiply = state.a.multiply(state.b, q);
        blackhole.consume(multiply);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void multiplicationConcurrent(Blackhole blackhole, MatrixBenchmarkState state) {
        Matrix multiply = state.a.multiply(state.b, q);
        blackhole.consume(multiply);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void multiplicationWithConstant(Blackhole blackhole, MatrixBenchmarkState state) {
        state.a.disableConcurrency();
        Matrix multiply = state.a.multiply(c, q);
        blackhole.consume(multiply);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void multiplicationConcurrentWithConstant(Blackhole blackhole, MatrixBenchmarkState state) {
        Matrix multiply = state.a.multiply(c, q);
        blackhole.consume(multiply);
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void negation(Blackhole blackhole, MatrixBenchmarkState state) {
        state.a.disableConcurrency();
        Matrix neg = state.a.negate(q);
        blackhole.consume(neg);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Measurement(iterations = 1, batchSize = 5)
    @Warmup(iterations = 1)
    public void negationConcurrent(Blackhole blackhole, MatrixBenchmarkState state) {
        Matrix neg = state.a.negate(q);
        blackhole.consume(neg);
    }

    @State(Scope.Benchmark)
    public static class MatrixBenchmarkState {
        private final Random secureRandom = new SecureRandom();
        @Param({"64", "128", "256", "512", "1024", "2048"})
        public int n;
        @Param({"64", "128", "256", "512", "1024", "2048"})
        public int m;

        public Matrix a;
        public Matrix b;

        @Setup(Level.Trial)
        public void setup() {
            a = new Matrix(n, m, this::getRandom, q);
            b = new Matrix(m, n, this::getRandom, q);
        }

        private BigInteger getRandom(BigInteger q) {
            return new BigInteger(q.bitLength(), secureRandom).mod(q);
        }
    }
}
