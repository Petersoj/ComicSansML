import edu.usu.hackathon2019.DataGenerators.JavaFXSampleFontGenerator;
import edu.usu.hackathon2019.charclassifier.CharClassifierNetwork;
import edu.usu.hackathon2019.charclassifier.CharacterClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontClassifierNetwork;
import edu.usu.hackathon2019.fontclassifier.FontManager;
import edu.usu.hackathon2019.server.NetworkManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.TimeIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Launcher extends Application {

    public static void start() {

        FontManager.init();
        int sampleCountPerFont = 15;
        int evaluationCountPerFont = 30;
        int lastupdate = 0;
        double lastAccuracy;
        double overfitAccuracy = 2.0d / FontManager.getFonts().length;
        ArrayList<Pair<INDArray, INDArray>> evaluationSet;
        ArrayList<Pair<INDArray, INDArray>> trainingSet;
        FontClassifierNetwork network;
        String past_save = null;
        for (char character: "tuvwxyz1234567890".toCharArray()) {
            System.out.println("Starting to train model \"" + character + "\"");
            lastAccuracy = 0;
            lastupdate = 0;
            JavaFXSampleFontGenerator generator = new JavaFXSampleFontGenerator();
            generator.setup(evaluationCountPerFont);
            evaluationSet = generator.getSamples(character, evaluationCountPerFont);
            trainingSet = generator.getSamples(character, sampleCountPerFont);
            network = new FontClassifierNetwork();
            for (int i = 0; i < 10; i++) {
                network.fit(trainingSet, FontClassifierConfig.batchSize, 10);
                Evaluation eval = network.evaluate(evaluationSet, FontClassifierConfig.batchSize);
                System.out.println("Finished \"" + character + "\" batch: " + i + " with acc: " + eval.accuracy());
                if (eval.accuracy() <= overfitAccuracy) {
                    System.out.println("breaking out of \"" + character + "\" due to overfitting");
                    break;
                }
                if (lastAccuracy < eval.accuracy()) {
                    System.out.println("saving " + character + " it: " + i);
                    lastAccuracy = eval.accuracy();
                    past_save = "fontIdentifierForChar_" + character + "_iter_ " + i + "_acc_" + lastAccuracy + ".network";
                    network.save(past_save);
                }
            }
        }
        Evaluation eval = NetworkManager.manager.eval('a', 40);
        System.out.println(eval.stats(false, true));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        start();
    }
}
