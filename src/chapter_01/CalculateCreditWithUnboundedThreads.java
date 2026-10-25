package chapter_01;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

record Credit(double score) {
}

record Person(Long id, String name) {
}

record Asset(String type, double value) {
}

record Liability(String type, double amount) {
}

public class CalculateCreditWithUnboundedThreads {
    static void main() throws Exception {
        CalculateCreditWithUnboundedThreads service = new CalculateCreditWithUnboundedThreads();

        System.out.println("=== Parallel Execution ===");
        ExecutionTimer.measure(() -> service.calculateCreditWithUnboundedThreads(1L));

        System.out.println("=== Executor Execution ===");
        ExecutionTimer.measure(() -> service.calculateCreditWithExecutor(1L));

        System.out.println("=== Completable Future Execution ===");
        ExecutionTimer.measure(() -> service.calculateCreditWithExecutor(1L));

        System.out.println("=== Virtual Thread Execution ===");
        ExecutionTimer.measure(() -> service.calculateCreditWithVirtualThread(1L));

        System.out.println("=== ForkJoinPool Execution ===");
        ExecutionTimer.measure(() -> service.calculateCreditWithForkJoinPool(1L));


    }

    public Credit calculateCreditWithForkJoinPool(Long personId) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            forkJoinPool.submit(() -> {
                var person = getPerson(personId);
                var assetsFuture = getAssets(person);
                var liabilitiesFuture = getLiability(person);
                importantWork();
                return calculateCredits(assetsFuture, liabilitiesFuture);
            }).join();
        }
        return null;
    }

    public Credit calculateCreditWithVirtualThread(Long personId) throws ExecutionException, InterruptedException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var person = getPerson(personId);
            var assetsFuture = executor.submit(() -> getAssets(person));
            var liabilitiesFuture = executor.submit(() -> getLiability(person));
            executor.submit(this::importantWork);
            return calculateCredits(assetsFuture.get(), liabilitiesFuture.get());
        }
    }

    public Credit calculateCreditWithCompletableFuture(Long personId) throws ExecutionException, InterruptedException {
        return CompletableFuture.runAsync(this::importantWork)
                .thenCompose(aVoid -> CompletableFuture.supplyAsync(() -> getPerson(personId)))
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> getAssets(getPerson(personId))), (person, assets) -> calculateCredits(assets, getLiability(person)))
                .get();
    }

    public Credit calculateCreditWithExecutor(Long personId) throws ExecutionException, InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            var person = getPerson(personId);
            var assetsFuture = executor.submit(() -> getAssets(person));
            var liabilitiesFuture = executor.submit(() -> getLiability(person));
            executor.submit(this::importantWork);

            return calculateCredits(assetsFuture.get(), liabilitiesFuture.get());
        }
    }

    Credit calculateCreditWithUnboundedThreads(Long personId) throws InterruptedException {
        var person = getPerson(personId);
        var assetsRef = new AtomicReference<List<Asset>>();
        var t1 = new Thread(() -> {
            var assets = getAssets(person);
            assetsRef.set(assets);
        });

        var liabilityRef = new AtomicReference<List<Liability>>();
        var t2 = new Thread(() -> {
            var liability = getLiability(person);
            liabilityRef.set(liability);
        });

        var t3 = new Thread(this::importantWork);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();

        var credit = calculateCredits(assetsRef.get(), liabilityRef.get());
        t3.join();
        return credit;
    }

    private Person getPerson(Long personId) {
        simulateDelay(200);
        return new Person(personId, "John");
    }

    private List<Asset> getAssets(Person person) {
        simulateDelay(200);
        return List.of(
                new Asset("House", 300000),
                new Asset("Car", 25000)
        );
    }

    private List<Liability> getLiability(Person person) {
        simulateDelay(200);
        return List.of(
                new Liability("Mortgage", 300000),
                new Liability("chapter_01.Credit Card", 25000)
        );
    }

    private void importantWork() {
        simulateDelay(200);
        System.out.println("Important Work Completed");
    }

    private Credit calculateCredits(List<Asset> assets, List<Liability> liabilities) {
        simulateDelay(200);
        double totalAssets = assets.stream().mapToDouble(Asset::value).sum();
        double totalLiabilities = liabilities.stream().mapToDouble(Liability::amount).sum();
        double creditScore = (totalAssets - totalLiabilities) / 1000;
        return new Credit(creditScore);
    }

    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

class ExecutionTimer {
    public static <T> T measure(Callable<T> task) throws Exception {
        long startTime = System.nanoTime();
        try {
            return task.call();
        } finally {
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            System.out.println("Execution time: " + duration + " milliseconds");
        }
    }
}
