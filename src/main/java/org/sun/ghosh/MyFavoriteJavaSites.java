package org.sun.ghosh;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyFavoriteJavaSites {
    enum Mode {
        VIRTUAL,
        PLATFORM
    }

    public static void main(String[] args) {
        String mode = args[0];
        Mode runMode = Mode.valueOf(mode);
        List<String> myFavSitesList = readFileInList("D:\\MyFavSites.txt");
        List<Future> futures = new ArrayList<>();
        long startTime = System.nanoTime();
        for (String favUrl : myFavSitesList) {
            URL url;
            try {
                url = new URI(favUrl).toURL();
            } catch (URISyntaxException | MalformedURLException e) {
                throw new RuntimeException(e);
            }

            switch (runMode) {
                case VIRTUAL:
                    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                        Future future1 = executor.submit(() -> fetchURL(url));
                        futures.add(future1);

                    }
                    break;
                case PLATFORM:
                    try (var executor = Executors.newCachedThreadPool()) {
                        Future future1 = executor.submit(() -> fetchURL(url));
                        futures.add(future1);
                    }
            }
        }
        // Remove from next iteration if URL is visited and written to file.
        List<Future> futuresClone = (List<Future>) ((ArrayList<Future>) futures).clone();
        try (FileWriter fw = new FileWriter("D:\\MyFavSites.html")) {
            while (!futuresClone.isEmpty())
                for (Future f : futures) {
                    if (f.isDone()) {
                        fw.write((String) f.resultNow());
                        // Remove from next iteration if URL is visited and written to file.
                        futuresClone.remove(f);
                    } else {
                        continue;
                    }
                }
            // Remove from next iteration if URL is visited and written to file.
            futures = (List<Future>) ((ArrayList<Future>) futuresClone).clone();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.nanoTime();
        System.out.println(String.format("Created automated reading digest for: %d websites in %d nanoseconds -> %d seconds %d milliseconds", myFavSitesList.size(), (endTime - startTime), (endTime - startTime) / 1000_000_000, ((endTime - startTime) / 1000_000) % 1000));

    }

    static String fetchURL(URL url) throws IOException {
        try (var in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static List<String> readFileInList(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        return lines;
    }
}