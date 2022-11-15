package com.manktelow.seek.service;

import com.manktelow.seek.model.Counter;
import com.manktelow.seek.model.Window;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class CounterService {

    private final Counter counter;

    private CounterService(Counter counter) {
        this.counter = counter;
    }

    public static CounterService withSortedWindows(List<Window> windows) {
        return new CounterService(new Counter(windows));
    }

    /**
     * Get the number of cars seen per day in reverse chronological order
     * @return Map<String, Long> a map of dates (yyyy-mm-dd) with corresponding car count
     */
    public Map<String, Long> getCarCountByDay() {
        // Use a linkedHashMap to guarantee ordering
        LinkedHashMap<String, Long> countsByDay = new LinkedHashMap<>();

        counter.getWindows().stream()
                .collect(Collectors.groupingBy(Window::getDate, Collectors.summarizingInt(Window::getCarCount)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(day -> countsByDay.put(day.getKey().toString(), day.getValue().getSum()));

        return countsByDay;
    }

    /**
     * Get the start datetime of the 90 minutes with the least amount of cars
     * In order to be considered 3 contiguous 30 min windows need to have been recorded
     * @return Optional<LocalDateTime> the start datetime of the 90minute window
     */
    public Optional<LocalDateTime> getStartOf90MinWithLeastCars() {
        HashMap<LocalDateTime, Integer> ninetyMinWindows = new HashMap<>();

        LocalDateTime smallestWindow = null;
        Integer leastCars = null;

        for (Window current : counter.getWindows()) {
            boolean hasMidWindow = false;
            boolean hasStartWindow = false;

            ninetyMinWindows.put(current.getDateTime(), current.getCarCount());

            LocalDateTime startOfWindow = current.getDateTime().minus(1, ChronoUnit.HOURS);
            LocalDateTime midWindow = current.getDateTime().minus(30, ChronoUnit.MINUTES);

            Integer windowCount = null;
            Integer startWindowCount = ninetyMinWindows.get(startOfWindow);
            if (startWindowCount != null) {
                windowCount = startWindowCount + current.getCarCount();
                ninetyMinWindows.replace(startOfWindow, windowCount);
                hasStartWindow = true;
            }

            Integer midWindowCount = ninetyMinWindows.get(midWindow);
            if (midWindowCount != null) {
                ninetyMinWindows.replace(midWindow, midWindowCount + current.getCarCount());
                hasMidWindow = true;
            }

            if (hasMidWindow && hasStartWindow) {
                boolean shouldUpdate = Objects.isNull(leastCars) || leastCars > windowCount;
                leastCars = shouldUpdate ? windowCount : leastCars;
                smallestWindow = shouldUpdate ? startOfWindow : smallestWindow;
            }
        }

        return Optional.ofNullable(smallestWindow);
    }

    /**
     * Get the top 3 30min windows with the most amount of cars
     * Will always return 3 even if there are 4 windows with the same count - it will return the earliest 3
     * @return List<Window> list of windows with their datetime and count
     */
    public List<Window> getTopThreeWindowsByCarCount() {
        return counter.getWindows().stream()
                .sorted(Comparator.comparing(Window::getCarCount).reversed())
                .limit(3)
                .toList();
    }

    /**
     * Get the total count of all cars across all 30min windows
     * @return Optional<Long> sum of all cars counted
     */
    public Optional<Integer> getTotalCarCount() {
        return counter.getWindows().stream()
                .map(Window::getCarCount)
                .reduce(Integer::sum);
    }
}
