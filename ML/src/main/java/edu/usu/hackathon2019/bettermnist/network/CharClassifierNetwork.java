package edu.usu.hackathon2019.bettermnist.network;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import edu.usu.hackathon2019.bettermnist.generator.CharDataSetGenerator;
import edu.usu.hackathon2019.bettermnist.generator.CharRasterGenerator;
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
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.linalg.schedule.MapSchedule;
import org.nd4j.linalg.schedule.ScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CharClassifierNetwork {

    private CharDataSetGenerator charDataSetGenerator;
    private Logger logger;
    private MultiLayerConfiguration configuration;
    private MultiLayerNetwork network;

    public CharClassifierNetwork(CharDataSetGenerator charDataSetGenerator) {
        this.charDataSetGenerator = charDataSetGenerator;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void loadNetwork(File file) {
        try {
            network = MultiLayerNetwork.load(file, true);
        } catch (IOException e) {
            logger.error("Error loading network file!", e);
        }
    }

    public void saveNetwork(File file) {
        try {
            network.save(file);
        } catch (IOException e) {
            logger.error("Error saving network file!", e);
        }
    }

    public void createNewNetwork() {
        // Create NN hyperparameters and layers
        NeuralNetConfiguration.Builder configurationBuilder = this.createConfigurationBuilder();
        MultiLayerConfiguration.Builder multiLayerConfigurationBuilder = this.createLayerBuilder(configurationBuilder);

        // Instantiate NN and init
        configuration = multiLayerConfigurationBuilder.build();
        network = new MultiLayerNetwork(configuration);
        network.init();
    }

    private NeuralNetConfiguration.Builder createConfigurationBuilder() {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();

        builder.seed(121L);
        builder.l2(0.0005);
        builder.weightInit(WeightInit.XAVIER);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        builder.activation(Activation.RELU);

        HashMap<Integer, Double> learningRateSchedule = new HashMap<>();
        learningRateSchedule.put(0, 0.06);
        learningRateSchedule.put(200, 0.045);
        learningRateSchedule.put(600, 0.03);
//        learningRateSchedule.put(800, 0.006);
//        learningRateSchedule.put(1500, 0.001);
        builder.updater(new Adam(new MapSchedule(ScheduleType.ITERATION, learningRateSchedule)));

        return builder;
    }

    private MultiLayerConfiguration.Builder createLayerBuilder(NeuralNetConfiguration.Builder configurationBuilder) {
        NeuralNetConfiguration.ListBuilder listBuilder = configurationBuilder.list();

        ConvolutionLayer inputConvolutionLayer = new ConvolutionLayer.Builder()
                .name("input_convolution_layer")
                .nIn(1)
                .nOut(25)
                .kernelSize(5, 5)
                .stride(1, 1)
                .build();

        SubsamplingLayer inputSubsamplingLayer = new SubsamplingLayer.Builder()
                .name("input_subsampling_layer")
                .poolingType(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .build();

        ConvolutionLayer secondConvolutionLayer = new ConvolutionLayer.Builder()
                .name("second_convolution_layer")
                .nOut(50)
                .kernelSize(5, 5)
                .stride(1, 1)
                .activation(Activation.IDENTITY)
                .build();

        SubsamplingLayer secondSubsamplingLayer = new SubsamplingLayer.Builder()
                .name("second_subsampling_layer")
                .poolingType(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .build();

        DenseLayer denseLayer = new DenseLayer.Builder()
                .name("dense_layer")
                .nOut(250)
                .activation(Activation.RELU)
                .build();

        OutputLayer outputLayer = new OutputLayer.Builder()
                .name("output_layer")
                .nOut(CharDataSet.CHARACTERS.length)
                .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .build();

        listBuilder.layer(inputConvolutionLayer);
        listBuilder.layer(inputSubsamplingLayer);
        listBuilder.layer(secondConvolutionLayer);
        listBuilder.layer(secondSubsamplingLayer);
        listBuilder.layer(denseLayer);
        listBuilder.layer(outputLayer);

        CharRasterGenerator charRasterGenerator = charDataSetGenerator.getCharRasterGenerator();
        listBuilder.setInputType(InputType.convolutionalFlat(
                charRasterGenerator.getRasterHeight(), charRasterGenerator.getRasterWidth(), 1));
        listBuilder.pretrain(false);
        listBuilder.backprop(true);
        listBuilder.backpropType(BackpropType.Standard);

        return listBuilder;
    }

    public void train() {
        if (network == null) {
            throw new NullPointerException("Network must be loaded/created before training!");
        }

        int epochs = 3;
        int numberOfSamples = 10_000;
        int batchSize = 5;

        logger.info("Generating " + numberOfSamples + " samples.");
        CharDataSet[] charDataSets = charDataSetGenerator.generate(numberOfSamples, true, true);

        INDArrayDataSetIterator trainDataSetIterator = this.createINDArrayDataSetIterator(charDataSets, batchSize);
        this.addTrainingListeners(epochs, numberOfSamples, batchSize);

        logger.info("Training network with " + numberOfSamples + " samples with batch size of " + batchSize + ".");
        network.fit(trainDataSetIterator, epochs); // Trains model
    }

    public Evaluation test() {
        if (network == null) {
            throw new NullPointerException("Network must be loaded/created before training!");
        }

        int numberOfSamples = 1_000;
        int batchSize = 1;

        logger.info("Generating " + numberOfSamples + " samples.");
        CharDataSet[] charDataSets = charDataSetGenerator.generate(numberOfSamples, true, true);

        INDArrayDataSetIterator testDataSetIterator = this.createINDArrayDataSetIterator(charDataSets, batchSize);

        logger.info("Evaluating " + numberOfSamples + " samples with batch size of " + batchSize + ".");
        Evaluation evaluation = network.evaluate(testDataSetIterator);

        logger.info(evaluation.stats(false, true));

        return evaluation;
    }

    private INDArrayDataSetIterator createINDArrayDataSetIterator(CharDataSet[] charDataSets, int batchSize) {
        ArrayList<Pair<INDArray, INDArray>> featureLabelPairs = new ArrayList<>(charDataSets.length);

        for (CharDataSet charDataSet : charDataSets) {
            featureLabelPairs.add(new Pair<>(charDataSet.getFeatures(), charDataSet.getLabels()));
        }

        return new INDArrayDataSetIterator(featureLabelPairs, batchSize);
    }

    private void addTrainingListeners(int epochs, int numberOfSamples, int batchSize) {
        network.addListeners(new ScoreIterationListener(1));

        // UIServer Listener
//        UIServer uiServer = UIServer.getInstance();
//        InMemoryStatsStorage inMemoryStatsStorage = new InMemoryStatsStorage();
//        uiServer.attach(inMemoryStatsStorage);
//        network.addListeners(new StatsListener(inMemoryStatsStorage, 10));
    }

    public CharDataSet predict(CharDataSet charDataSet) {
        INDArray outputLabels = network.output(charDataSet.getFeatures());
        return matchMostProbable(charDataSet.getFeatures(), outputLabels);
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
        int maxValueIndex = labels.argMax(1).getInt(0); // Get the highest number index in INDArray
        CharDataSet charDataSet = new CharDataSet(CharDataSet.CHARACTERS[maxValueIndex]);
        charDataSet.setFeatures(features);
        charDataSet.setLabels(labels);
        charDataSet.assignLabels();
        return charDataSet;
    }

    public boolean doesExist() {
        return network != null;
    }
}
