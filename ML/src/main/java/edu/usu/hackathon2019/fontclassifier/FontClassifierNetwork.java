package edu.usu.hackathon2019.fontclassifier;

import edu.usu.hackathon2019.DataGenerators.JavaFXSampleFontGenerator;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.TruncatedNormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.weightnoise.WeightNoise;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.NesterovsUpdater;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FontClassifierNetwork {

    private MultiLayerNetwork network;

    public FontClassifierNetwork() {
        network = new MultiLayerNetwork(createConfig());
        network.init();
//        network.setListeners(new ScoreIterationListener(1));
    }

    public void fit(int sampleSize, int batchSize, int epochs, char c) {
        fit(new JavaFXSampleFontGenerator(), sampleSize, batchSize, epochs, c);
    }

    public void fit(JavaFXSampleFontGenerator generator, int sampleSize, int batchSize, int epochs, char c) {
        ArrayList<Pair<INDArray, INDArray>> list = generator.getSamples(c, sampleSize);
        generator.destroy();
        fit(list, batchSize, epochs);
    }

    public void fit(ArrayList<Pair<INDArray, INDArray>> data, int batchSize, int epochs) {
        fit(new INDArrayDataSetIterator(data, batchSize), epochs);
    }

    public void fit(INDArrayDataSetIterator data, int epochs) {
//        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
//        StatsStorage statsStorage = new InMemoryStatsStorage();

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
//        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains
//        network.setListeners(new StatsListener(statsStorage, 25000));
//        network.setListeners(new EvaluativeListener(data, 1));
        network.fit(data, epochs);
    }

    public Evaluation evaluate(int sampleSize, int batchSize, char c) {
        return evaluate(new JavaFXSampleFontGenerator(), sampleSize, batchSize, c);
    }

    public Evaluation evaluate(JavaFXSampleFontGenerator generator, int sampleSize, int batchSize, char c) {
        ArrayList<Pair<INDArray, INDArray>> list = generator.getSamples(c, sampleSize);
        generator.destroy();
        return evaluate(list, batchSize);
    }

    public Evaluation evaluate(ArrayList<Pair<INDArray, INDArray>> data, int batchSize) {
        return evaluate(new INDArrayDataSetIterator(data, batchSize));
    }

    public INDArray output(INDArray input) {
        return network.output(input);
    }

    public Evaluation evaluate(DataSetIterator data) {
        return network.evaluate(data);
    }

    public boolean save(String filePath) {
        try {
            File file = new File(filePath);
            network.save(file, true);
            return true;
        } catch (IOException e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean load(String filePath) {
        try {
            File file = new File(filePath);
            network = MultiLayerNetwork.load(file, true);
            return true;
        } catch (IOException e) {
            System.err.println(e);
        }
        return false;
    }

    public static MultiLayerConfiguration createConfig() {
        return new NeuralNetConfiguration.Builder()
                .seed(FontClassifierConfig.nueralNetworkSeed)
                .l2(FontClassifierConfig.l2)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
//                .weightNoise(new WeightNoise(new TruncatedNormalDistribution(6 * FontClassifierConfig.l2, FontClassifierConfig.learningRate / 10), true))
//                .updater(
//                        new NesterovsUpdater(new Nesterovs(FontClassifierConfig.learningRate, 0.1)).getConfig())
                .updater(new Adam(FontClassifierConfig.learningRate))
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(0, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                        .name("CNN 1")
                        .nIn(FontClassifierConfig.channels)
                        .nOut(32)
                        .biasInit(0)
                        .build()
                )
                .layer(1, maxPool("subsampling layer 1", new int[]{2,2}))
                .layer(2, conv3x3("CNN 2", 64, 0))
                .layer(3, conv3x3("CNN 3", 64, 1))
                .layer(4, maxPool("subsampling layer 2", new int[]{2,2}))
                .layer(5, new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .nOut(512)
                        .dropOut(0.5)
                        .build()
                )
                .layer(6, new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                        .nOut(FontManager.getFonts().length)
                        .activation(Activation.SOFTMAX)
                        .build()
                )
                .backprop(true)
                .pretrain(false)
                .backpropType(BackpropType.Standard)
                .setInputType(InputType.convolutional(FontClassifierConfig.imageHeight, FontClassifierConfig.imageWidth, FontClassifierConfig.channels))
                .build();
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).activation(Activation.RELU).name(name).nOut(out).biasInit(bias).build();
    }


    private static SubsamplingLayer maxPool(String name, int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }
}
