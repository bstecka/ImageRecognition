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
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Random;

public class ImageViewFx extends Application {

    SiftImage image1, image2;
    PointPair[] matches;

    private Parent createContent() {
        this.image1 = new SiftImage(Const.PATH + "ksiaz.png.haraff.sift");
        this.image2 = new SiftImage(Const.PATH + "ksiaz2.png.haraff.sift");
        //this.matches = image1.getKeyPointPairs(image2);
        this.matches = image1.getConsistentPairs(image2, 15, 1);
        StackPane root = new StackPane();
        root.setPrefSize(1290, 300);
        ImageView imageView1 = new ImageView(new Image("file:/Users/Piotr/Desktop/zdj/ksiaz.png/"));
        ImageView imageView2 = new ImageView(new Image("file:/Users/Piotr/Desktop/zdj/ksiaz2.png/"));
        root.getChildren().addAll(imageView1, imageView2);
        StackPane.setAlignment(imageView1, Pos.TOP_LEFT);
        StackPane.setAlignment(imageView2, Pos.TOP_RIGHT);
        StackPane processed = processMatches(root);
        return processed;
    }

    private javafx.scene.paint.Color randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return javafx.scene.paint.Color.rgb(r, g, b);
    }

    private StackPane processMatches(StackPane root) {
        for (int i = 0; i < matches.length; i++){
            if (matches[i] != null) {
                PointPair pair = matches[i];
                Line line = new Line();
                line.setStroke(randomColor());
                root.getChildren().add(line);
                line.setStartX(pair.getKey().x);
                line.setStartY(pair.getKey().y);
                line.setEndX(pair.getValue().x + 650);
                line.setEndY(pair.getValue().y);
                StackPane.setAlignment(line, Pos.TOP_LEFT);
                line.setTranslateX(pair.getKey().x);
                line.setTranslateY(pair.getKey().y);
            }
        }
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        Scale scale = new Scale(0.8, 0.8);
        scale.setPivotX(0);
        scale.setPivotY(0);
        primaryStage.setTitle("Pary punktów kluczowych");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
