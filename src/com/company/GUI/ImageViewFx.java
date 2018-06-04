package com.company.GUI;

import com.company.Const;
import com.company.PointPair;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class ImageViewFx extends Application {

    SiftImage image1, image2;
    Image img, img2;
    PointPair[] matches;
    double xTranslate = 0;

    private Parent createContent() {
        String fileName = "samochod";
        this.image1 = new SiftImage(Const.PATH + fileName + ".png.haraff.sift");
        this.image2 = new SiftImage(Const.PATH + fileName + "2.png.haraff.sift");
        //this.matches = image1.getKeyPointPairs(image2);
        this.matches = image1.getConsistentPairs(image2, 15, 2);
        AnchorPane root = new AnchorPane();
        this.img = new Image("file:/Users/Piotr/Desktop/zdj/" + fileName + ".png/");
        this.img2 = new Image("file:/Users/Piotr/Desktop/zdj/" + fileName + "2.png/");
        xTranslate = img.getWidth();
        ImageView imageView1 = new ImageView(img);
        ImageView imageView2 = new ImageView(img2);
        imageView2.setLayoutX(img.getWidth());
        root.getChildren().add(imageView1);
        root.getChildren().add(imageView2);
        AnchorPane processed = processMatches(root);
        ScrollPane sp = new ScrollPane();
        sp.setContent(processed);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        return sp;
    }

    private javafx.scene.paint.Color randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return javafx.scene.paint.Color.rgb(r, g, b);
    }

    private AnchorPane processMatches(AnchorPane root) {
        for (int i = 0; i < matches.length; i++){
            if (matches[i] != null) {
                PointPair pair = matches[i];
                Line line = new Line(pair.getKey().x, pair.getKey().y, pair.getValue().x + img.getWidth(), pair.getValue().y);
                line.setStroke(randomColor());
                root.getChildren().add(line);
            }
        }
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), Math.min(img.getWidth() + img2.getWidth(), 1000),
                Math.min(Math.max(img.getHeight(), img2.getHeight()), 680));
        primaryStage.setTitle("Pary punktów kluczowych");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
