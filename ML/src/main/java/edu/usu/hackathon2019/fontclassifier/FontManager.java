package edu.usu.hackathon2019.fontclassifier;

import javafx.scene.text.Font;
import org.nd4j.evaluation.classification.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FontManager {

    protected static String[] fonts;
    protected static HashMap<String, Integer> fontIndex;

    public static void init() {
        setup();
    }

    private static void setup() {
        HashMap<String, Integer> result = new HashMap<>();
        List<String> families = Font.getFamilies();
        for (int i = 0; i < families.size(); i++) {
            result.put(families.get(i), i);
        }
        FontManager.fonts = families.toArray(new String[families.size()]);
        fontIndex = result;
        for (String font: FontManager.fonts) {
            System.out.println("font: " + font);
        }
    }

    public static String[] getFonts() {
        return fonts;
    }

    public static int getPosition(String font) {
        return fontIndex.getOrDefault(font, -1);
    }
}
