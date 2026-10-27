package chapter_02;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class LittleLawExample {
    static void main() {
        int numTasks = 10_000;
        int avgResponseTimeMillis = 500;
        Runnable ioBoundTask = () -> {
            try {
                Thread.sleep(Duration.ofMillis(avgResponseTimeMillis));
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
        };

        System.out.println("=== Little's Law Throughput Comparison ===");
        System.out.println("Testing " + numTasks + " tasks with " + avgResponseTimeMillis + " ms ");
        benchMark("Virtual Threads", Executors.newVirtualThreadPerTaskExecutor(), ioBoundTask, numTasks);
        benchMark("Fixed Threadpool (100)", Executors.newFixedThreadPool(100), ioBoundTask, numTasks);
        benchMark("Fixed Threadpool (500)", Executors.newFixedThreadPool(500), ioBoundTask, numTasks);
        benchMark("Fixed Threadpool (1000)", Executors.newFixedThreadPool(1000), ioBoundTask, numTasks);
    }

    static void benchMark(String type, ExecutorService executor, Runnable task, int numTask) {
        Instant start = Instant.now();
        AtomicLong completeTasks = new AtomicLong();
        try (executor) {
            IntStream.range(0, numTask)
                    .forEach(i -> executor.submit(() -> {
                        task.run();
                        completeTasks.incrementAndGet();
                    }));
        }
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        double throughPut = (double) completeTasks.get() / duration * 1000;
        System.out.printf("%-25s - Time: %5dms, Throughput: %8.2f tasks/s%n", type, duration, throughPut);
    }
}
