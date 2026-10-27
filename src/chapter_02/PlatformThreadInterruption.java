package chapter_02;

public class PlatformThreadInterruption {
    static void main() {
        Thread platformThread = Thread.ofPlatform().start(() -> {
            try {
                System.out.println("Platform thread started...");
                for (int i = 0; i < 5; i++) {
                    System.out.println("Platform thread working: " + i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Platform thread interrupted");
            }
        });

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {

        }
        platformThread.interrupt();
    }
}


class VirtualThreadInterruption {
    static void main() {
        Thread virtualThread = Thread.ofVirtual().start(() -> {
            try {
                System.out.println("Virtual thread started...");
                for (int i = 0; i < 5; i++) {
                    System.out.println("Virtual thread working: " + i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Virtual thread interrupted");
            }
        });

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {

        }
        virtualThread.interrupt();
    }
}
