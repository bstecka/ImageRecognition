package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class SiftReader {

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
            while (scanner.hasNextLine()) {
                int[] descriptor = new int[traitCount];
                float x, y;
                if (scanner.hasNextFloat()) {
                    x = scanner.nextFloat();
                    y = scanner.nextFloat();
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    for (int i = 0; i < traitCount; i++) {
                        descriptor[i] = scanner.nextInt();
                    }
                    keyPoints[pointIndex] = new Point(x, y, descriptor);
                    pointIndex++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } finally {
            if(scanner != null)
                scanner.close();
        }
        return keyPoints;
    }
}
