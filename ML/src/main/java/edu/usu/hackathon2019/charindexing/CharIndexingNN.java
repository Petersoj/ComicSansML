package edu.usu.hackathon2019.charindexing;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.awt.Font;
import java.io.File;

public class CharIndexingNN {

    public static void main(String[] args) {
//        File networkSaveFile = null;
//        CharIndexingNN charIndexingNN = new CharIndexingNN(networkSaveFile);

        StringRasterDataSet stringRasterDataSet = new StringRasterDataSet("Ostensibly",
                new Font("Brush Script MT", Font.ITALIC, 32));
        stringRasterDataSet.createGlyphsOutlineRaster();

        CharIndexingViewer charIndexingViewer = new CharIndexingViewer();
        charIndexingViewer.setStringRasterDataSet(stringRasterDataSet);
        charIndexingViewer.show();
    }

    private File networkSaveFile;
    private MultiLayerNetwork network;

    public CharIndexingNN(File networkSaveFile) {
        this.networkSaveFile = networkSaveFile;
    }

    public void configureNetwork() {
        NeuralNetConfiguration.Builder neuralNetConfigurationBuilder = new NeuralNetConfiguration.Builder();
//        neuralNetConfigurationBuilder.list()
//        ListDataSetIterator<StringRasterDataSet> stringRasterDataSetIterator = new ListDataSetIterator<>();
    }

    public void trainNetwork() {

    }

    public void loadNetwork(File networkSaveFile) {

    }
}
