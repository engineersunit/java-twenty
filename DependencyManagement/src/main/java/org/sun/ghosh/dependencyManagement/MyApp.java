package org.sun.ghosh.dependencyManagement;

public class MyApp {
    public static void main(String[] args) {
        System.out.println("The code is running " +
                "in the scope of the main application - MyApp");
        System.out.println("The code is calling " +
                "Another API");
        AnotherAPI.doTask();
    }
}
