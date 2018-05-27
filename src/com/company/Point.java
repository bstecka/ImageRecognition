package com.company;

public class Point {
    public double x;
    public double y;
    public int[] descriptor;

    public Point(double x, double y, int[] descriptor) {
        this.x = x;
        this.y = y;
        this.descriptor = descriptor;
    }

    public int getDistance(Point point) {
        int distance = 0;
        for (int i = 0, j = 0; i < this.descriptor.length && j < point.descriptor.length; i++, j++)
            distance += Math.abs(this.descriptor[i] - point.descriptor[j]);
        return distance;
    }

    @Override
    public String toString() {
        return  "(" + x + ", " + y + ")";
    }
}
