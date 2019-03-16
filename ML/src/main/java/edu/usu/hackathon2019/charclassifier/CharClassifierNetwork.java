package edu.usu.hackathon2019.charclassifier;

import edu.usu.hackathon2019.DataGenerators.JavaFXSampleGenerator;
import edu.usu.hackathon2019.DataGenerators.SampleDataGenerator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CharClassifierNetwork {

    private MultiLayerNetwork network;

    public CharClassifierNetwork() {
        network = new MultiLayerNetwork(createConfig());
    }

    public void fit(int sampleSize, int batchSize, int epochs) {
        fit(new JavaFXSampleGenerator(), sampleSize, batchSize, epochs);
    }

    public void fit(SampleDataGenerator generator, int sampleSize, int batchSize, int epochs) {
        ArrayList<Pair<INDArray, INDArray>> list = generator.getSamples(sampleSize);
        generator.destroy();
        fit(list, batchSize, epochs);
    }

    public void fit(ArrayList<Pair<INDArray, INDArray>> data, int batchSize, int epochs) {
        fit(new INDArrayDataSetIterator(data, batchSize), epochs);
    }

    public void fit(INDArrayDataSetIterator data, int epochs) {
        network.fit(data, epochs);
    }

    public Evaluation evaluate(int sampleSize, int batchSize) {
        return evaluate(new JavaFXSampleGenerator(), sampleSize, batchSize);
    }

    public Evaluation evaluate(SampleDataGenerator generator, int sampleSize, int batchSize) {
        ArrayList<Pair<INDArray, INDArray>> list = generator.getSamples(sampleSize);
        generator.destroy();
        return evaluate(list, batchSize);
    }

    public Evaluation evaluate(ArrayList<Pair<INDArray, INDArray>> data, int batchSize) {
        return evaluate(new INDArrayDataSetIterator(data, batchSize));
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
                .seed(CharacterClassifierConfig.nueralNetworkSeed)
                .l2(CharacterClassifierConfig.l2)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(0, new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
                        .name("CNN 1")
                        .nIn(CharacterClassifierConfig.channels)
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
                        .nOut(CharacterClassifierConfig.characters.length)
                        .activation(Activation.SOFTMAX)
                        .build()
                )
                .backprop(true)
                .pretrain(false)
                .backpropType(BackpropType.Standard)
                .setInputType(InputType.convolutional(CharacterClassifierConfig.imageHeight, CharacterClassifierConfig.imageWidth, CharacterClassifierConfig.channels))
                .build();
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {0,0}).activation(Activation.RELU).name(name).nOut(out).biasInit(bias).build();
    }


    private static SubsamplingLayer maxPool(String name, int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{3,3}).name(name).build();
    }

}
