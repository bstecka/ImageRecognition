package com.company.GUI;

import com.company.Const;
import com.company.PointPair;
import org.opencv.core.*;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class ImagesView {
    SiftImage image1, image2;
    Mat mat1, mat2, mat3;
    KeyPoint[] keyPoints1, keyPoints2;
    DMatch[] matches;

    public ImagesView(String imagePath1, String imagePath2, PointPair[] pairs, float keyPointDiameter) {
        this.image1 = new SiftImage(imagePath1);
        this.image2 = new SiftImage(imagePath2);
        this.keyPoints1 = new KeyPoint[image1.keyPoints.length];
        this.keyPoints2 = new KeyPoint[image2.keyPoints.length];
        this.matches = new DMatch[image1.keyPoints.length];
        for (int i = 0; i < pairs.length; i++){
            int dist1 = 0, dist2 = 0;
            for (int j = 0; j < image1.keyPoints.length; j++)
                if (image1.keyPoints[j] == pairs[i].getKey())
                    dist1 = j - i;
            for (int j = 0; j < image2.keyPoints.length; j++)
                if (image2.keyPoints[j] == pairs[i].getValue())
                    dist2 = j - i;
            this.matches[i] = new DMatch(dist1, dist2, 0);
        }
        for (int i = 0; i < image1.keyPoints.length; i++)
            keyPoints1[i] = new KeyPoint((float) image1.keyPoints[i].x, (float) image1.keyPoints[i].y, keyPointDiameter);
        for (int i = 0; i < image2.keyPoints.length; i++)
            keyPoints2[i] = new KeyPoint((float) image2.keyPoints[i].x, (float) image2.keyPoints[i].y, keyPointDiameter);
        createMats();
        match();
    }

    public ImagesView(SiftImage image1, SiftImage image2, PointPair[] pairs, float keyPointDiameter) {
        this.image1 = image1;
        this.image2 = image2;
        this.keyPoints1 = new KeyPoint[image1.keyPoints.length];
        this.keyPoints2 = new KeyPoint[image2.keyPoints.length];
        System.out.println(Arrays.toString(pairs));
        this.matches = new DMatch[image1.keyPoints.length];
        for (int i = 0; i < pairs.length; i++){
            int dist1 = 0, dist2 = 0;
            for (int j = 0; j < image1.keyPoints.length; j++) {
                if (pairs[i] != null){
                    if (image1.keyPoints[j] == pairs[i].getKey())
                        dist1 = j - i;
                }
            }
            for (int j = 0; j < image2.keyPoints.length; j++)
                if (pairs[i] != null){
                    if (image2.keyPoints[j] == pairs[i].getValue())
                        dist2 = j - i;
                }
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
        //HighGui.imshow("Image Recognition", mat3);
        //HighGui.waitKey(0);
    }

    private void createMats() {
        System.out.println(image1.imagePath);
        this.mat1 = Imgcodecs.imread(image1.imagePath);
        this.mat2 = Imgcodecs.imread(image2.imagePath);
    }

    public void match(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MatOfKeyPoint matKey1 = new MatOfKeyPoint(keyPoints1);
        MatOfKeyPoint matKey2 = new MatOfKeyPoint(keyPoints2);
        MatOfDMatch matDMatch = new MatOfDMatch(matches);
        Mat result = drawMatches(mat1, matKey1, mat2, matKey2, matDMatch, false);
    }

    private Mat drawMatches(Mat img1, MatOfKeyPoint key1, Mat img2, MatOfKeyPoint key2, MatOfDMatch matches, boolean imageOnly){
        Mat out = new Mat();
        Mat im1 = new Mat();
        Mat im2 = new Mat();
        Imgproc.cvtColor(img1, im1, Imgproc.COLOR_GRAY2RGB);
        Imgproc.cvtColor(img2, im2, Imgproc.COLOR_GRAY2RGB);
        if ( imageOnly){
            MatOfDMatch emptyMatch = new MatOfDMatch();
            MatOfKeyPoint emptyKey1 = new MatOfKeyPoint();
            MatOfKeyPoint emptyKey2 = new MatOfKeyPoint();
            Features2d.drawMatches(im1, emptyKey1, im2, emptyKey2, emptyMatch, out);
        } else {
            Features2d.drawMatches(im1, key1, im2, key2, matches, out);
        }
        Imgproc.cvtColor(out, out, Imgproc.COLOR_BGR2RGB);
        Imgproc.putText(out, "Frame", new Point(img1.width() / 2,30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0,255,255),3);
        Imgproc.putText(out, "Match", new Point(img1.width() + img2.width() / 2,30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255,0,0),3);
        return out;
    }

    private void matchPoints() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MatOfKeyPoint matKey1 = new MatOfKeyPoint(keyPoints1);
        MatOfKeyPoint matKey2 = new MatOfKeyPoint(keyPoints2);
        MatOfDMatch matDMatch = new MatOfDMatch(matches);
        Mat output = new Mat();
        //output = new Mat(matKey1.rows(), matKey1.cols(), CvType.CV_8U, Scalar.all(0));
        if (!output.empty())
            System.out.println("not empty");
        else
            System.out.println("empty");
        Features2d.drawMatches(mat1, matKey1, mat2, matKey2, matDMatch, output);
        //HighGui.imshow("Matches", output);
    }
}
