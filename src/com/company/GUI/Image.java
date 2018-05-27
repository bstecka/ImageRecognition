package com.company.GUI;

import com.company.Point;
import com.company.PointPair;
import com.company.SiftReader;

public class Image {
    String imagePath;
    Point[] keyPoints;

    public Image(String imagePath, Point[] keyPoints) {
        this.imagePath = imagePath;
        this.keyPoints = keyPoints;
    }

    public void readImage(String imagePath) {
        this.keyPoints = new SiftReader().getKeyPoints(imagePath);
        this.imagePath = imagePath;
    }

    public Point getNeighbour(Point point) {
        int diff, minDiff = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int i = 0; i < keyPoints.length; i++){
            diff = point.getDistance(keyPoints[i]);
            if (diff < minDiff && diff != 0) {
                minDiff = diff;
                closestPoint = keyPoints[i];
            }
        }
        return closestPoint;
    }

    public PointPair[] getKeyPointPairs(Image otherImage){
        PointPair[] pairs = new PointPair[keyPoints.length];
        int pairCount = 0;
        for (int i = 0; i < keyPoints.length; i++){
            Point point = keyPoints[i];
            Point foreignNeighbour = otherImage.getNeighbour(point);
            if (getNeighbour(foreignNeighbour) == point){
                pairs[pairCount] = new PointPair(point, foreignNeighbour);
                pairCount++;
            }
        }
        return pairs;
    }
}
