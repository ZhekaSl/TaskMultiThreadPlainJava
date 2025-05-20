package ua.zhenya;

import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TaskMultiThreadApplication {
    public static void main(String[] args) throws Exception {
        System.out.println("Введите размер пула вычислительных потоков:");
        Scanner scanner = new Scanner(System.in);
        int poolSize = scanner.nextInt();

        String inputPath = Paths.get("src", "main", "resources", "input.txt").toString();
        String outputPath = Paths.get("src", "main", "resources", "output.txt").toString();


        BlockingQueue<InputTask> inputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ResultTask> resultQueue = new LinkedBlockingQueue<>();

        RateLimiter rateLimiter = new RateLimiter(100, 1, TimeUnit.SECONDS);

        ReaderThread reader = new ReaderThread(inputPath, inputQueue);
        FactorialThread calculator = new FactorialThread(poolSize, inputQueue, resultQueue, rateLimiter);
        WriterThread writer = new WriterThread(outputPath, resultQueue);

        long start = System.nanoTime();

        reader.start();
        calculator.start();
        writer.start();

        reader.join();
        calculator.join();
        writer.join();

        long end = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(end - start);

        System.out.println("Готово. Результаты записаны в " + outputPath);
        System.out.printf("Время выполнения: %d мин %d сек %d мс%n",
                durationMs / 60000, (durationMs % 60000) / 1000, durationMs % 1000);
    }
}