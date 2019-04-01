package edu.usu.hackathon2019.bettermnist;

import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.Font;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;

public class CharDataSet implements Comparable<CharDataSet> {

    // Don't change this order! The order corresponds with the output layer of the network!
    public static final char[] CHARACTERS = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "1234567890")
            .toCharArray();
    private static char[] UPPER_CASE_CHARACTERS;
    private static char[] LOWER_CASE_CHARACTERS;
    private static char[] NUMBERIC_CHARACTERS;

    public static final char[] getUpperCaseCharacters() {
        if (UPPER_CASE_CHARACTERS == null) {
            UPPER_CASE_CHARACTERS = Arrays.copyOfRange(CHARACTERS, 0, 26);
        }
        return UPPER_CASE_CHARACTERS;
    }

    public static final char[] getLowerCaseCharacters() {
        if (LOWER_CASE_CHARACTERS == null) {
            LOWER_CASE_CHARACTERS = Arrays.copyOfRange(CHARACTERS, 26, 52);
        }
        return LOWER_CASE_CHARACTERS;
    }

    public static final char[] getNumbericCharacters() {
        if (NUMBERIC_CHARACTERS == null) {
            NUMBERIC_CHARACTERS = Arrays.copyOfRange(CHARACTERS, 52, 62);
        }
        return NUMBERIC_CHARACTERS;
    }

    private char character;
    private Font font;
    private BufferedImage raster;
    private Shape shape;

    private INDArray features;
    private INDArray labels;
    private double probability;

    /**
     * Represents a POJO for a character, its raster, output neuron labels, etc.
     *
     * @param character the char representing this object.
     */
    public CharDataSet(char character) {
        this.character = character;
    }

    /**
     * Create the features INDArray with the desired network input tensor.
     */
    public void assignFeatures() {
        if (raster.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            throw new IllegalStateException("CharDataSet raster must be a grayscale!");
        }
        int rows = raster.getHeight();
        int cols = raster.getWidth();

        byte[] imageBytes = ((DataBufferByte) raster.getRaster().getDataBuffer()).getData();
        double[][][][] imageDoubles = new double[1][1][rows][cols];

        for (int i = 0; i < imageBytes.length; i++) {
            imageDoubles[0][0][i % rows][i % cols] = ((imageBytes[i] & 0xFF) / 255d);
        }
        features = Nd4j.create(imageDoubles);
    }

    /**
     * Create the labels INDArray with the desired network output tensor.
     */
    public void assignLabels() {
        double[] doubleLabels = new double[CHARACTERS.length];
        for (int i = 0; i < doubleLabels.length; i++) {
            doubleLabels[i] = CHARACTERS[i] == character ? 1d : 0d;
        }
        labels = Nd4j.create(doubleLabels); // Create 1D INDArray with value 1d at this char index and 0d for the rest.
    }

    /**
     * Set this CharDataSet's data from output labels
     */
    public void labelAssign() {
        // TODO this
    }

    @Override
    public int compareTo(@NotNull CharDataSet o) { // Sort by probability value given from probability output matrix
        return Double.compare(probability, o.getProbability());
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public BufferedImage getRaster() {
        return raster;
    }

    public void setRaster(BufferedImage raster) {
        this.raster = raster;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public INDArray getFeatures() {
        return features;
    }

    public void setFeatures(INDArray features) {
        this.features = features;
    }

    public INDArray getLabels() {
        return labels;
    }

    public void setLabels(INDArray labels) {
        this.labels = labels;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
