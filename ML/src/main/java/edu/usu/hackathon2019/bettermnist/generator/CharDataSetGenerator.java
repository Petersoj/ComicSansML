package edu.usu.hackathon2019.bettermnist.generator;

import edu.usu.hackathon2019.bettermnist.CharDataSet;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CharDataSetGenerator {

    private char[] chars;
    private ThreadLocalRandom random;
    private ArrayList<Font> fonts;
    private CharRasterGenerator charRasterGenerator;

    public CharDataSetGenerator() {
        this.chars = CharDataSet.CHARACTERS;
        this.random = ThreadLocalRandom.current();
        this.fonts = new ArrayList<>();
        this.charRasterGenerator = new CharRasterGenerator(this);
    }

    public void init() throws IOException, FontFormatException {
        this.loadFonts();
    }

    private void loadFonts() throws IOException, FontFormatException {
        // Handwritten fonts
        fonts.add(this.getNamedTrueTypeFont("handwritten", "ArchitectsDaughter.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "Estefin.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "Friday Vibes.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "Gregor Miller\'s Friends.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "gunny_rewritten.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "HandwritingCR.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "Rockness.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "TingTong.ttf"));
        fonts.add(this.getNamedTrueTypeFont("handwritten", "WhiteAngelica.ttf"));

        // TODO add lots of other types of fonts (look at possible styles at https://www.dafont.com/)
    }

    private Font getNamedTrueTypeFont(String subfolder, String fontName) throws IOException, FontFormatException {
        String path = "/fonts/" + subfolder + "/" + fontName;
        return Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream(path));
    }

    public CharDataSet generate() {
        char character = chars[random.nextInt(chars.length)];
        Font charFont = fonts.get(random.nextInt(fonts.size()));
        BufferedImage charRaster = charRasterGenerator.generate();

        CharDataSet charDataSet = new CharDataSet(character);
        charDataSet.setFont(charFont);
        charDataSet.setRaster(charRaster);
        return charDataSet;
    }

    public CharDataSet[] generate(int number) {
        CharDataSet[] charDataSets = new CharDataSet[number];
        for (int i = 0; i < number; i++) {
            charDataSets[i] = generate();
        }
        return charDataSets;
    }

    public ThreadLocalRandom getRandom() {
        return random;
    }

    public void setRandom(ThreadLocalRandom random) {
        this.random = random;
    }

    public ArrayList<Font> getFonts() {
        return fonts;
    }

    public void setFonts(ArrayList<Font> fonts) {
        this.fonts = fonts;
    }

    public CharRasterGenerator getCharRasterGenerator() {
        return charRasterGenerator;
    }

    public void setCharRasterGenerator(CharRasterGenerator charRasterGenerator) {
        this.charRasterGenerator = charRasterGenerator;
    }
}
