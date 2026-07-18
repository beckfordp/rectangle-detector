package com.littlebay;

import java.util.Objects;

// Class to store rectangle information
public class Rectangle {
    int x;          // top-left x coordinate
    int y;          // top-left y coordinate
    int width;      // width in pixels
    int height;     // height in pixels

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("Rectangle at (%d, %d) - Width: %d, Height: %d",
                x, y, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return x == rectangle.x && y == rectangle.y && width == rectangle.width && height == rectangle.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
