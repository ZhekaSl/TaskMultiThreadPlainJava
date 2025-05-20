package ua.zhenya;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

public class ReaderThread extends Thread {
    private final String inputPath;
    private final BlockingQueue<InputTask> inputQueue;

    public ReaderThread(String inputPath, BlockingQueue<InputTask> inputQueue) {
        this.inputPath = inputPath;
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath))) {
            String line;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    int num = Integer.parseInt(line);
                    inputQueue.put(new InputTask(index++, num));
                } catch (NumberFormatException ignored) {

                }
            }
            inputQueue.put(InputTask.POISON_PILL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}