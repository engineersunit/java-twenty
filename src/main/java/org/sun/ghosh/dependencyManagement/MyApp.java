package org.sun.ghosh.dependencyManagement;

public class MyApp {
    public static void main(String[] args) {
        System.out.println("The code is running " +
                "in the main application");
        System.out.println("The code is calling " +
                "Another application");
        AnotherApp.doTask();
    }
}
