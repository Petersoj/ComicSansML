package ML.SampleDate;

import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class JavaFXSampleGenerator extends SampleDataGenerator {

    protected static Font[] fonts;
    protected static EffectHelper[] effects;

    public void setup(int sampleCount) {
        rand = new Random();
        fonts = new Font[Font.getFamilies().size()];
        for (int i = 0; i < Font.getFamilies().size(); i++) {
            fonts[i] = Font.font(Font.getFamilies().get(i));
        }
        generateEffects();
    }

    @Override
    protected void cleanup() {

    }

    @Override
    protected void destroy() {
        fonts = null;
        System.gc();
    }

    public Pair<WritableImage, String> getNextElement() {
        String label = ((Character) getRandomCharacter()).toString();
        Text txt = new Text(label);
        txt.setFont(getRandomFont());
        distortText(txt);
        return new Pair<>(txt.snapshot(null, null), label);

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

    private void generateEffects() {
        ArrayList<EffectHelper> effectHelpers = new ArrayList<>();
        effectHelpers.add((rand) -> new Glow(rand.nextDouble()));
        effectHelpers.add(rand1 -> new ColorAdjust(rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()));
        effectHelpers.add(rand1 -> new Blend(BlendMode.values()[(int) (rand1.nextDouble() * BlendMode.values().length)]));
        effectHelpers.add(rand1 -> new Bloom(rand1.nextDouble()));
        effectHelpers.add(rand1 -> new BoxBlur(rand1.nextDouble(), rand1.nextDouble(), (int) (rand1.nextDouble() * 10)));
        effectHelpers.add(rand1 -> new GaussianBlur(rand1.nextDouble() * 15));
        effectHelpers.add(rand1 -> new Reflection(rand1.nextDouble() * 15, rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()));
        effectHelpers.add(rand1 -> new SepiaTone(rand1.nextDouble()));
        effectHelpers.add(rand1 -> new Shadow(BlurType.values()[(int) (rand1.nextDouble() * BlurType.values().length)], Color.color(rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()), rand1.nextDouble() * 15));
        effectHelpers.add(rand1 -> new DropShadow(BlurType.values()[(int) (rand1.nextDouble() * BlurType.values().length)], Color.color(rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()), rand1.nextDouble() * 15,rand1.nextDouble() * 15, rand1.nextDouble() * 15, rand1.nextDouble() * 15));
        effectHelpers.add(rand1 -> new Lighting(new Light.Distant(rand1.nextDouble() * 100, rand1.nextDouble() * 50, Color.color(rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble(), rand1.nextDouble()))));
        effects = effectHelpers.toArray(new EffectHelper[effectHelpers.size()]);
    }

    private interface EffectHelper {
        Effect getRandomEffect(Random rand);
    }
}
