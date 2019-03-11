package edu.usu.hackathon2019;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Tester extends Application {

    ImageView[] imageViews;
    JavaFXSampleGenerator gen = new JavaFXSampleGenerator();

    final int width = 10;
    final int height = 10;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane p = new Pane();
        gen.setup(1000);
        imageViews = new ImageView[width * height];
        GridPane images = new GridPane();

        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView();
            images.add(imageViews[i], i % width, i / height);
        }

        setImageViews();

        p.getChildren().add(images);

        Button btn = new Button("next");
        btn.setOnMouseClicked((event -> {
            setImageViews();
        }));

        p.getChildren().add(btn);

        Scene scene = new Scene(p, 1000, 800);

        btn.layoutXProperty().bind(scene.widthProperty().subtract(btn.widthProperty()).divide(2));
        btn.layoutYProperty().bind(scene.heightProperty().subtract(btn.heightProperty()).subtract(2));

        images.layoutXProperty().bind(scene.widthProperty().subtract(images.widthProperty()).divide(2));
        images.layoutYProperty().bind(scene.heightProperty().subtract(images.heightProperty()).divide(2));

        primaryStage.setScene(scene);
        primaryStage.setTitle("test");
        primaryStage.show();
    }

    private void setImageViews() {
        for (ImageView imv : imageViews) {
            imv.setImage(gen.getNextElement().getKey());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
