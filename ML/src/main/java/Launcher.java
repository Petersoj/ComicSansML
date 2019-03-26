import edu.usu.hackathon2019.DataGenerators.JavaFXSampleFontGenerator;
import edu.usu.hackathon2019.charclassifier.CharClassifierNetwork;
import edu.usu.hackathon2019.charclassifier.CharacterClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontClassifierNetwork;
import edu.usu.hackathon2019.fontclassifier.FontManager;
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

//    public static INDArrayDataSetIterator getSamples(int sampleCount, int batchSize) {
//        ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<>();
//        JavaFXSampleGenerator sampleGenerator = new JavaFXSampleGenerator();
//        sampleGenerator.setup(sampleCount);
//        for (int i = 0; i < sampleCount * batchSize; i++) {
//            list.add(sampleGenerator.getNextSample());
//        }
//        return new INDArrayDataSetIterator(list, batchSize);
//    }

    public static void start() {
//        System.out.println("Creating network");
//        MultiLayerNetwork network = new MultiLayerNetwork(createConfig());
//        network.init();
//        network.setListeners(new TimeIterationListener(1));
//
//        System.out.println("Generating training data");
//        INDArrayDataSetIterator trainData = getSamples(20, CharacterClassifierConfig.batchSize);
//
//        System.out.println("Generating test data");
//        INDArrayDataSetIterator testData = getSamples(20, CharacterClassifierConfig.batchSize);
//
//        System.out.println("Training the network");
//        network.fit(trainData, CharacterClassifierConfig.epochs);
//
//        System.out.println("Evaluating network");

//        CharClassifierNetwork network = new CharClassifierNetwork();
//        if (!network.load("target/classes/models/CharacterIdentifier.network")) {
//            long start = System.currentTimeMillis();
//            System.out.println("training network");
//            network.fit(2080, CharacterClassifierConfig.batchSize, CharacterClassifierConfig.epochs);
//            long elapse = System.currentTimeMillis() - start;
//            System.out.println("took:" + elapse + " ms");
//            System.out.println("Training time: " + TimeUnit.MILLISECONDS.toHours(elapse) + " Hours " + TimeUnit.MILLISECONDS.toMinutes(elapse) + " minutes");
//            network.save("CharacterIdentifier.network");
//        }
//        JavaFXSampleFontGenerator gen = new JavaFXSampleFontGenerator();
//        gen.setup(100);
//        gen.getNextElement();
//        System.exit(0);
        FontManager.init();
        long start = System.currentTimeMillis();
        FontClassifierNetwork network;
        network = new FontClassifierNetwork();
        System.out.println("training network");
        if (!network.load("target/classes/models/fontIdentifierForChar_a_.network")) {
            JavaFXSampleFontGenerator generator = new JavaFXSampleFontGenerator();
            generator.setup(10);
            ArrayList<Pair<INDArray, INDArray>> samples = generator.getSamples('a', 15);
//        double accuracy = 0;
//        while (true) {
            network.fit(samples, FontClassifierConfig.batchSize, FontClassifierConfig.epochs);


            long elapse = System.currentTimeMillis() - start;
            System.out.println("took:" + elapse + " ms");
            System.out.println("Training time: " + TimeUnit.MILLISECONDS.toHours(elapse) + " Hours " + TimeUnit.MILLISECONDS.toMinutes(elapse) + " minutes");


            network.save("fontIdentifierForChar_a_.network");
            System.out.println("Evaluating network with training data");
            Evaluation eval = network.evaluate(samples, FontClassifierConfig.batchSize);
            System.out.println("learning rate: " + FontClassifierConfig.learningRate + " got accuracy of: " + eval.accuracy());

            System.out.println(eval.stats(false, true));
//            if (eval.accuracy() > accuracy) {
//                FontClassifierConfig.learningRate /= 7;
//            } else {
//                FontClassifierConfig.learningRate /= 0.7;
//            }
//        }
        }
        System.out.println("Evaluating network with new data");
        Evaluation eval = network.evaluate(40, FontClassifierConfig.batchSize, 'a');
        System.out.println(eval.stats(false, true));
        System.exit(0);
//        System.exit(0);
////        System.out.println("Evaluating network");
////        Evaluation eval = network.evaluate(40000, CharacterClassifierConfig.batchSize);
////        System.out.println(eval.stats(false, true));
////        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        start();
    }
}
