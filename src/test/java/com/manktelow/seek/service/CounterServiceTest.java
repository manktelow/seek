package com.manktelow.seek.service;

import com.manktelow.seek.model.Window;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CounterServiceTest {

    LocalDateTime now;

    @Before
    public void SetUp() {
        now = LocalDateTime.now();
    }

    @Test
    public void getCarCountByDay() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 5"),
                Window.fromString("2021-12-02T16:00:00 11"),
                Window.fromString("2021-12-02T16:30:00 2"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Map<String, Long> actual = svc.getCarCountByDay();

        assertTrue(actual.containsKey("2021-12-01"));
        assertTrue(actual.containsKey("2021-12-02"));
        assertEquals(Long.valueOf(5), actual.getOrDefault("2021-12-01", 0L));
        assertEquals(Long.valueOf(13), actual.getOrDefault("2021-12-02", 0L));
    }

    @Test
    public void getCarCountByDayNoWindows() {
        CounterService svc = CounterService.withSortedWindows(new ArrayList<>());

        Map<String, Long> actual = svc.getCarCountByDay();

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getStartOf90MinWithLeastCars() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 5"),
                Window.fromString("2021-12-01T16:00:00 11"),
                Window.fromString("2021-12-01T16:30:00 2"),
                Window.fromString("2021-12-01T17:00:00 1"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals("2021-12-01T16:00", actual.orElse(now).toString());
    }

    @Test
    public void getStartOf90MinWithLeastCarsZeroConsidered() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 2"),
                Window.fromString("2021-12-01T16:00:00 0"),
                Window.fromString("2021-12-01T16:30:00 1"),
                Window.fromString("2021-12-01T17:00:00 1"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals("2021-12-01T16:00", actual.orElse(now).toString());
    }

    @Test
    public void getStartOf90MinWithLeastCarsWhenNonContiguous() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:00:00 2"),
                Window.fromString("2021-12-01T16:00:00 15"),
                Window.fromString("2021-12-01T16:30:00 25"),
                Window.fromString("2021-12-01T17:00:00 1"),
                Window.fromString("2021-12-01T17:30:00 5"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals("2021-12-01T16:30", actual.orElse(now).toString());
    }

    @Test
    public void getStartOf90MinWithLeastCarsWhenNonContiguousDifferentDays() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:00:00 2"),
                Window.fromString("2021-12-01T15:30:00 1"),
                Window.fromString("2021-12-02T16:00:00 25"),
                Window.fromString("2021-12-02T17:00:00 1"),
                Window.fromString("2021-12-02T17:30:00 1"),
                Window.fromString("2021-12-02T18:00:00 1"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals("2021-12-02T17:00", actual.orElse(now).toString());
    }

    @Test
    public void getStartOf90MinWithLeastCarsNoResultWhenNonContiguous() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:00:00 2"),
                Window.fromString("2021-12-01T16:30:00 1"),
                Window.fromString("2021-12-02T17:00:00 25"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void getStartOf90MinWithLeastCarsUnordered() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:00:00 2"),
                Window.fromString("2021-12-01T15:30:00 1"),
                Window.fromString("2021-12-01T16:00:00 3"),
                Window.fromString("2021-12-01T14:30:00 2"));

        CounterService svc = CounterService.withSortedWindows(windows);

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals("2021-12-01T14:30", actual.orElse(now).toString());
    }

    @Test
    public void getStartOf90MinWithLeastCarsNoWindows() {
        CounterService svc = CounterService.withSortedWindows(new ArrayList<>());

        Optional<LocalDateTime> actual = svc.getStartOf90MinWithLeastCars();

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void getTopThreeWindowsByCarCount() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 5"),
                Window.fromString("2021-12-01T16:00:00 11"),
                Window.fromString("2021-12-02T17:30:00 2"),
                Window.fromString("2021-12-02T18:30:00 23"),
                Window.fromString("2021-12-02T19:00:00 20"));

        CounterService svc = CounterService.withSortedWindows(windows);

        List<Window> actual = svc.getTopThreeWindowsByCarCount();

        assertEquals(3, actual.size());
        assertEquals("2021-12-02T18:30 23", actual.get(0).toPrintString());
        assertEquals("2021-12-02T19:00 20", actual.get(1).toPrintString());
        assertEquals("2021-12-01T16:00 11", actual.get(2).toPrintString());
    }

    @Test
    public void getTopThreeWindowsByCarCountDuplicateCount() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 5"),
                Window.fromString("2021-12-01T16:00:00 20"),
                Window.fromString("2021-12-02T17:30:00 20"),
                Window.fromString("2021-12-02T18:30:00 23"),
                Window.fromString("2021-12-02T19:00:00 20"));

        CounterService svc = CounterService.withSortedWindows(windows);

        List<Window> actual = svc.getTopThreeWindowsByCarCount();

        assertEquals(3, actual.size());
        assertEquals("2021-12-02T18:30 23", actual.get(0).toPrintString());
        assertEquals("2021-12-01T16:00 20", actual.get(1).toPrintString());
        assertEquals("2021-12-02T17:30 20", actual.get(2).toPrintString());
    }

    @Test
    public void getTotalCarCount() {
        List<Window> windows = Arrays.asList(
                Window.fromString("2021-12-01T15:30:00 5"),
                Window.fromString("2021-12-02T16:00:00 11"),
                Window.fromString("2021-12-02T16:30:00 2"));

        CounterService svc = CounterService.withSortedWindows(windows);

        int actual = svc.getTotalCarCount().orElse(0);

        assertEquals(18, actual);
    }

    @Test
    public void getTotalCarCountNoWindows() {
        CounterService svc = CounterService.withSortedWindows(new ArrayList<>());

        int actual = svc.getTotalCarCount().orElse(0);

        assertEquals(0, actual);
    }
}
