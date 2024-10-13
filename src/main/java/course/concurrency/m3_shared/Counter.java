package course.concurrency.m3_shared;

import lombok.SneakyThrows;

public class Counter {
    public static final Object lock = new Object();
    public static volatile int current = 0;

    public static void run(int value) {
        value--;
        while (true) {
            synchronized (lock) {
                while (current != value) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(value + 1);
                current = (current + 1) % 3;
                lock.notifyAll();
            }
        }
    }

    public static void first() {
        run(1);
    }

    public static void second() {
        run(2);
    }

    public static void third() {
        run(3);
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());
        t1.start();
        t2.start();
        t3.start();
    }
}
