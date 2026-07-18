package com.littlebay;

import java.util.List;

public class RectangleFinder {

    private final SearchStrategy strategy;

    public RectangleFinder(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Rectangle> find(int[][] bitmap) {
        if (bitmap == null || bitmap.length == 0 || bitmap[0].length == 0) {
            return List.of();
        }
        return strategy.find(bitmap);
    }
}
