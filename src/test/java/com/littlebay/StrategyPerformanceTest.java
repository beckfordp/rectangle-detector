package com.littlebay;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StrategyPerformanceTest {

    private static final int WARMUP_RUNS   = 5;
    private static final int MEASURED_RUNS = 20;

    record NamedStrategy(String name, SearchStrategy strategy) {}
    record Scenario(String label, int[][] bitmap) {}
    record Result(String strategy, double meanMs, double stddevMs, int found) {}

    // ── Bitmap generators ────────────────────────────────────────────────────

    static int[][] sparse(int rows, int cols, int n) {
        int[][] bm = allBlack(rows, cols);
        Random rng = new Random(42);
        int placed = 0, attempts = 0;
        while (placed < n && attempts < n * 200) {
            attempts++;
            int w = 2 + rng.nextInt(Math.max(2, cols / 10));
            int h = 2 + rng.nextInt(Math.max(2, rows / 10));
            int x = rng.nextInt(Math.max(1, cols - w));
            int y = rng.nextInt(Math.max(1, rows - h));
            if (canPlace(bm, x, y, w, h, rows, cols)) {
                fillWhite(bm, x, y, w, h);
                placed++;
            }
        }
        return bm;
    }

    static int[][] dense(int rows, int cols, int cellSize) {
        int[][] bm = allBlack(rows, cols);
        int stride = cellSize + 1;
        for (int y = 1; y + cellSize <= rows; y += stride)
            for (int x = 1; x + cellSize <= cols; x += stride)
                fillWhite(bm, x, y, cellSize, cellSize);
        return bm;
    }

    private static int[][] allBlack(int rows, int cols) {
        int[][] bm = new int[rows][cols];
        for (int[] row : bm) Arrays.fill(row, 1);
        return bm;
    }

    private static void fillWhite(int[][] bm, int x, int y, int w, int h) {
        for (int row = y; row < y + h; row++)
            for (int col = x; col < x + w; col++)
                bm[row][col] = 0;
    }

    private static boolean canPlace(int[][] bm, int x, int y, int w, int h, int rows, int cols) {
        int x0 = Math.max(0, x - 1), y0 = Math.max(0, y - 1);
        int x1 = Math.min(cols, x + w + 1), y1 = Math.min(rows, y + h + 1);
        for (int row = y0; row < y1; row++)
            for (int col = x0; col < x1; col++)
                if (bm[row][col] == 0) return false;
        return true;
    }

    // ── Benchmark harness ────────────────────────────────────────────────────

    private Result benchmark(NamedStrategy ns, int[][] bitmap) {
        RectangleFinder finder = new RectangleFinder(ns.strategy());
        for (int i = 0; i < WARMUP_RUNS; i++) finder.find(bitmap);

        long[] nanos = new long[MEASURED_RUNS];
        int found = 0;
        for (int i = 0; i < MEASURED_RUNS; i++) {
            long t = System.nanoTime();
            found = finder.find(bitmap).size();
            nanos[i] = System.nanoTime() - t;
        }

        double mean = 0;
        for (long n : nanos) mean += n;
        mean /= MEASURED_RUNS;
        double variance = 0;
        for (long n : nanos) variance += (n - mean) * (n - mean);
        double stddev = Math.sqrt(variance / MEASURED_RUNS);

        return new Result(ns.name(), mean / 1_000_000.0, stddev / 1_000_000.0, found);
    }

    private void runScenario(Scenario scenario, List<NamedStrategy> strategies) {
        List<Result> results = new ArrayList<>();
        List<Rectangle> reference = null;

        for (NamedStrategy ns : strategies) {
            results.add(benchmark(ns, scenario.bitmap()));
            List<Rectangle> got = new RectangleFinder(ns.strategy()).find(scenario.bitmap());
            if (reference == null) {
                reference = got;
            } else {
                assertThat("strategies disagree on " + scenario.label(), got, is(reference));
            }
        }

        System.out.printf("%n%-26s%n", scenario.label());
        System.out.printf("  %-14s %10s %10s %8s%n", "strategy", "mean(ms)", "stddev(ms)", "rects");
        for (Result r : results)
            System.out.printf("  %-14s %10.3f %10.3f %8d%n",
                    r.strategy(), r.meanMs(), r.stddevMs(), r.found());
    }

    // ── Test ─────────────────────────────────────────────────────────────────

    @Test
    void compareStrategies() {
        List<NamedStrategy> strategies = List.of(
                new NamedStrategy("Raster",     new RasterStrategy()),
                new NamedStrategy("Histogram",  new HistogramStrategy()),
                new NamedStrategy("Parallel-2", new ParallelRasterStrategy(2)),
                new NamedStrategy("Parallel-8", new ParallelRasterStrategy(8))
        );

        List<Scenario> scenarios = List.of(
                new Scenario("small-sparse  50×50",     sparse(  50,   50,  3)),
                new Scenario("small-dense   50×50",     dense(   50,   50,  4)),
                new Scenario("medium-sparse 500×500",   sparse( 500,  500, 20)),
                new Scenario("medium-dense  500×500",   dense(  500,  500,  8)),
                new Scenario("large-sparse  2000×2000", sparse(2000, 2000, 50)),
                new Scenario("large-dense   2000×2000", dense( 2000, 2000, 16))
        );

        System.out.println("\n=== Strategy Performance Comparison ===");
        System.out.printf("warmup=%d  measured=%d%n", WARMUP_RUNS, MEASURED_RUNS);

        for (Scenario scenario : scenarios)
            runScenario(scenario, strategies);

        System.out.println();
    }
}
