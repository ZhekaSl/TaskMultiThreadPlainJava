package ua.zhenya;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WriterThread extends Thread {
    private final String outputPath;
    private final BlockingQueue<ResultTask> resultQueue;
    private final Map<Integer, ResultTask> buffer = new ConcurrentHashMap<>();
    private final AtomicInteger nextToWrite = new AtomicInteger(1);

    public WriterThread(String outputPath, BlockingQueue<ResultTask> resultQueue) {
        this.outputPath = outputPath;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            while (true) {
                ResultTask task = resultQueue.take();
                if (task == ResultTask.POISON_PILL) {
                    while (buffer.containsKey(nextToWrite.get()))
                        writeTask(writer, buffer.remove(nextToWrite.getAndIncrement()));
                    break;
                }
                buffer.put(task.index, task);

                while (buffer.containsKey(nextToWrite.get()))
                    writeTask(writer, buffer.remove(nextToWrite.getAndIncrement()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeTask(BufferedWriter writer, ResultTask task) throws IOException {
        writer.write(task.number + " = " + task.factorial.toString());
        writer.newLine();
    }
}