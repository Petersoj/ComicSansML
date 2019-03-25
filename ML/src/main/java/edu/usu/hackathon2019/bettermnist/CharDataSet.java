package edu.usu.hackathon2019.bettermnist;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.Font;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class CharDataSet {

    // Don't change this order! The order corresponds with the output layer of the network!
    public static final char[] CHARACTERS = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "1234567890")
            .toCharArray();

    private char character;
    private Font font;
    private BufferedImage raster;
    private Shape shape;

    private INDArray features;
    private INDArray labels;

    public CharDataSet(char character) {
        this.character = character;
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
}