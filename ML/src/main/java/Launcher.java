import ML.SampleDate.Config;
import ML.SampleDate.JavaFXSampleGenerator;
import javafx.application.Application;
import javafx.stage.Stage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.TimeIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;

import java.util.ArrayList;
import java.util.List;

public class Launcher extends Application {

    public static MultiLayerConfiguration createConfig() {
        return new NeuralNetConfiguration.Builder()
                .seed(Config.nueralNetworkSeed)
                .l2(0.05)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.ADAM)
                .list()
                    .layer(0, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                            .name("CNN 1")
                            .nIn(Config.channels)
                            .nOut(32)
                            .biasInit(0)
                            .build()
                    )
                    .layer(1, maxPool("subsampling layer 1", new int[]{2,2}))
                    .layer(2, conv3x3("CNN 2", 64, 0))
                    .layer(3, conv3x3("CNN 3", 64, 1))
                    .layer(4, maxPool("subsampling layer 2", new int[]{2,2}))
                    .layer(5, new DenseLayer.Builder()
                            .activation(Activation.RRELU)
                            .nOut(16)
                            .dropOut(0.5)
                            .build()
                    )
                    .layer(6, new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                            .nOut(Config.characters.length)
                            .activation(Activation.SOFTMAX)
                            .build()
                    )
                .backpropType(BackpropType.Standard)
                .pretrain(false)
                .setInputType(InputType.convolutional(Config.imageHeight, Config.imageWidth, Config.channels))
                .build();
    }

    public static INDArrayDataSetIterator getSamples(int sampleCount, int batchSize) {
        ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<>();
        JavaFXSampleGenerator sampleGenerator = new JavaFXSampleGenerator();
        sampleGenerator.setup(sampleCount);
        for (int i = 0; i < sampleCount; i++) {
            list.add(sampleGenerator.getNextSample());
        }
        return new INDArrayDataSetIterator(list, batchSize);
    }

    public static void start() {
        System.out.println("Creating network");
        MultiLayerNetwork network = new MultiLayerNetwork(createConfig());
        network.init();
        network.setListeners(new TimeIterationListener(1));

        System.out.println("Generating training data");
        INDArrayDataSetIterator trainData = getSamples(200, Config.batchSize);

        System.out.println("Generating test data");
        INDArrayDataSetIterator testData = getSamples(400, Config.batchSize);

        System.out.println("Training the network");
        network.fit(trainData, Config.epochs);

        System.out.println("Evaluating network");
        Evaluation eval = network.evaluate(testData);
        System.out.println(eval.stats(false, true));
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {0,0}).activation(Activation.RRELU).name(name).nOut(out).biasInit(bias).build();
    }


    private static SubsamplingLayer maxPool(String name, int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{3,3}).name(name).build();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        start();
    }
}
