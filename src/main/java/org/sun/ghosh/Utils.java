package org.sun.ghosh;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.sun.ghosh.MyFavoriteJavaSites.SECURE;
import static org.sun.ghosh.MyFavoriteJavaSites.SITE_URL;

public class Utils {
    public static boolean isSiteContentSecure() {
        System.out.println(String.format("For thread %s the site (%s) content" +
                        " was " +
                        "fetched by %s protocol", Thread.currentThread(),
                SITE_URL.isBound() ? SITE_URL.get()
                        : "Unknown", SECURE.get()));
        return SECURE.get().equals(MyFavoriteJavaSites.HttpSecurity.https);
    }

    /**
     * Overloaded method which utilizes ScopedValue SITE_URL
     *
     * @return Site content
     */
    public static String fetchURL() {
        return fetchURL(SITE_URL.isBound() ? SITE_URL.get() : null);
    }

    public static String fetchURL(URL url) {
        // Set the ThreadLocal variable SECURE - security level when the
        // virtual thread is submitted and run
        SECURE.set(MyFavoriteJavaSites.HttpSecurity.valueOf(url.getProtocol()));
        // Utility methods in another class uses the ThreadLocal variable SECURE
        boolean isSiteContentSecure = Utils.isSiteContentSecure();
        if (!isSiteContentSecure) {
            return "";
        }
        try (var in = url.openStream()) {
            String siteContent = new String(in.readAllBytes(),
                    StandardCharsets.UTF_8);
            SECURE.remove();
            return siteContent;
        } catch (IOException e) {
            e.printStackTrace();
            String returnStr = String.format("The site %s was not fetched " +
                            "due to " +
                            "exception %s, details: %s",
                    SITE_URL.isBound() ? SITE_URL.get() : null,
                    e.getClass(), e.getMessage());
            return returnStr;
        }
    }


    public static List<String> readFileInList(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        return lines;
    }

    public static List<URL> getURLsFromSource() {
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
