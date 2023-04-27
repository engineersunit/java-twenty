package org.sun.ghosh;

import java.util.List;

record Name(String firstName,
            String lastName) {}

record CartesianCoordinate(double x,
                           double y,
                           double z) {}

record Geolocation(double latitude,
                   double longitude,
                   double altitude) {}

record MapCoordinate(CartesianCoordinate cc,
                     Geolocation gl) {}

record MapLocation(MapCoordinate upperLeft,
                   MapCoordinate lowerRight) {}

class A {}

class B extends A {}

sealed interface I permits C, D {}

final class C implements I {}

final class D implements I {}

record Pair<T>(T x, T y) {}


public class RecordPattern {
    public static void main(String[] args) {
        Object userValue = "User String";

        // Old code
        if (userValue instanceof String) {
            String s = (String) userValue;
            System.out.println(
                    String.format(
                    "You passed %s to the program",
                    s));
        }

        // New code: JDK 16, JEP 394
        // It does 2 things:
        // 1. Uses type pattern - "String s"
        // 2. Perform pattern matching on "String s"
        if (userValue instanceof String s) {
            System.out.println(
                    String.format(
                    "You passed %s to the program",
                    s));
        }

        // Create a record Name
        Name name = new Name("Java", "User");

        // Record classes (JEP 395)
        if (name instanceof Name pattern) {
            //  extract the data, known as the components
            String firstName = pattern.firstName();
            String lastName = pattern.lastName();
            System.out.println(String.format("You passed values %s and %s " +
                    "to the program. Name is %s.", String.format("%s %s",
                    firstName, lastName)));
        }

        // Record Pattern
        if (name instanceof Name(
                String firstName,
                String lastName)) {
            System.out.println(
                    String.format(
                    "You passed values %s and %s " +
                    "to the program. Name is %s.",
                    String.format("%s %s",
                    firstName, lastName)));
        }

        List<CartesianCoordinate> CartesianCoordinateList =
                List.of(new CartesianCoordinate(1, 1, 1),
                        new CartesianCoordinate(2, 2, 2));
        // Record Pattern in for header!
        for (CartesianCoordinate(
                var x,
                var y,
                var z
        ) : CartesianCoordinateList) {
            System.out.println(
                    "(" + x + ", " +
                            "" + y + ", " +
                            "" + z + ")");
        }

        Pair<A> p1 = null;
        Pair<I> p2 = null;

        /*switch (p1) {                 // Error!
            case Pair<A>(A a, B b) -> System.out.println(a.toString() + b);
            case Pair<A>(B b, A a) -> System.out.println(a.toString() + b);
        }*/

        switch (p2) {
            case Pair<I>(I i, C c) -> System.out.println(i.toString() + c);
            case Pair<I>(I i, D d) -> System.out.println(i.toString() + d);
        }

    }

    /**
     * If we want to extract the
     * cartesian coordinates
     * from the upper-left
     * MapCoordinate, we could write
     *
     * @param r
     */
    static void
    printUpperLeftMapCartesianCoordinate
    (MapLocation r)
    {
        if (r instanceof MapLocation(
                MapCoordinate ul,
                MapCoordinate lr
        )) {
            System.out.println(ul.cc());
        }
    }

    /**
     * We can nest another pattern
     * inside the record pattern, and
     * decompose both the
     * outer and inner records at once
     *
     * @param r
     */
    static void printHeightOfUpperLeftMapCartesianCoordinate
    (MapLocation r)
    {
        if (r instanceof MapLocation(
                MapCoordinate(
                        CartesianCoordinate cc,
                        Geolocation gl),
                MapCoordinate lr
        )) {
            System.out.println(cc.z());
        }
    }

    /**
     * With nested patterns we can
     * deconstruct such a MapLocation with
     * code that echoes the structure
     * of the nested constructors
     * @param r
     */
    static void printXCoordOfUpperLeftCartesianCoordinateWithPatterns
    (MapLocation r) {
        if (r instanceof MapLocation(
                MapCoordinate(
                        CartesianCoordinate(
                                var x,
                                var y,
                                var z), var c
                ),
                var lr
        )) {
            System.out.println(
                    "Upper-left corner: " + x);
        }
    }

    /**
     * The record pattern in the enhanced for statement can have nested patterns
     *
     * @param r
     */
    static void printUpperLeftGeolocation(MapLocation[] r) {
        for (MapLocation(
                MapCoordinate(
                        CartesianCoordinate p, Geolocation c),
                MapCoordinate lr
        ) : r) {
            System.out.println(c);
        }
    }
}
