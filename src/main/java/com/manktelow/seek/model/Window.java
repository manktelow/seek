package com.manktelow.seek.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Window {

    private LocalDateTime dateTime;
    private int carCount;

    public Window(LocalDateTime dateTime, int carCount) {
        this.dateTime = dateTime;
        this.carCount = carCount;
    }

    public static Window fromString(String input) {
        String[] splitInput = input.split(" ");

        return new Window(
                LocalDateTime.parse(splitInput[0]),
                Integer.parseInt(splitInput[1])
        );
    }

    public LocalDate getDate() {
        return this.getDateTime().toLocalDate();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window that = (Window) o;
        return carCount == that.carCount && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, carCount);
    }

    @Override
    public String toString() {
        return "Window{" +
                "dateTime=" + dateTime +
                ", carCount=" + carCount +
                '}';
    }

    public String toPrintString() {
        return dateTime + " " + carCount;
    }
}
