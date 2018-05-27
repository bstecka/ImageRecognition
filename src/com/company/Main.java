package com.company;

public class Main {

    public static void main(String[] args) {
        Point[] points = new SiftReader().getKeyPoints(Const.PATH + "3.png.haraff.sift");
    }
}
