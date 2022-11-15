package com.manktelow.seek.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Counter {

    private List<Window> windows;

    public Counter(List<Window> windows) {
        this.windows = windows;
    }

    public List<Window> getWindows() {
        return windows;
    }

    public void setWindows(List<Window> windows) {
        this.windows = windows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return Objects.equals(windows, counter.windows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windows);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "windows=" + windows +
                '}';
    }
}
