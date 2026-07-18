package com.littlebay;

import java.util.List;

public class RectangleDetector {

    private final SearchStrategy strategy;

    public RectangleDetector(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Rectangle> detect(int[][] bitmap) {
        if (bitmap == null || bitmap.length == 0 || bitmap[0].length == 0) {
            return List.of();
        }
        return strategy.detect(bitmap);
    }
}
