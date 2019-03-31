package edu.usu.hackathon2019.DataGenerators;

import edu.usu.hackathon2019.fontclassifier.FontClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontManager;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.*;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class JavaFXSampleFontGenerator extends JavaFXSampleGenerator {

    protected static Font[] fonts;
    protected static EffectHelper[] effects;

    protected static SnapshotParameters snp = new SnapshotParameters();

    public void setup(int sampleCount) {
        rand = new Random();
        fonts = new Font[Font.getFamilies().size()];
        for (int i = 0; i < Font.getFamilies().size(); i++) {
            fonts[i] = Font.font(Font.getFamilies().get(i));
        }
        generateEffects();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void destroy() {
        fonts = null;
        System.gc();
    }

    public Pair<WritableImage, String> getNextElement(char c) {
        String[] fonts = FontManager.getFonts();
        return getNextElement(c, fonts[(int) (Math.random() * fonts.length)]);
    }

    public Pair<WritableImage, String> getNextElement(char c, String fontFamily) {
        String label = fontFamily;
        Text txt = new Text(((Character) c).toString());
        txt.setFont(Font.font(fontFamily));
        txt.setRotate(Math.random() * 20 - 10);
        distortText(txt);
        return new Pair<>(txt.snapshot(snp, new WritableImage(FontClassifierConfig.imageWidth, FontClassifierConfig.imageHeight)), label);
    }

    public Pair<INDArray, INDArray> getNextSample(char character) {
        String[] fonts = FontManager.getFonts();
        return getNextSample(character, fonts[ (int) (Math.random() * fonts.length)]);
    }

    public Pair<INDArray, INDArray> getNextSample(char character, String fontFamily) {
        Pair<WritableImage, String> sample = getNextElement(character, fontFamily);

        WritableImage image = sample.getKey();
        PixelReader pr = image.getPixelReader();

        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        double[][][][] doubleImage = new double[FontClassifierConfig.batchSize][FontClassifierConfig.channels][height][width];
        for (int b = 0; b < FontClassifierConfig.batchSize; b++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = pr.getColor(x, y);
                    doubleImage[b][0][y][x] = c.getRed();
                    doubleImage[b][1][y][x] = c.getGreen();
                    doubleImage[b][2][y][x] = c.getBlue();
                }
            }
        }

        double[] label = new double[FontManager.getFonts().length];
        label[FontManager.getPosition(fontFamily)] = 1d;
        return new Pair<>(Nd4j.create(doubleImage), Nd4j.create(label));
    }

    public Pair<INDArray, INDArray> getNextSample() {
        return getNextSample(getRandomCharacter());
    }

    public Pair<WritableImage, String> getNextElement() {
        return getNextElement(getRandomCharacter());
    }

    public void distortText(Text txt) {
        int amt = rand.nextInt(15);
        for (int i = 0; i < amt; i++) {
            txt.setEffect(getRandomEffect());
        }
    }

    public Effect getRandomEffect() {
        return effects[(int) (effects.length * rand.nextDouble())].getRandomEffect(rand);
    }


    protected Font getRandomFont() {
        return fonts[(int) (fonts.length * rand.nextDouble())];
    }

    public ArrayList<Pair<INDArray, INDArray>> getSamples(char c, int samplesPerFont) {
        ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<>();
        setup(samplesPerFont);
        for (String font: FontManager.getFonts()) {
            for (int i = 0; i < samplesPerFont; i++) {
                list.add(getNextSample(c, font));
            }
        }
        Collections.shuffle(list);
        return list;
    }

    public ArrayList<Pair<INDArray, INDArray>> getSamples(int samplesPerFont) {
        ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<>();
        setup(samplesPerFont);
        for (String font: FontManager.getFonts()) {
            for (int i = 0; i < samplesPerFont; i++) {
                list.add(getNextSample(getRandomCharacter(), font));
            }
        }
        Collections.shuffle(list);
        return list;
    }

    private void generateEffects() {
        ArrayList<EffectHelper> effectHelpers = new ArrayList<>();
        effectHelpers.add((rand) -> new Glow(rand.nextDouble()));
        effectHelpers.add(rand1 -> new ColorAdjust(rand1.nextDouble() * 0.5 + 0.5, rand1.nextDouble() * 0.5 + 0.5, rand1.nextDouble() * 0.5, rand1.nextDouble()));
        effectHelpers.add(rand1 -> new Blend(BlendMode.values()[(int) (rand1.nextDouble() * BlendMode.values().length)]));
        effectHelpers.add(rand1 -> new Bloom(rand1.nextDouble()* 15));
        effectHelpers.add(rand1 -> new BoxBlur(rand1.nextDouble() * 2, rand1.nextDouble()* 2, (int) (rand1.nextDouble() * 10)));
        effectHelpers.add(rand1 -> new GaussianBlur(rand1.nextDouble() * 3));
        effectHelpers.add(rand1 -> new Reflection(rand1.nextDouble() * 5, rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()));
        effectHelpers.add(rand1 -> new SepiaTone(rand1.nextDouble()*15));
        effectHelpers.add(rand1 -> new Shadow(BlurType.values()[(int) (rand1.nextDouble() * BlurType.values().length)], Color.color(rand1.nextDouble()* 0.8, rand1.nextDouble() * 0.8, rand1.nextDouble() * 0.8, rand1.nextDouble() * 0.25 + 0.75), rand1.nextDouble() * 2));
        effectHelpers.add(rand1 -> new DropShadow(BlurType.values()[(int) (rand1.nextDouble() * BlurType.values().length)], Color.color(rand1.nextDouble() * 0.8, rand1.nextDouble() * 0.8, rand1.nextDouble()* 0.8, rand1.nextDouble()*0.5), rand1.nextDouble() * 3,rand1.nextDouble() * 5, rand1.nextDouble() * 4 - 2, rand1.nextDouble() * 4 - 2));
        effectHelpers.add(rand1 -> new Lighting(new Light.Distant(rand1.nextDouble() * 100, rand1.nextDouble() * 50, Color.color(rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()))));
        effects = effectHelpers.toArray(new EffectHelper[effectHelpers.size()]);
    }

    private interface EffectHelper {
        Effect getRandomEffect(Random rand);
    }
}
