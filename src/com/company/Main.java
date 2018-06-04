package com.company;

import com.company.GUI.ImageViewFx;
import com.company.GUI.ImagesView;
import com.company.GUI.SiftImage;
import org.opencv.core.Core;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("start");
        SiftImage image1 = new SiftImage(Const.PATH + "ksiaz.png.haraff.sift");
        System.out.println(image1);
        SiftImage image2 = new SiftImage(Const.PATH + "ksiaz2.png.haraff.sift");
        System.out.println(image2);
        PointPair[] matches = image1.getKeyPointPairs(image2);
        System.out.println(Arrays.toString(matches));
        PointPair[] consistentPairs = image1.getConsistentPairs(image2, 4, 2);
    }
}
