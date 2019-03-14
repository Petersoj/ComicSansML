package edu.usu.hackathon2019.charindexing;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.awt.Font;
import java.io.File;

public class CharIndexingNN {

    public static void main(String[] args) {
        File networkSaveFile = null;
        CharIndexingNN charIndexingNN = new CharIndexingNN(networkSaveFile);

        StringRasterDataSet stringRasterDataSet = new StringRasterDataSet("Ostensibly",
                new Font("Brush Script MT", Font.ITALIC, 64));
        stringRasterDataSet.createGlyphsOutlineRaster();

        CharIndexingViewer charIndexingViewer = new CharIndexingViewer();
        charIndexingViewer.setStringRasterDataSet(stringRasterDataSet);
        charIndexingViewer.show();
    }

    private File networkSaveFile;
    private MultiLayerNetwork network;

    /**
     * The idea behind this char indexing is that we take in a picture of a word and create a CNN
     * that can differentiate characters in the word. This is so we can index the letters and then
     * run another CNN classification on the letter to determine what letter it is.
     * Then we run that through another CNN which can then recognize the font face of the character.
     * For now, forget about character indexing as it is pretty difficult to achieve this. What is
     * important right now is the character classification regardless of what font it is in and then,
     * of course, the font classification network.
     */
    public CharIndexingNN(File networkSaveFile) {
        this.networkSaveFile = networkSaveFile;
    }

    public void configureNetwork() {
        NeuralNetConfiguration.Builder neuralNetConfigurationBuilder = new NeuralNetConfiguration.Builder();
    }

    public void trainNetwork() {

    }

    public void loadNetwork(File networkSaveFile) {

    }
}
