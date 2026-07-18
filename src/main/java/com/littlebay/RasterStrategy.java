package com.littlebay;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RasterStrategy implements SearchStrategy {

    @Override
    public List<Rectangle> find(int[][] bitmap) {
        int rows = bitmap.length;
        int cols = bitmap[0].length;
        return IntStream.range(0, rows)
                .boxed()
                .flatMap(y -> IntStream.range(0, cols)
                        .filter(x -> bitmap[y][x] == 0 && RasterScanSupport.isTopLeft(bitmap, x, y))
                        .mapToObj(x -> RasterScanSupport.detect(bitmap, x, y, rows, cols)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Raster";
    }
}
