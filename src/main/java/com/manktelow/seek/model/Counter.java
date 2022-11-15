package com.manktelow.seek.model;

import java.util.List;
import java.util.Objects;

public class Counter {

    private List<Window> sortedWindows;

    public Counter(List<Window> windows) {
        this.sortedWindows = windows;
    }

    public List<Window> getSortedWindows() {
        return sortedWindows;
    }

    public void setSortedWindows(List<Window> sortedWindows) {
        this.sortedWindows = sortedWindows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return Objects.equals(sortedWindows, counter.sortedWindows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortedWindows);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "windows=" + sortedWindows +
                '}';
    }
}
