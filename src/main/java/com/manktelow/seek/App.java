package com.manktelow.seek;

import com.manktelow.seek.model.Window;
import com.manktelow.seek.service.CounterService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Automatic traffic counter

 * Assumptions:
 * - no duplicate windows
 */
public class App 
{
    public static void main( String[] args ) {
        String filename;
         try {
             filename = args[0];
         } catch (ArrayIndexOutOfBoundsException error) {
             System.out.println("Pleased provide a file name");
             return;
         }

        File file = new File(filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            CounterService counterService = CounterService.withWindows(
                    br.lines().map(Window::fromString).toList()
            );

            printTotalCarCount(counterService);
            printCarCountByDay(counterService);
            printTopThreeWindowsByCarCount(counterService);
            print90minWindowWithLeastCars(counterService);
        } catch (FileNotFoundException error) {
            System.out.println("File " + filename + " not found!");
        } catch (IOException error) {
            System.out.println("An error occurred " + error.getMessage());
        }
    }

    public static void printCarCountByDay(CounterService counterService) {
        System.out.println("\nCar count by day:");
        counterService.getCarCountByDay().forEach((date, count) -> System.out.println(date + " " + count));
    }

    public static void printTopThreeWindowsByCarCount(CounterService counterService) {
        System.out.println("\nTop 3 half hour windows:");
        counterService.getTopThreeWindowsByCarCount().forEach(window -> System.out.println(window.toPrintString()));
    }

    public static void printTotalCarCount(CounterService counterService) {
        System.out.println("\nTotal car count:");
        System.out.println(counterService.getTotalCarCount().orElse(null));
    }

    public static void print90minWindowWithLeastCars(CounterService counterService) {
        System.out.println("\n90 minutes with least cars:");

        LocalDateTime startDateTime = counterService.getStartOf90MinWithLeastCars().orElse(null);

        if (Objects.isNull(startDateTime)) {
            return;
        }

        System.out.println(startDateTime + " - " + startDateTime.plus(90, ChronoUnit.MINUTES));
    }

}
