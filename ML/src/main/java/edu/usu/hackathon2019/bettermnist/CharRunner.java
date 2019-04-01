package edu.usu.hackathon2019.bettermnist;

import edu.usu.hackathon2019.bettermnist.generator.CharDataSetGenerator;
import edu.usu.hackathon2019.bettermnist.viewer.CharRasterViewer;

import javax.imageio.ImageIO;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * The objective of this Neural Network Deep Learning and Generation Application is to create a CNN
 * (Convolutional Neural Network) that is capable of identifying and classifying characters regardless of what font or
 * style that are in. Some fonts have extra detail and features that can cause other trained models to return
 * false positives in classification. The idea here is that we can perpetually generate thousands of character rasters
 * of different font faces and then apply raster warping/filtering to more accurately model a real world situation
 * in which the Network is given a slightly warped picture of a character or perhaps even a picture of something
 * handwritten.
 * Now there can potentially be some issues with using a single CNN and attempting to classify thousands of different
 * fonts. In the future, we may train multiple CNNs against specific types of fonts (serifs, sans serifs, monospaced, etc.)
 * to circumvent this issue.
 * When giving these trained models a char image, make sure that the whole raster is taken up by the image (e.g. scale
 * the char up to fill the whole raster) because that is what the network is currently trained to work with.
 */
public class CharRunner {

    public static void main(String[] args) {
        try {
            CharRunner charRunner = new CharRunner();
            charRunner.init();
            charRunner.testNeuralNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CharDataSetGenerator charDataSetGenerator;

    public CharRunner() {
        this.charDataSetGenerator = new CharDataSetGenerator();
    }

    public void init() throws FontFormatException, IOException {
        charDataSetGenerator.init();
    }

    public void trainNeuralNetwork() throws IOException {
        CharDataSet[] charDataSets = charDataSetGenerator.generate(42, false, false);
        int i = 0;
        for (CharDataSet charDataSet : charDataSets) {
            System.out.println(charDataSet.getCharacter());
            System.out.println(charDataSet.getFont().getName());
            ImageIO.write(charDataSet.getRaster(), "JPG", new File("/Users/jacob/Desktop/", "test-" + i++ +
                    " - " + charDataSet.getCharacter() + ".jpg"));
        }
    }

    public void testNeuralNetwork() {
        CharRasterViewer charRasterViewer = new CharRasterViewer(this);
        charRasterViewer.init();
        charRasterViewer.show();
    }

    public CharDataSetGenerator getCharDataSetGenerator() {
        return charDataSetGenerator;
    }
}
