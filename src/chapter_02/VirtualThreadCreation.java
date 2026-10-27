package chapter_02;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class VirtualThreadCreation {
    static void main() {
        long startTime = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000_000).forEach(i -> executor.submit(() -> {
                sleep(Duration.ofSeconds(1));
                return i;
            }));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time Taken: " + (endTime - startTime) + " ms ");
    }
}
