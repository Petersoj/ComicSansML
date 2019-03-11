package edu.usu.hackathon2019;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class SampleDataGenerator implements Iterable {

    protected Random rand = new Random();

    protected abstract void setup(int sampleCount);

    protected abstract void cleanup();

    protected abstract void destroy();

    @NotNull
    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }

    public char getRandomCharacter() {
        return Config.characters[(int) (Config.characters.length * rand.nextDouble())];
    }
}
