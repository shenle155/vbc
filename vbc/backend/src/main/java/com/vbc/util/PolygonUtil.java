package com.vbc.util;

import java.util.List;

public class PolygonUtil {

    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    /**
     * Ray-casting algorithm: test if point is inside polygon.
     */
    public static boolean isPointInPolygon(double px, double py, List<Point> polygon) {
        int count = 0;
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % n);
            if ((a.y > py) != (b.y > py)
                    && px < (b.x - a.x) * (py - a.y) / (b.y - a.y) + a.x) {
                count++;
            }
        }
        return count % 2 == 1;
    }
}
