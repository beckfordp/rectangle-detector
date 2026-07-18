package com.littlebay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelRasterStrategy extends RasterStrategy {

    private final int threads;
    private final ExecutorService pool;

    public ParallelRasterStrategy(int threads) {
        if (threads < 1) throw new IllegalArgumentException("threads must be >= 1");
        this.threads = threads;
        this.pool = Executors.newFixedThreadPool(threads,
                r -> { Thread t = new Thread(r); t.setDaemon(true); return t; });
    }

    @Override
    public List<Rectangle> find(int[][] bitmap) {
        int rows = bitmap.length;
        int cols = bitmap[0].length;
        int bandSize = Math.max(1, (rows + threads - 1) / threads);

        List<Future<List<Rectangle>>> futures = new ArrayList<>();
        for (int band = 0; band < rows; band += bandSize) {
            final int startRow = band;
            final int endRow = Math.min(band + bandSize, rows);
            futures.add(pool.submit(() -> findInBand(bitmap, startRow, endRow, rows, cols)));
        }

        List<Rectangle> all = new ArrayList<>();
        for (Future<List<Rectangle>> f : futures) {
            try {
                all.addAll(f.get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Parallel detection failed", e);
            }
        }

        all.sort(Comparator.comparingInt((Rectangle r) -> r.y * cols + r.x));
        return all;
    }

    private static List<Rectangle> findInBand(int[][] bitmap, int startRow, int endRow,
                                               int rows, int cols) {
        return IntStream.range(startRow, endRow)
                .boxed()
                .flatMap(y -> IntStream.range(0, cols)
                        .filter(x -> bitmap[y][x] == 0 && RasterScanSupport.isTopLeft(bitmap, x, y))
                        .mapToObj(x -> RasterScanSupport.detect(bitmap, x, y, rows, cols)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Parallel(" + threads + ")";
    }
}
