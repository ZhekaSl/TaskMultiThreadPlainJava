package ua.zhenya;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiter {
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;

    public RateLimiter(int maxPermits, long period, TimeUnit unit) {
        semaphore = new Semaphore(maxPermits);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            int toRelease = maxPermits - semaphore.availablePermits();
            if (toRelease > 0) {
                semaphore.release(toRelease);
            }
        }, period, period, unit);
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}