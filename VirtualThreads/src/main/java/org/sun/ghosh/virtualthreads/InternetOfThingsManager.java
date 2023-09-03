package org.sun.ghosh.virtualthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class InternetOfThingsManager {

    private static volatile LinkedBlockingQueue<Map<String, Integer>> tempSensorValuesQ = new LinkedBlockingQueue<>();
    private static volatile LinkedBlockingQueue<Map<String, Integer>> notifySensorValuesQ = new LinkedBlockingQueue<>();

    private static final int MAX_NUM_OF_VALUES = 1_000_000;
    private static final int NUM_OF_BLOCKING_SECS = 100;
    private static final Integer POISON_PILL_INT = Integer.MIN_VALUE;
    private static final String DEVICE_NAME = "IoTDevice";

    public static void main(String[] args) throws InterruptedException {
        Instant startTime = Instant.now();

        // CPU intensive operation - Platform Thread
        Thread emitSensorValues = Thread.ofPlatform() // ofVirtual()
                .start(() ->
                        emitSensorValues(100, 0));

        // I/O intensive operation
        Thread validateQSensorValues = Thread.ofVirtual() // ofVirtual()
                .start(() ->
                        validateQSensorValues());

        emitSensorValues.join();
        validateQSensorValues.join();

        Instant endTime = Instant.now();
        Duration between = Duration.between(startTime, endTime);


//        print(tempSensorValuesQ);
        print("Total IoT Device sensor values generated: " + MAX_NUM_OF_VALUES);

//        print(notifySensorValuesQ);
        print("Total IoT Device sensor values progressed for rule violation notification: " + notifySensorValuesQ.size());

        print(String.format("Time taken: %s", between.toString()));

    }

    private static void emitSensorValues(int max, int min) {
        IntStream.rangeClosed(1, MAX_NUM_OF_VALUES).
                forEach((i) ->
                        tempSensorValuesQ.add(
                                Map.of(
                                        DEVICE_NAME + i,
                                        (int) (Math.random() * (max - min + 1) + min))));
        // mark termination of emission
        tempSensorValuesQ.add(Map.of(DEVICE_NAME, POISON_PILL_INT));
    }

    // blocking call
    private static void validateQSensorValues() {
        int num_virtual_threads_spawned = 0;
        try (var executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {
        /*try (var executor =
                         Executors.newCachedThreadPool()) {*/
            while (true) {

                Map<String, Integer> sensorValue = null;
                try {
                    sensorValue = tempSensorValuesQ.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String iotDevice = (String) sensorValue.keySet().toArray()[0];
                Integer temperature = sensorValue.get(iotDevice);

                if (POISON_PILL_INT.equals(temperature)) {
                    break;
                }

                executor.submit(() -> {
                    // blocking call
                    Integer[] minMax = fetchSensorRulesFromDataSource(iotDevice);

                    // Business logic
                    int max = minMax[0], min = minMax[1];
                    if (temperature > max || temperature < min) {
                        notifySensorValuesQ.add(Map.of(iotDevice, temperature));
                    }

                });
                num_virtual_threads_spawned++;
            }
            shutdownExecutorService(executor, 10);
        }
        print("Number of virtual threads spawned: " + num_virtual_threads_spawned);
    }

    // blocking call. Mimics a database call
    private static Integer[] fetchSensorRulesFromDataSource(String iotDevice) {
        try {
            Thread.sleep(NUM_OF_BLOCKING_SECS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Integer[]{85, 15};
    }

    private static void print(Object o) {
        System.out.println(o);
    }

    private static void shutdownExecutorService(ExecutorService executor, int seconds) {
        // Disable new tasks from being submitted
        executor.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(seconds, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(1, TimeUnit.SECONDS))
                    System.err.println("Created Virtual Thread Per Task Executor did not terminate");
            } else {
                System.out.println("Created Virtual Thread Per Task Executor terminated");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
