package com.company;

public class PointPair {

    public Point point1, point2;

    public PointPair(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getKey() {
        return point1;
    }

    public Point getValue() {
        return point2;
    }

    @Override
    public String toString() {
        return  "{ " + point1.toString() + ", " + point2.toString() + " }";
    }
}
