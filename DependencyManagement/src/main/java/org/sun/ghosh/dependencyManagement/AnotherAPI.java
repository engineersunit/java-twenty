package org.sun.ghosh.dependencyManagement;

public class AnotherAPI {
    public static void doTask() {
        System.out.println("Entering Another API");
        System.out.println("The code is running " +
                "the **direct dependency** of " +
                "the main application - MyApp");
        System.out.println("The code is calling " +
                "Some Other API");
        SomeOtherAPI.doTask();
    }
}
