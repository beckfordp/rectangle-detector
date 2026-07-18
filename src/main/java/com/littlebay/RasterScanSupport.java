package com.littlebay;

class RasterScanSupport {

    static boolean isTopLeft(int[][] bitmap, int x, int y) {
        return (y == 0 || bitmap[y - 1][x] == 1) &&
               (x == 0 || bitmap[y][x - 1] == 1);
    }

    static Rectangle detect(int[][] bitmap, int x, int y, int rows, int cols) {
        int width = 0;
        while (x + width < cols && bitmap[y][x + width] == 0) width++;

        int height = 0;
        while (y + height < rows && bitmap[y + height][x] == 0) height++;

        for (int row = y; row < y + height; row++) {
            for (int col = x; col < x + width; col++) {
                if (bitmap[row][col] != 0) return null;
            }
        }

        return new Rectangle(x, y, width, height);
    }
}
