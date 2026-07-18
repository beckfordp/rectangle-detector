package com.littlebay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AllStrategiesTest {

    static Stream<SearchStrategy> strategies() {
        return Stream.of(
                new RasterStrategy(),
                new HistogramStrategy(),
                new ParallelRasterStrategy(4)
        );
    }

    private List<Rectangle> find(SearchStrategy strategy, int[][] bitmap) {
        return new RectangleFinder(strategy).find(bitmap);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategies")
    @DisplayName("detect multiple rectangles not touching sides")
    void multipleRectangles(SearchStrategy strategy) {
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

        List<Rectangle> rectangles = find(strategy, bitmap);
        assertThat(rectangles.size(), is(3));
        assertThat(rectangles.get(0), is(new Rectangle(1, 1, 3, 2)));
        assertThat(rectangles.get(1), is(new Rectangle(5, 1, 2, 2)));
        assertThat(rectangles.get(2), is(new Rectangle(1, 4, 6, 3)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategies")
    @DisplayName("detect single rectangle not touching bounds")
    void singleRectangle(SearchStrategy strategy) {
        int[][] bitmap = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1}
        };

        List<Rectangle> rectangles = find(strategy, bitmap);
        assertThat(rectangles.size(), is(1));
        assertThat(rectangles.get(0), is(new Rectangle(3, 2, 3, 2)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategies")
    @DisplayName("detect two rectangles both touching bounds")
    void twoRectanglesBothTouchingBounds(SearchStrategy strategy) {
        int[][] bitmap = {
                {0, 0, 0, 1, 1, 1, 1},
                {0, 0, 0, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 0, 0, 0}
        };

        List<Rectangle> rectangles = find(strategy, bitmap);
        assertThat(rectangles.size(), is(2));
        assertThat(rectangles.get(0), is(new Rectangle(0, 0, 3, 2)));
        assertThat(rectangles.get(1), is(new Rectangle(4, 3, 3, 2)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategies")
    @DisplayName("detect only one rectangle when overlapping")
    void twoRectanglesTouchingBoundsAndOverlapping(SearchStrategy strategy) {
        int[][] bitmap = {
                {0, 0, 0, 0, 1, 1, 1},
                {0, 0, 0, 0, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0}
        };

        List<Rectangle> rectangles = find(strategy, bitmap);
        assertThat(rectangles.size(), is(1));
        assertThat(rectangles.get(0), is(new Rectangle(0, 0, 4, 3)));
    }
}
