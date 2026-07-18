package com.littlebay;

import java.util.List;

public interface SearchStrategy {
    List<Rectangle> find(int[][] bitmap);
}
