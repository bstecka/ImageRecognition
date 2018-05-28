package com.company.GUI;

import com.company.Point;
import com.company.PointPair;
import com.company.SiftReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class SiftImage {
    String imagePath;
    Point[] keyPoints;

    public SiftImage(String imagePath, Point[] keyPoints) {
        this.imagePath = imagePath;
        this.keyPoints = keyPoints;
    }

    public SiftImage(String imagePath) {
        this.imagePath = imagePath;
        readImage(imagePath);
    }

    public void readImage(String imagePath) {
        this.keyPoints = getKeyPoints(imagePath);
        this.imagePath = imagePath;
    }

    public Point[] getKeyPoints(String filePath) {
        int traitCount, keyCount;
        Point[] keyPoints = null;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath)).useLocale(Locale.ENGLISH);
            if (scanner.hasNextLine()){
                traitCount = scanner.nextInt();
                keyCount = scanner.nextInt();
            } else
                throw new IllegalArgumentException();
            keyPoints = new Point[keyCount];
            int pointIndex = 0;
            boolean end = false;
            while (scanner.hasNextLine() && !end) {
                short[] descriptor = new short[traitCount];
                float x, y;
                if (scanner.hasNextFloat()) {
                    x = scanner.nextFloat();
                    y = scanner.nextFloat();
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    for (int i = 0; i < traitCount; i++) {
                        descriptor[i] = (short) scanner.nextInt();
                    }
                    keyPoints[pointIndex] = new Point(x, y, descriptor);
                    pointIndex++;
                } else
                    end = true;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } finally {
            if(scanner != null)
                scanner.close();
        }
        return keyPoints;
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

    public PointPair[] getKeyPointPairs(SiftImage otherImage){
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
