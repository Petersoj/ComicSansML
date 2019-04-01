package edu.usu.hackathon2019.bettermnist.network;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;

public class CharClassifierNetwork {

    private NeuralNetwork network;
    private MultiLayerConfiguration configuration;

    public CharClassifierNetwork() {
    }

    public void init() {
        NeuralNetConfiguration.Builder configurationBuilder = this.createConfigurationBuilder();
        MultiLayerConfiguration.Builder multiLayerConfigurationBuilder = this.buildLayers(configurationBuilder);

        configuration = multiLayerConfigurationBuilder.build();

        network = new MultiLayerNetwork(configuration);
        network.init();
    }

    private NeuralNetConfiguration.Builder createConfigurationBuilder() {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();

        builder.seed(100L);
        builder.weightInit(WeightInit.XAVIER);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

        return builder;
    }

    private MultiLayerConfiguration.Builder buildLayers(NeuralNetConfiguration.Builder configurationBuilder) {
        NeuralNetConfiguration.ListBuilder listBuilder = configurationBuilder.list();
        listBuilder.layer(new ConvolutionLayer.Builder()
                .kernelSize()
                .build());
//        layers.layer();
        return listBuilder;
    }

    /**
     * @param features
     * @param labels
     * @return a sorted (from most probable to least probable) ArrayList of CharDataSets reflecting a network output matrix
     */
    public ArrayList<CharDataSet> match(INDArray features, INDArray labels) {
        ArrayList<CharDataSet> charDataSets = new ArrayList<>();

        // Loop through all output neurons, create a CharDataSet with proper values, and add it to list
        for (int i = 0; i < labels.length(); i++) {

            char character = CharDataSet.CHARACTERS[i];
            CharDataSet charDataSet = new CharDataSet(character);

            charDataSet.setFeatures(features); // Set the reference to the features (network inputs)
            charDataSet.setLabels(labels); // Set the reference to the labels (network outputs)
            charDataSet.setProbability(labels.getDouble(i));

            charDataSets.add(charDataSet);
        }
        charDataSets.sort(CharDataSet::compareTo);
        return charDataSets;
    }

    /**
     * Creates a CharDataSet from the most probable label of the network output matrix (Tensors to POJO)
     *
     * @param features the corresponding input features to the output labels (optional)
     * @param labels   the output labels tensor
     * @return
     */
    public CharDataSet matchMostProbable(INDArray features, INDArray labels) {
        int maxValueIndex = (int) labels.argMax(0).getDouble(0); // Get the highest number index in INDArray
        CharDataSet charDataSet = new CharDataSet(CharDataSet.CHARACTERS[maxValueIndex]);
        charDataSet.setFeatures(features);
        charDataSet.setLabels(labels);
        charDataSet.assignLabels();
        return charDataSet;
    }

    /**
     * @see CharClassifierNetwork#matchMostProbable(INDArray, INDArray)
     */
    public CharDataSet matchMostProbable(INDArray labels) {
        return matchMostProbable(null, labels);
    }
}
