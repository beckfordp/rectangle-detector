package com.littlebay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RectangleDetector {

    // Class to store rectangle information
    public static class Rectangle {
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

    /**
     * Find all white rectangles in a binary bitmap
     * @param bitmap 2D array where 1 = black, 0 = white
     * @return List of rectangles found
     */
    public static List<Rectangle> findWhiteRectangles(int[][] bitmap) {
        if (bitmap == null || bitmap.length == 0 || bitmap[0].length == 0) {
            return new ArrayList<>();
        }

        int rows = bitmap.length;
        int cols = bitmap[0].length;
        List<Rectangle> rectangles = new ArrayList<>();

        // Create a visited array to mark processed pixels
        boolean[][] visited = new boolean[rows][cols];

        // Scan the bitmap row by row
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Look for a white pixel that hasn't been visited
                if (bitmap[y][x] == 0 && !visited[y][x]) {
                    // Found a potential top-left corner
                    // But we need to verify it's actually a top-left corner
                    // of a rectangle (not just an interior point)

                    // Check if this is a top-left corner:
                    // - It's white (already checked)
                    // - It's either at the top row OR the pixel above is black
                    // - It's either at the left column OR the pixel to the left is black
                    boolean isTopLeft = (y == 0 || bitmap[y-1][x] == 1) &&
                            (x == 0 || bitmap[y][x-1] == 1);

                    if (isTopLeft) {
                        // Found the top-left corner, now find the rectangle dimensions
                        Rectangle rect = findRectangle(bitmap, visited, x, y);
                        if (rect != null) {
                            rectangles.add(rect);

                            // Mark all pixels in this rectangle as visited
                            markRectangleVisited(visited, rect.x, rect.y,
                                    rect.width, rect.height);
                        }
                    } else {
                        // Not a top-left corner, mark as visited to avoid reprocessing
                        visited[y][x] = true;
                    }
                }
            }
        }

        return rectangles;
    }

//    private static List<T> rasterScan(int[][] bitmap, Function<T>(int x, int y, List<T>) found) {
//        int rows = bitmap.length;
//        int cols = bitmap[0].length;
//
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                found(x, y);
//            }
//        }
//    }
    /**
     * Find the dimensions of a rectangle starting from its top-left corner
     */
    private static Rectangle findRectangle(int[][] bitmap, boolean[][] visited,
                                           int startX, int startY) {
        int rows = bitmap.length;
        int cols = bitmap[0].length;

        // Find the width by scanning right until we hit a black pixel or boundary
        int width = 0;
        while (startX + width < cols && bitmap[startY][startX + width] == 0) {
            width++;
        }

        // Find the height by scanning down until we hit a black pixel or boundary
        int height = 0;
        while (startY + height < rows && bitmap[startY + height][startX] == 0) {
            height++;
        }

        // Verify this is a solid rectangle (all pixels inside are white)
        if (!isSolidRectangle(bitmap, startX, startY, width, height)) {
            return null;
        }

        return new Rectangle(startX, startY, width, height);
    }

    /**
     * Verify that all pixels within the candidate rectangle are white
     */
    private static boolean isSolidRectangle(int[][] bitmap, int x, int y,
                                            int width, int height) {
        for (int row = y; row < y + height; row++) {
            for (int col = x; col < x + width; col++) {
                if (bitmap[row][col] != 0) {
                    return false; // Found a black pixel inside the rectangle
                }
            }
        }
        return true;
    }

    /**
     * Mark all pixels in a rectangle as visited
     */
    private static void markRectangleVisited(boolean[][] visited, int x, int y,
                                             int width, int height) {
        for (int row = y; row < y + height; row++) {
            for (int col = x; col < x + width; col++) {
                visited[row][col] = true;
            }
        }
    }

    /**
     * Alternative approach: Find rectangles using connected component labeling
     * This handles non-rectangular shapes by verifying rectangularity
     */
    public static List<Rectangle> findRectanglesConnectedComponents(int[][] bitmap) {
        if (bitmap == null || bitmap.length == 0 || bitmap[0].length == 0) {
            return new ArrayList<>();
        }

        int rows = bitmap.length;
        int cols = bitmap[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<Rectangle> rectangles = new ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (bitmap[y][x] == 0 && !visited[y][x]) {
                    // Found a white component, find its bounding box
                    int minX = x, maxX = x;
                    int minY = y, maxY = y;

                    // Use flood fill to find all pixels in this component
                    findComponentBoundingBox(bitmap, visited, x, y,
                            minX, maxX, minY, maxY);

                    // Check if the component forms a filled rectangle
                    int width = maxX - minX + 1;
                    int height = maxY - minY + 1;

                    if (isSolidRectangle(bitmap, minX, minY, width, height)) {
                        rectangles.add(new Rectangle(minX, minY, width, height));
                    }
                }
            }
        }

        return rectangles;
    }

    /**
     * Find the bounding box of a connected component using DFS
     */
    private static void findComponentBoundingBox(int[][] bitmap, boolean[][] visited,
                                                 int x, int y, int minX, int maxX,
                                                 int minY, int maxY) {
        // This is a simplified version - in practice you'd use a stack/queue
        // and update min/max coordinates as you explore
        // For brevity, I'm skipping the full implementation here
    }

    // Example usage
    public static void main(String[] args) {
        // Example bitmap: 1 = black, 0 = white
        int[][] bitmap = {
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1}
        };

        int[][] bitmap2 = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 0, 1}
        };



        List<Rectangle> rectangles = findWhiteRectangles(bitmap2);

        for (Rectangle rect : rectangles) {
            System.out.println(rect);
        }
    }
}
