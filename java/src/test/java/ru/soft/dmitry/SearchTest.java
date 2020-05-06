package ru.soft.dmitry;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by main on 28.06.17.
 */
public class SearchTest {

    public static final String DATA_TXT = "data.txt";
    public static final int SIZE = 40_000_000;

    public static class EndlessQueue<E> extends ArrayDeque<E> {
        @Override
        public E poll() {
            final E e = super.poll();
            offerLast(e);
            return e;
        }
    }

    @State(Scope.Thread)
    public static class Data {

        EndlessQueue<Integer> queue = new EndlessQueue<>();
        int[] arr = new int[SIZE];
        int key;

        @Setup(Level.Trial)
        @SuppressWarnings("unchecked")
        public void setUpValues() throws IOException, ClassNotFoundException {
            for (int i = 0; i< SIZE; i++) {
                arr[i] = i;
            }
            try (final FileInputStream fis = new FileInputStream(DATA_TXT);
                 final ObjectInputStream ois = new ObjectInputStream(fis)) {
                queue = (EndlessQueue<Integer>) ois.readObject();
            }
        }

        @Setup(Level.Invocation)
        public void getKey() {
            key = queue.poll();
        }

    }

    @State(Scope.Thread)
    public static class Rnd {

        private Random rnd;
        public int l;
        public int r;

        @Setup(Level.Trial)
        public void doSetup() {
            rnd = new Random(System.currentTimeMillis());
        }

        @Setup(Level.Invocation)
        public void setUpValues() {
            l = rnd.nextInt();
            r = rnd.nextInt();
        }
    }

    @Test
    public void generateTestData() throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(DATA_TXT);
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            final EndlessQueue<Integer> queue = new EndlessQueue<>();
            Random rnd = new Random();
            for (int i = 0; i< 1_000_000; i++) {
                queue.offer(rnd.nextInt(SIZE));
            }
            oos.writeObject(queue);
        }
    }

    @Test
    public void binarySanityCheck() throws Exception {
        sanityCheck(Search::binary);
    }

    @Test
    public void binary2SanityCheck() throws Exception {
        sanityCheck(Search::binary2);
    }

    @Test
    public void binaryStdSanityCheck() throws Exception {
        sanityCheck((key, arr) -> Arrays.binarySearch(arr, key));
    }

    private void sanityCheck(final Function function) {
        final int k = 3;
        assertEquals(0, function.binary(k, new int[]{ k }));
        assertEquals(0, function.binary(k, new int[]{ k, 4 }));
        assertEquals(0, function.binary(k, new int[]{ k, 4, 5 }));
        assertEquals(1, function.binary(k, new int[]{ 1, k }));
        assertEquals(1, function.binary(k, new int[]{ 1, k, 4 }));
        assertEquals(1, function.binary(k, new int[]{ 1, k, 4, 5 }));
        assertEquals(2, function.binary(k, new int[]{ 1, 2, k }));
        assertEquals(2, function.binary(k, new int[]{ 1, 2, k, 4 }));
        assertEquals(2, function.binary(k, new int[]{ 1, 2, k, 4, 5 }));
        assertTrue(function.binary(k, new int[]{ 1 }) < 0);
        assertTrue(function.binary(k, new int[]{ 1, 2 }) < 0);
        assertTrue(function.binary(k, new int[]{ 1, 2, 4 }) < 0);
        assertTrue(function.binary(k, new int[]{ 1, 2, 4, 5 }) < 0);
    }

    private interface Function {
        int binary(int key, int[] arr);
    }

    @Test
    public void anyBetweenTest() throws Exception {
        final int r = Integer.MAX_VALUE;
        final int l = r - 10;
        assertThat(Search.anyBetween(l, r), greaterThan(l));
        assertThat(Search.anyBetween2(l, r), greaterThan(l));
        assertThat(Search.anyBetween3(l, r), greaterThan(l));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureBinarySearch(final Blackhole blackhole, final Data data) {
        blackhole.consume(Search.binary(data.key, data.arr));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureBinary2Search(final Data data) {
        Search.binary2(data.key, data.arr);
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureBinarySearchStdLib(final Data data) {
        Arrays.binarySearch(data.arr, data.key);
    }

    @Benchmark @BenchmarkMode(Mode.SampleTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureAnyBetween1(final Rnd rnd) {
        return Search.anyBetween(rnd.l, rnd.r);
    }
    @Benchmark @BenchmarkMode(Mode.SingleShotTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureAnyBetween2(final Rnd rnd) {
        return Search.anyBetween2(rnd.l, rnd.r);
    }
    @Benchmark @BenchmarkMode(Mode.SingleShotTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureAnyBetween3(final Rnd rnd) {
        return Search.anyBetween3(rnd.l, rnd.r);
    }

}
