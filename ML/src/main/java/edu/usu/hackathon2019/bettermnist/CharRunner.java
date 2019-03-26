package edu.usu.hackathon2019.bettermnist;

import edu.usu.hackathon2019.bettermnist.generator.CharDataSetGenerator;

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
 */
public class CharRunner {

    public static void main(String[] args) {
        try {

            CharRunner charRunner = new CharRunner();
            charRunner.generateCharDataSets();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CharRunner() {
    }

    public void generateCharDataSets() throws IOException, FontFormatException {
        CharDataSetGenerator charDataSetGenerator = new CharDataSetGenerator();
        charDataSetGenerator.init();

        CharDataSet[] charDataSets = charDataSetGenerator.generate(40);
        int i = 0;
        for (CharDataSet charDataSet : charDataSets) {
            System.out.println(charDataSet.getCharacter());
            System.out.println(charDataSet.getFont().getName());
            ImageIO.write(charDataSet.getRaster(), "PNG", new File("/Users/jacob/Desktop/", "test" + i++ + ".png"));
        }
    }

    public void trainNeuralNetwork() {

    }

    public void testNeuralNetwork(boolean displayCharRasterViewer) {

    }
}
