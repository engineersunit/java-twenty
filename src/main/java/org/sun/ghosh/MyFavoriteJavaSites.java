package org.sun.ghosh;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MyFavoriteJavaSites {
    enum Mode {
        VIRTUAL, PLATFORM, QUICK, // StructuredTaskScope.ShutdownOnSuccess
        ALL_OR_NONE // StructuredTaskScope.ShutdownOnFailure
    }

    public static void main(String[] args) {
        String mode = args[0];
        Mode runMode = Mode.valueOf(Objects.isNull(mode) ? "VIRTUAL" : mode);
        List<Future> futures = new ArrayList<>();

        List<URL> myFavSitesURLList = getURLsFromSource();


        Instant startTime = Instant.now();

        switch (runMode) {
            case VIRTUAL:
                try (var executor =
                             Executors.newVirtualThreadPerTaskExecutor()) {
                    futures =
                            myFavSitesURLList.stream()
                                    .map(url -> executor.submit(() -> fetchURL(url)))
                                    .collect(Collectors.toList());

                }
                break;
            case PLATFORM:
                try (var executor = Executors.newCachedThreadPool()) {
                    futures =
                            myFavSitesURLList.stream()
                                    .map(url -> executor.submit(() -> fetchURL(url)))
                                    .collect(Collectors.toList());
                }
                break;
            case QUICK:
                try (var scope =
                             new StructuredTaskScope.ShutdownOnSuccess<String>()) {
                    futures =
                            myFavSitesURLList.stream()
                                    .map(url -> scope.fork(() -> fetchURL(url)))
                                    .collect(Collectors.toList());

                    var result = scope.join().result();

                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            case ALL_OR_NONE:
                try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                    futures =
                            myFavSitesURLList.stream()
                                    .map(url -> scope.fork(() -> fetchURL(url)))
                                    .collect(Collectors.toList());
                    scope.join();           // Join both forks
                    scope.throwIfFailed(e -> e);  // ... and propagate errors
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                break;
        }


        processFutures(futures);

        Instant endTime = Instant.now();
        Duration between = Duration.between(startTime, endTime);
        System.out.println(String.format("Mode: %s. Created automated " +
                        "reading" + " " + "digest " + "for: %d websites in %d" +
                        "seconds or %d nanoseconds.",
                runMode,
                myFavSitesURLList.size(), between.get(ChronoUnit.SECONDS), between.get(ChronoUnit.NANOS)));

    }

    private static void processFutures(List<Future> futures) {
        // Remove from next iteration if URL is visited and written to file.
        List<Future> futuresClone =
                (List<Future>) ((ArrayList<Future>) futures).clone();
        try (FileWriter fw = new FileWriter("D:\\MyFavSites.html")) {
            while (!futuresClone.isEmpty()) {
                for (Future f : futures) {
                    if (f.isDone()) {
                        switch (f.state()) {
                            case RUNNING, FAILED, CANCELLED -> {
                            }
                            case SUCCESS -> fw.write((String) f.resultNow());
                        }
                        // Remove from next iteration if URL is visited and
                        // written to file.
                        futuresClone.remove(f);
                    } else {
                        continue;
                    }
                }
            }
            // Remove from next iteration if URL is visited and written to file.
            futures = (List<Future>) ((ArrayList<Future>) futuresClone).clone();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String fetchURL(URL url) throws IOException {
        try (var in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static List<String> readFileInList(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        return lines;
    }

    private static List<URL> getURLsFromSource() {
        return readFileInList("D:\\MyFavSites.txt").stream().map(url -> {
            try {
                return new URI(url).toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}