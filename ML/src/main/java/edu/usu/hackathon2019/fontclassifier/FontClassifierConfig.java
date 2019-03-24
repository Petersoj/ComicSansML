package edu.usu.hackathon2019.fontclassifier;

import java.util.HashMap;

public class FontClassifierConfig {

    public static final int imageWidth = 35;
    public static final int imageHeight = 25;

    public static final int nueralNetworkSeed = 505;
    public static final int channels = 3;


    public static final int batchSize = 1;
    public static final int epochs = 200;
    public static final double l2 = 0.000009; //0.00001
    public static final double learningRate = 0.000000009; //0.000005;

}
