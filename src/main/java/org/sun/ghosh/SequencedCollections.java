package org.sun.ghosh;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SequencedCollections {
    public static void main(String[] args) {

        List<String> javaVersionList = Arrays.asList(
                "Java 8", "Java 11", "Java 17", "Java 20", "Java 21");

        // Older ways of getting the first and
        // the last elements from a list
        String firstJavaVersion = javaVersionList.get(0);
        String lastJavaVersion =
                javaVersionList.get(javaVersionList.size() - 1);

        // Printing the first and last elements shows that
        // the Encounter Order is preserved
        System.out.println(String.format("Java List: The first LTS " +
                        "Java version is: %s, and the upcoming " +
                        "LTS Java version is: %s",
                firstJavaVersion, lastJavaVersion));


        Set<String> javaVersionSet = new HashSet<>(javaVersionList);

        // Try to get the first element by using Stream -> findFirst()
        firstJavaVersion = javaVersionSet.stream().findFirst().get();

        Iterator<String> it = javaVersionSet.iterator();
        while (it.hasNext()) {
            // Try to get the last element
            // by using Set.Iterator and
            // looping till the last element
            lastJavaVersion = it.next();
        }

        // Printing the first and last elements shows that
        // the Encounter Order is not preserved
        System.out.println(String.format("Java HashSet: The first LTS " +
                        "Java version is: %s, and the upcoming " +
                        "LTS Java version is: %s",
                firstJavaVersion, lastJavaVersion));

    }
}
