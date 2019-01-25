package com.xishi.moon;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
@Fork(2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class HashCacheJmhTest {

    @State(Scope.Benchmark)
    public static class RandomState{
        Random random = new Random();
    }

    @State(Scope.Benchmark)
    public static class HashCacheState {
        HashCache hc = new HashCache(1024);
    }
    @State(Scope.Benchmark)
    public static class ConcurrentHashMapState {
        ConcurrentHashMap hm = new ConcurrentHashMap(1024);
    }

    @Benchmark
    public void hashCacheTest(HashCacheState state,RandomState random){
        String k = String.valueOf(random.random.nextInt(10000));
        state.hc.put(k,k);
    }

    @Benchmark
    public void hashMapTest(ConcurrentHashMapState state,RandomState random){
        String k = String.valueOf(random.random.nextInt(10000));
        state.hm.put(k,k);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(HashCacheJmhTest.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
