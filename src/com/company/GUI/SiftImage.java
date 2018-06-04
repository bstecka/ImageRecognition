package com.company.GUI;

import com.company.Point;
import com.company.PointPair;
import org.ejml.simple.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SiftImage {
    String imagePath;
    Point[] keyPoints;

    public SiftImage(String imagePath, Point[] keyPoints) {
        this.imagePath = imagePath;
        this.keyPoints = keyPoints;
    }

    public SiftImage(String imagePath) {
        this.imagePath = imagePath;
        this.keyPoints = getKeyPoints(imagePath);
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
        Point closestPoint = keyPoints[0];
        for (int i = 0; i < keyPoints.length; i++){
            diff = point.getDistance(keyPoints[i]);
            if (diff < minDiff) {
                minDiff = diff;
                closestPoint = keyPoints[i];
            }
        }
        return closestPoint;
    }

    private PointPair[] getRandomSamples(PointPair[] pairs, int sampleSize) {
        if (sampleSize > pairs.length)
            throw new IllegalArgumentException();
        Random rand = new Random();
        int[] ints = new int [sampleSize];
        for (int i = 0; i < sampleSize; i++){
            int value = rand.nextInt(sampleSize);
            while(contains(ints, value))
                value = rand.nextInt(sampleSize);
            ints[i] = value;
        }
        PointPair[] samples = new PointPair[sampleSize];
        for (int i = 0; i < sampleSize; i++){
            samples[i] = pairs[ints[i]];
        }
        return samples;
    }

    public SimpleMatrix getBestModelRANSAC(SiftImage otherImage, int iterations, boolean perspective) {
        SimpleMatrix bestModel = null;
        double bestScore = 0, maxError = Integer.MIN_VALUE;
        PointPair[] pairs = getKeyPointPairs(otherImage), samples;
        for (int i = 0; i < iterations; i++) {
            SimpleMatrix model = null;
            if (perspective) {
                samples = getRandomSamples(getKeyPointPairs(otherImage), 4);
                model = calculateModelPerspective(samples);
            }
            else {
                samples = getRandomSamples(getKeyPointPairs(otherImage), 3);
                model = calculateModelAffine(samples);
            }
            int score = 0;
            double error;
            for (int j = 0; j < pairs.length; j++){
                if (perspective)
                    error = getErrorPerspective(model, pairs[j]);
                else
                    error = getErrorAffine(model, pairs[j]);
                if (error < maxError)
                    score++;
                else if (error > maxError)
                    maxError = error;
            }
            if (score > bestScore) {
                bestScore = score;
                bestModel = model;
            }
        }
        return bestModel;
    }

    public PointPair[] getRANSACPairs(SiftImage otherImage, boolean perspective) {
        SimpleMatrix model = getBestModelRANSAC(otherImage, 100, perspective);
        PointPair[] pairs = getKeyPointPairs(otherImage);
        PointPair[] RANSACPairs = new PointPair[pairs.length];
        for (int i = 0; i < pairs.length; i++){
            Point first = pairs[i].getKey();
            Point second = getPoint(model, first);
            RANSACPairs[i] = new PointPair(first, second);
        }
        return RANSACPairs;
    }

    public double getErrorAffine(SimpleMatrix A, PointPair pair) {
        double[][] dB = new double[3][1];
        dB[1][0] = pair.getKey().x;
        dB[2][0] = pair.getKey().y;
        dB[3][0] = 1;
        SimpleMatrix B = new SimpleMatrix(dB);
        SimpleMatrix result = A.mult(B);
        double u1 = result.get(0,0);
        double v1 = result.get(1,0);
        double u2 = pair.getValue().x;
        double v2 = pair.getValue().y;
        return Math.hypot(u1-u2, v1-v2);
    }

    public Point getPoint(SimpleMatrix A, Point point) {
        double[][] dB = new double[3][1];
        dB[1][0] = point.x;
        dB[2][0] = point.y;
        dB[3][0] = 1;
        SimpleMatrix B = new SimpleMatrix(dB);
        SimpleMatrix result = A.mult(B);
        float x = (float) result.get(0,0);
        float y = (float) result.get(1,0);
        return new Point(x, y, new short[1]);
    }

    public double getErrorPerspective(SimpleMatrix H, PointPair pair) {
        double[][] dB = new double[3][1];
        dB[1][0] = pair.getKey().x;
        dB[2][0] = pair.getKey().y;
        dB[3][0] = 1;
        SimpleMatrix B = new SimpleMatrix(dB);
        SimpleMatrix result = H.mult(B);
        double t = result.get(2,0);
        double u1 = result.get(0,0)/t;
        double v1 = result.get(1,0)/t;
        double u2 = pair.getValue().x;
        double v2 = pair.getValue().y;
        return Math.hypot(u1-u2, v1-v2);
    }

    private SimpleMatrix calculateModelAffine(PointPair[] s) {
        double[][] dB = new double[6][6];
        double[][] dC = new double[6][1];
        for (int i = 0; i < 3; i++){
            double x = s[i].getKey().x;
            double y = s[i].getKey().y;
            double u = s[i].getValue().x;
            double v = s[i].getValue().y;
            dB[i][0] = dB[i+3][3] = x;
            dB[i][1] = dB[i+3][4] = y;
            dB[i][2] = dB[i+3][5] = 1;
            dC[i][0] = u;
            dC[i+3][0] = v;
        }
        SimpleMatrix B = new SimpleMatrix(dB);
        SimpleMatrix C = new SimpleMatrix(dC);
        SimpleMatrix result = B.transpose().mult(C);
        SimpleMatrix A = new SimpleMatrix(6, 1);
        A.set(0,0, result.get(0, 0));
        A.set(0,1, result.get(1, 0));
        A.set(0,2, result.get(2, 0));
        A.set(1,0, result.get(3, 0));
        A.set(1,1, result.get(4, 0));
        A.set(1,2, result.get(5, 0));
        A.set(2,2,1);
        return A;
    }

    private SimpleMatrix calculateModelPerspective(PointPair[] s) {
        double[][] dB = new double[8][8];
        double[][] dC = new double[8][1];
        for (int i = 0; i < 4; i++){
            double x = s[i].getKey().x;
            double y = s[i].getKey().y;
            double u = s[i].getValue().x;
            double v = s[i].getValue().y;
            dB[i][0] = dB[i+4][3] = x;
            dB[i][1] = dB[i+4][4] = y;
            dB[i][2] = dB[i+4][5] = 1;
            dC[i][0] = u;
            dC[i+4][0] = v;
            dB[i][6] = -u * x;
            dB[i+4][6] = -v * x;
            dB[i][7] = -u * y;
            dB[i+4][7] = -v * y;
        }
        SimpleMatrix B = new SimpleMatrix(dB);
        SimpleMatrix C = new SimpleMatrix(dC);
        SimpleMatrix result = B.transpose().mult(C);
        SimpleMatrix A = new SimpleMatrix(6, 1);
        A.set(0,0, result.get(0, 0));
        A.set(0,1, result.get(1, 0));
        A.set(0,2, result.get(2, 0));
        A.set(1,0, result.get(3, 0));
        A.set(1,1, result.get(4, 0));
        A.set(1,2, result.get(5, 0));
        A.set(2,0,result.get(6, 0));
        A.set(2,1,result.get(7, 0));
        A.set(2,2,1);
        return A;
    }

    public Point[] getNeighbours(Point point, int n, Point[] keyPoints) {
        Point[] neigbhoursTemp = new Point[n];
        int[] diffs = new int[keyPoints.length];
        int diff, m = 0;
        for (int i = 0; i < keyPoints.length; i++){
            diffs[i] = point.getDistance(keyPoints[i]);
        }
        Arrays.sort(diffs);
        int nthDiff = diffs[n];
        for (int i = 0; i < keyPoints.length && m < n; i++){
            diff = point.getDistance(keyPoints[i]);
            if (diff < nthDiff) {
                neigbhoursTemp[m] = keyPoints[i];
                m++;
            }
        }
        Point[] neighbours = new Point[m];
        System.arraycopy(neigbhoursTemp, 0, neighbours, 0, m);
        return neighbours;
    }

    private boolean contains(Point[] array, Point point){
        boolean found = false;
        for (int i = 0; i < array.length && !found; i++) {
            if(array[i].equals(point))
                found = true;
        }
        return found;
    }

    private boolean contains(int[] array, int value){
        boolean found = false;
        for (int i = 0; i < array.length && !found; i++) {
            if(array[i] == value)
                found = true;
        }
        return found;
    }

    public PointPair[] getConsistentPairs(SiftImage otherImage, int neighbourSize, int threshold){
        PointPair[] pairs = getKeyPointPairs(otherImage);
        PointPair[] consistentPairsTemp = new PointPair[pairs.length];
        Point[] otherKeyPoints = otherImage.keyPoints;
        int c = 0;
        for (int i = 0; i < pairs.length; i++){
            Point Pi = pairs[i].getKey();
            Point Pj = pairs[i].getValue();
            Point[] PiNeighbours = getNeighbours(Pi, neighbourSize, keyPoints);
            Point[] PjNeighbours = getNeighbours(Pj, neighbourSize, otherKeyPoints);
            int nOfNeighbouringPairs = 0;
            for (int j = i + 1; j < pairs.length && nOfNeighbouringPairs < threshold; j++){
                Point Qi = pairs[j].getKey();
                Point Qj = pairs[j].getValue();
                if (contains(PiNeighbours, Qi) && contains(PjNeighbours, Qj)) {
                    nOfNeighbouringPairs++;
                }
            }
            if (nOfNeighbouringPairs >= threshold) {
                consistentPairsTemp[c] = pairs[i];
                c++;
            }
        }
        PointPair[] consistentPairs = new PointPair[c];
        System.arraycopy(consistentPairsTemp, 0, consistentPairs, 0, c);
        return consistentPairs;
    }

    public PointPair[] getKeyPointPairs(SiftImage otherImage){
        PointPair[] pairs = new PointPair[keyPoints.length];
        int pairCount = 0;
        for (int i = 0; i < keyPoints.length; i++){
            Point point = keyPoints[i];
            Point foreignNeighbour = otherImage.getNeighbour(point);
            if (getNeighbour(foreignNeighbour).equals(point)){
                pairs[pairCount] = new PointPair(point, foreignNeighbour);
                pairCount++;
            }
        }
        pairs = Arrays.copyOfRange(pairs, 0, pairCount);
        return pairs;
    }
}
