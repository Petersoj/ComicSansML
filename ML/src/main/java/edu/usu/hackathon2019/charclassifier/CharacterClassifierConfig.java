package edu.usu.hackathon2019.charclassifier;

import java.util.HashMap;

public class CharacterClassifierConfig {

    public static final int imageWidth = 35;
    public static final int imageHeight = 25;

    public static final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();

    public static final HashMap<Character, Integer> lookup = setup();

    public static final int nueralNetworkSeed = 505;
    public static final int channels = 3;


    public static final int batchSize = 1;
    public static final int epochs = 2100;
    public static final double l2 = 0.0000085; //0.00001


    private static HashMap<Character, Integer> setup() {
        HashMap<Character, Integer> result = new HashMap<>();
        for (int i = 0; i < characters.length; i++) {
            result.put(characters[i], i);
        }
        return result;
    }
}
