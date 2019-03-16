package edu.usu.hackathon2019.DataGenerators;

import edu.usu.hackathon2019.charclassifier.CharacterClassifierConfig;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import java.util.ArrayList;
import java.util.Random;

public abstract class SampleDataGenerator {

    protected Random rand = new Random();

    public abstract void setup(int sampleCount);

    public abstract void cleanup();

    public abstract void destroy();


    public abstract Pair<INDArray, INDArray> getNextSample();

    public abstract Pair<INDArray, INDArray> getNextSample(char character);

    public ArrayList<Pair<INDArray, INDArray>> getSamples(int sampleSize) {
        ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<>();
        setup(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            list.add(getNextSample());
        }
        return list;
    }

    public char getRandomCharacter() {
        return CharacterClassifierConfig.characters[(int) (CharacterClassifierConfig.characters.length * rand.nextDouble())];
    }
}
