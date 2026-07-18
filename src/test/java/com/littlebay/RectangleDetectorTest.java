package com.littlebay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class RectangleDetectorTest {

    @Test
    @DisplayName("detect multiple rectangles not touching sides")
    void multipleRectangles() {
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

        List<RectangleDetector.Rectangle> rectangles = RectangleDetector.findWhiteRectangles(bitmap);
        assertThat(rectangles.size(), is(3));

        assertThat(rectangles.get(0),
                is( new RectangleDetector.Rectangle(1, 1, 3, 2)));

        assertThat(rectangles.get(1),
                is( new RectangleDetector.Rectangle(5, 1, 2, 2)));

        assertThat(rectangles.get(2),
                is( new RectangleDetector.Rectangle(1, 4, 6, 3)));

    }

    @Test
    @DisplayName("detect single rectangle not touching out of bounds")
    void singleRectangle() {



        int[][] bitmap = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1}
        };

        List<RectangleDetector.Rectangle> rectangles = RectangleDetector.findWhiteRectangles(bitmap);
        assertThat(rectangles.size(), is(1));

        assertThat(rectangles.get(0),
                is( new RectangleDetector.Rectangle(3, 2, 3, 2)));

    }


    @Test
    @DisplayName("detect two rectangles both touching sides")
    void twoRectanglesBothTouchingBounds() {



        int[][] bitmap = {
                {0, 0, 0, 1, 1, 1, 1},
                {0, 0, 0, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 0, 0, 0}
        };

        List<RectangleDetector.Rectangle> rectangles = RectangleDetector.findWhiteRectangles(bitmap);
        assertThat(rectangles.size(), is(2));

        assertThat(rectangles.get(0),
                is( new RectangleDetector.Rectangle(0, 0, 3, 2)));

        assertThat(rectangles.get(1),
                is( new RectangleDetector.Rectangle(4, 3, 3, 2)));
    }

    @Test
    @DisplayName("detect only one rectangle when overlapping")
    void twoRectanglesTouchingBoundsAndOverlapping() {


        int[][] bitmap = {
                {0, 0, 0, 0, 1, 1, 1},
                {0, 0, 0, 0, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0}
        };

        List<RectangleDetector.Rectangle> rectangles = RectangleDetector.findWhiteRectangles(bitmap);
        assertThat(rectangles.size(), is(1));

        assertThat(rectangles.get(0),
                is(new RectangleDetector.Rectangle(0, 0, 4, 3)));
    }

}
