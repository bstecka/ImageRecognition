package com.company;

import com.company.GUI.SiftImage;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("start");
        SiftImage image1 = new SiftImage(Const.PATH_SIFT + "ksiaz.png.haraff.sift");
        System.out.println(image1);
        SiftImage image2 = new SiftImage(Const.PATH_SIFT + "ksiaz2.png.haraff.sift");
        System.out.println(image2);
        PointPair[] matches = image1.getKeyPointPairs(image2);
        System.out.println(Arrays.toString(matches));
        PointPair[] consistentPairs = image1.getConsistentPairs(image2, 4, 2);
    }
}
