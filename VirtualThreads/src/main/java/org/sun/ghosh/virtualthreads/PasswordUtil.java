package org.sun.ghosh.virtualthreads;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordUtil {

    private final static List<Character> listOfPasswordCandidates = Collections.
            synchronizedList(new ArrayList<>());
    final static int THREAD_NUM = 1000; // Generates N * 3.5 chars

    private static final CyclicBarrier cyclicBarrier =
            new CyclicBarrier(
            2,
            PasswordUtil::injectSpecialCharacter);

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        /*try (var executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {*/
        /*try (var executor =
                         Executors.newCachedThreadPool()) {*/
            try (var executor =
                         Executors.newFixedThreadPool(10)) {
                /*try (var executor =
                             Executors.newSingleThreadExecutor()) {*/
            // Spawn TYPE_MAX_CHAR Virtual Threads for
            // a mix of A-Z, a-z, 0-9 and special characters
            IntStream.rangeClosed(1, THREAD_NUM).
                    forEach((i) ->
                            executor.submit(PasswordUtil::generateData));

            // Disable new tasks from being submitted
            executor.shutdown();
            try {
                // Wait a while for existing tasks to terminate
                if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executor.awaitTermination(2, TimeUnit.SECONDS))
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

        String password = listOfPasswordCandidates
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining());

        System.out.println("Use this password:\n" + password);

        Instant endTime = Instant.now();
        Duration between = Duration.between(startTime, endTime);
        System.out.printf("Created password of length %d in %d seconds and %f milliseconds.",
                password.length(), between.get(ChronoUnit.SECONDS),
                between.get(ChronoUnit.NANOS) / 1000_000.0);
    }

    /**
     * 48-57  - 0-9
     * 65-90  - A-Z
     * 97-122 - a-z
     *
     * <a href="https://owasp.org/www-community/password-special-characters">
     *     Open Worldwide Application Security Project - Password Special Characters</a>
     * <p>
     * 33	!	Exclamation
     * 35	#	Number sign (hash)
     * 36	$	Dollar sign
     * 38	&	Ampersand
     * 42	*	Asterisk
     * 43	+	Plus
     * 64	@	At sign
     * 126	~	Tilde
     */
    private static void generateData() {

        // 97-122 - a-z
        listOfPasswordCandidates.
                add(generateRandomChar(122, 97));

        // 48-57  - 0-9
        listOfPasswordCandidates.
                add(generateRandomChar(57, 48));

        // 65-90  - A-Z
        listOfPasswordCandidates.
                add(generateRandomChar(90, 65));

        try {
            // Mimicking a I/O operation of 100ms
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException |
                 BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectSpecialCharacter() {
        listOfPasswordCandidates.
                add(generateRandomSpecialChar());
    }

    private static char generateRandomChar(int max, int min) {
        return (char) (Math.random() * (max - min + 1) + min);
    }

    private static char generateRandomSpecialChar() {
        char[] specialChars = new char[]
                {'!', '#', '$', '&', '*', '+', '@', '~'};
        int max = specialChars.length;
        return specialChars[(int) (Math.random() * max)];
    }
}