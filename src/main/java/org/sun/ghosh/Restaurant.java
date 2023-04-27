package org.sun.ghosh;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class Restaurant {
    private static final String START = "_START";
    private static final String END = "_END";

    enum Menu {
        BREAKFAST,
        LUNCH,
        SNACKS,
        DINNER,
        NO_SERVICE
    }

    private static Menu getMenuType() {
        int hourOfDay = LocalDateTime.now().getHour();
        hourOfDay = 16;
        return switch (hourOfDay) {
            case 6, 7, 8, 9, 10, 11 -> Menu.BREAKFAST;
            case 12, 13, 14, 15 -> Menu.LUNCH;
            case 16, 17, 18, 19 -> Menu.SNACKS;
            case 20, 21, 22, 23 -> Menu.DINNER;
            default -> Menu.NO_SERVICE;
        };
    }

    public static List<String> getMenuItems() {
        List<String> allMenuItems = Utils
                .readFileInList(
                        "./src/main/java/org/sun/ghosh/MyFavFoodItems.txt");
        System.out.println("All Menu Items: " + allMenuItems);
        // Get the current menu on the basis of time of the day
        Menu currentMenu = getMenuType();
        System.out.println("The current running Menu is: " +
                currentMenu.toString().toLowerCase());

        // prepare the start and end markers
        String startMarker = currentMenu + START;
        String endMarker = currentMenu + END;

        List<String> menuItems = allMenuItems.stream()
                .takeWhile(menuType ->
                        !menuType.
                                equals(endMarker))
                .dropWhile(menuType ->
                        !menuType.
                                equals(startMarker))
                .collect(Collectors.toList());
        // remove the start marker
        menuItems.remove(0);
        return menuItems;
    }

    public static void main(String[] args) {

        System.out.println("Hello, Today we can offer you" +
                " the following: \n" +
                getMenuItems());
    }


}
