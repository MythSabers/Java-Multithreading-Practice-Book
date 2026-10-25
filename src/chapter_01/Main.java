

void main() {
    Thread t1 = new ThreadByExtension("Worker-1");
    t1.start();

    Thread t2 = new Thread(new RunnableImplementation(), "Worker-2");
    t2.start();

    Thread t3 = new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("Anonymous: " + Thread.currentThread().getName());
        }
    }, "Worker-3");
    t3.start();

    Thread t4 = new Thread(() -> System.out.println("Lambda: " + Thread.currentThread().getName()), "Worker-4");
    t4.start();
}


static class ThreadByExtension extends Thread {
    public ThreadByExtension(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("Extension Thread: " + getName());
    }
}


class RunnableImplementation implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable: " + Thread.currentThread().getName());
    }
}
