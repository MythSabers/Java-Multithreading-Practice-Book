void main() {
    try (ExecutorService executor = Executors.newFixedThreadPool(200)) {
        for (int i = 0; i < 10_000_000; i++) {
            final int taskId = i;
            executor.submit(() -> IO.println("Executing task: " + taskId + " in thread " + Thread.currentThread().getName()));
        }
    }
}