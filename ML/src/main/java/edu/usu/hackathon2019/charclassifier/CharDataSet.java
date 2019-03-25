package edu.usu.hackathon2019.charclassifier;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.Font;
import java.awt.image.BufferedImage;

public class CharDataSet {

    public static final int CHAR_RASTER_WIDTH = 64;
    public static final int CHAR_RASTER_HEIGHT = 64;
    public static final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789".toCharArray();

    private char character;
    private Font charFont;
    private BufferedImage charRaster;

    private INDArray features;
    private INDArray labels;

    public CharDataSet() {

    }

}
