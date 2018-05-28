package com.company;

import com.company.GUI.ImageViewFx;
import com.company.GUI.ImagesView;
import com.company.GUI.SiftImage;
import org.opencv.core.Core;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("start");
        SiftImage image1 = new SiftImage(Const.PATH + "3.png.haraff.sift");
        System.out.println(image1);
        SiftImage image2 = new SiftImage(Const.PATH + "4.png.haraff.sift");
        System.out.println(image2);
        /*PointPair[] pairs = image1.getKeyPointPairs(image2);
        ImagesView view = new ImagesView(image1, image2, image1.getKeyPointPairs(image2), (float) 0.7);
        System.out.println(view);*/
        //view.show();
    }
}
