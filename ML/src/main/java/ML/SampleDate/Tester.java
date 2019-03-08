package ML.SampleDate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Tester extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane p = new Pane();
        JavaFXSampleGenerator gen = new JavaFXSampleGenerator();
        gen.setup(1000);
        ImageView imv = new ImageView();
        imv.setImage(gen.getNextElement().getKey());
        p.getChildren().add(imv);

        Button btn = new Button("next");
        btn.setOnMouseClicked((event -> {
            imv.setImage(gen.getNextElement().getKey());
        }));

        p.getChildren().add(btn);

        Scene scene = new Scene(p, 1000, 800);

        btn.layoutXProperty().bind(scene.widthProperty().subtract(btn.widthProperty()).divide(2));
        btn.layoutYProperty().bind(scene.heightProperty().subtract(btn.heightProperty()).subtract(2));

        imv.layoutXProperty().bind(scene.widthProperty().subtract(imv.fitWidthProperty()).divide(2));
        imv.layoutYProperty().bind(scene.heightProperty().subtract(imv.fitHeightProperty()).divide(2));

        primaryStage.setScene(scene);
        primaryStage.setTitle("test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
