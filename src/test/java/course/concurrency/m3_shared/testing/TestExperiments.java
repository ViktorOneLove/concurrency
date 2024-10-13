package course.concurrency.m3_shared.testing;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExperiments {

    // Don't change this class
    public static class Counter {
        private volatile int counter = 0;

        public void increment() {
            counter++;
        }

        public int get() {
            return counter;
        }
    }

    @RepeatedTest(100)
    public void counterShouldFail() {
        ExecutorService ex = Executors.newFixedThreadPool(20);

        int iterations = 5;
        CountDownLatch latch = new CountDownLatch(1);

        Counter counter = new Counter();

        for (int i = 0; i < iterations; i++) {
            ex.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                counter.increment();
            });
//            counter.increment();
        }

        latch.countDown();
        assertEquals(iterations, counter.get());
    }
}
