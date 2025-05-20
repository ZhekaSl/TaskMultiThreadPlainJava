package ua.zhenya;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FactorialThread extends Thread {
    private final ExecutorService executor;
    private final BlockingQueue<InputTask> inputQueue;
    private final BlockingQueue<ResultTask> resultQueue;
    private final RateLimiter rateLimiter;

    public FactorialThread(int poolSize,
                           BlockingQueue<InputTask> inputQueue,
                           BlockingQueue<ResultTask> resultQueue,
                           RateLimiter rateLimiter) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.inputQueue = inputQueue;
        this.resultQueue = resultQueue;
        this.rateLimiter = rateLimiter;
        setName("FactorialThread");
    }

    @Override
    public void run() {
        try {
            while (true) {
                InputTask task = inputQueue.take();
                if (task == InputTask.POISON_PILL) {
                    inputQueue.put(task);
                    break;
                }
                executor.submit(() -> {
                    try {
                        rateLimiter.acquire();
                        BigInteger fact = factorial(task.number);
                        resultQueue.put(new ResultTask(task.index, task.number, fact));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.out.println("Ожидаем завершения вычислений...");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}