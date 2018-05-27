package com.company.GUI;

import com.company.PointPair;
import org.opencv.core.*;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class ImagesView {
    Image image1, image2;
    Mat mat1, mat2, mat3;
    KeyPoint[] keyPoints1, keyPoints2;
    DMatch[] matches;

    public ImagesView(Image image1, Image image2, PointPair[] pairs, float keyPointDiameter) {
        this.image1 = image1;
        this.image2 = image2;
        for (int i = 0; i < pairs.length; i++){
            int dist1 = 0, dist2 = 0;
            for (int j = 0; j < image1.keyPoints.length; j++)
                if (image1.keyPoints[j] == pairs[i].getKey())
                    dist1 = j - i;
            for (int j = 0; j < image2.keyPoints.length; j++)
                if (image2.keyPoints[j] == pairs[i].getValue())
                    dist2 = j - i;
            matches[i] = new DMatch(dist1, dist2, 0);
        }
        for (int i = 0; i < image1.keyPoints.length; i++)
            keyPoints1[i] = new KeyPoint((float) image1.keyPoints[i].x, (float) image1.keyPoints[i].y, keyPointDiameter);
        for (int i = 0; i < image2.keyPoints.length; i++)
            keyPoints2[i] = new KeyPoint((float) image2.keyPoints[i].x, (float) image2.keyPoints[i].y, keyPointDiameter);
        createMats();
        matchPoints();
    }

    public void show() {
        HighGui.imshow("Image Recognition", mat3);
        HighGui.waitKey(0);
    }

    private void createMats() {
        this.mat1 = Imgcodecs.imread(image1.imagePath);
        this.mat2 = Imgcodecs.imread(image2.imagePath);
    }

    private void matchPoints() {
        MatOfKeyPoint matKey1 = new MatOfKeyPoint(keyPoints1);
        MatOfKeyPoint matKey2 = new MatOfKeyPoint(keyPoints1);
        MatOfDMatch matDMatch = new MatOfDMatch(matches);
        Features2d.drawMatches(mat1, matKey1, mat2, matKey2, matDMatch, mat3);
    }
}
