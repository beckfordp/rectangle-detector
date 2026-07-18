package com.littlebay;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HistogramStrategy implements SearchStrategy {

    @Override
    public List<Rectangle> detect(int[][] bitmap) {
        int rows = bitmap.length;
        int cols = bitmap[0].length;

        int[][] runRight = buildRunRight(bitmap, rows, cols);
        int[][] runDown  = buildRunDown(bitmap, rows, cols);

        return IntStream.range(0, rows)
                .boxed()
                .flatMap(y -> IntStream.range(0, cols)
                        .filter(x -> bitmap[y][x] == 0 && RasterScanSupport.isTopLeft(bitmap, x, y))
                        .mapToObj(x -> detect(runRight, runDown, x, y)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // runRight[y][x] = consecutive white pixels rightward from (x,y) inclusive
    private static int[][] buildRunRight(int[][] bitmap, int rows, int cols) {
        int[][] r = new int[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = cols - 1; x >= 0; x--) {
                if (bitmap[y][x] == 0) {
                    r[y][x] = (x + 1 < cols) ? r[y][x + 1] + 1 : 1;
                }
            }
        }
        return r;
    }

    // runDown[y][x] = consecutive white pixels downward from (x,y) inclusive
    private static int[][] buildRunDown(int[][] bitmap, int rows, int cols) {
        int[][] d = new int[rows][cols];
        for (int x = 0; x < cols; x++) {
            for (int y = rows - 1; y >= 0; y--) {
                if (bitmap[y][x] == 0) {
                    d[y][x] = (y + 1 < rows) ? d[y + 1][x] + 1 : 1;
                }
            }
        }
        return d;
    }

    private static Rectangle detect(int[][] runRight, int[][] runDown, int x, int y) {
        int width  = runRight[y][x];
        int height = runDown[y][x];

        for (int row = y; row < y + height; row++) {
            if (runRight[row][x] < width) return null;
        }

        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Histogram";
    }
}
