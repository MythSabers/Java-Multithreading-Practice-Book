package chapter_02;

import java.util.HashSet;
import java.util.Set;

public class VirtualThreadGroupExample {
    static void main() throws InterruptedException {
        Set<ThreadGroup> threadGroup = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Thread vThread = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threadGroup.add(vThread.getThreadGroup());
        }

        Thread.sleep(1000);
        System.out.println("Unique thread group: " + threadGroup.size());
        System.out.println("Thread group: " + threadGroup.iterator().next());
    }
}
