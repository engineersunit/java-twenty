package org.sun.ghosh.designpatterns;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class PoisonPillPattern {
    private static volatile LinkedBlockingQueue<String> smsQ = new LinkedBlockingQueue<>();
    private static final int maxMsg = 100;
    private static final String POISON_PILL_STR = PoisonPillPattern.class.getName();
    private final static List<String> mobileSubscribers = Arrays.asList("9876500001",
            "9876500002", "9876500003",
            "9876500004", "9876500005");

    public static void main(String[] args) throws InterruptedException {
        Thread deliver = Thread.ofVirtual().start(PoisonPillPattern::deliverMessage);
        Thread bank = Thread.ofVirtual().start(() -> new BankMessageSender().send());
        Thread school = Thread.ofVirtual().start(() -> new SchoolMessageSender().send());
        Thread ecom = Thread.ofVirtual().start(() -> new ECommerceMessageSender().send());

        bank.join();
        school.join();
        ecom.join();

        smsQ.add(POISON_PILL_STR);

        deliver.join();

        System.out.println(smsQ.size());
        System.out.println(smsQ);


    }

    static void deliverMessage() {
        Map<String, List<String>> messageDelivered = new ConcurrentHashMap<>(3 * maxMsg);
        while (true) {
            String subscriber = mobileSubscribers.get((int) (Math.random() * (5)));
            List<String> messages = messageDelivered.get(subscriber);
            try {
                String message = smsQ.take();

                if (message.equals(POISON_PILL_STR)) {
                    break;
                }

                if (Objects.isNull(messageDelivered.get(subscriber))) {
                    messages = new ArrayList<>(100);
                    messages.add(message);
                    messageDelivered.put(subscriber, messages);
                    continue;
                }

                messages.add(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            messageDelivered.put(subscriber, messages);
        }
        System.out.println(messageDelivered);
    }

    interface MessageSender {
        void send();
    }

    static void shutdownExecutorService(ExecutorService executor) {
        // Disable new tasks from being submitted
        executor.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
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

    static class BankMessageSender implements MessageSender {
        int max = 1_00_000;
        int min = 1_00;

        @Override
        public void send() {
            try (var executor =
                         Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.rangeClosed(1, maxMsg).
                        forEach((i) ->
                                executor.submit(() ->
                                        smsQ.add(String.format("Your account was credited with Rupees %d.",
                                                (int) (Math.random() * (max - min + 1) + min)))));
                shutdownExecutorService(executor);
            }

        }
    }


    static class SchoolMessageSender implements MessageSender {
        int max = 31;
        int min = 1;

        @Override
        public void send() {
            try (var executor =
                         Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.rangeClosed(1, maxMsg).
                        forEach((i) ->
                                executor.submit(() ->
                                        smsQ.add(String.format("It is a holiday on %d July.",
                                                (int) (Math.random() * (max - min + 1) + min)))));
                shutdownExecutorService(executor);
            }
        }
    }

    static class ECommerceMessageSender implements MessageSender {
        int max = 14;
        int min = 1;

        @Override
        public void send() {
            try (var executor =
                         Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.rangeClosed(1, maxMsg).
                        forEach((i) ->
                                executor.submit(() -> smsQ.add(String.format("Your shipment will reach in %d days.",
                                        (int) (Math.random() * (max - min + 1) + min)))));
                shutdownExecutorService(executor);
            }
        }
    }

}