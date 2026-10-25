void main() {
    AtomicInteger count = new AtomicInteger(0);
    try {
        while (true) {
            Thread thread = new Thread(() -> {
                count.incrementAndGet();
                LockSupport.park();
            });
            thread.start();
            System.out.println(count.get());
        }
    } catch (OutOfMemoryError err) {
        System.err.println(err.getMessage() + " " + count);
        err.printStackTrace();
    }
}
