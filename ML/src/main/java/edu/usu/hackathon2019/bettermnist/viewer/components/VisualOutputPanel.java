package edu.usu.hackathon2019.bettermnist.viewer.components;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import edu.usu.hackathon2019.bettermnist.viewer.CharRasterViewer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class VisualOutputPanel extends JPanel {

    private CharRasterViewer charRasterViewer;
    private int noProbabiltyBaseAlpha;

    public VisualOutputPanel(CharRasterViewer charRasterViewer) {
        this.charRasterViewer = charRasterViewer;
        this.noProbabiltyBaseAlpha = 40;
    }

    public void init() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setBackground(Color.WHITE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        CharDataSet charDataSet = charRasterViewer.getCharDataSet();
        if (charDataSet == null) {
            return;
        }
        BufferedImage charRaster = charDataSet.getRaster();
        if (charRaster == null || charDataSet.getFont() == null) {
            return;
        }

        Graphics2D graphics = (Graphics2D) g;
        this.configureGraphics(graphics);

        char[] upperCaseChars = CharDataSet.getUpperCaseCharacters();
        char[] lowerCaseChars = CharDataSet.getLowerCaseCharacters();
        char[] numberChars = CharDataSet.getNumbericCharacters();

        int stepX = this.getWidth() / upperCaseChars.length; // Uppercase chars has largest length of chars
        int stepY = this.getHeight() / 3; // One row for uppercase, one for lowercase, and one for numbers

        // Draw Uppercase
        int currentX = 0;
        int currentY = 0;
        for (int i = 0; i < upperCaseChars.length; i++) {
            this.drawCharacter(upperCaseChars[i], this.getCharProbability(charDataSet, i), currentX, currentY, graphics);
            currentX += stepX;
        }

        // Draw Lowercase
        currentX = 0;
        currentY += stepY;
        for (int i = 0; i < lowerCaseChars.length; i++) {
            this.drawCharacter(lowerCaseChars[i], this.getCharProbability(charDataSet, i + upperCaseChars.length),
                    currentX, currentY, graphics);
            currentX += stepX;
        }

        // Draw Numbers
        currentX = 0;
        currentY += stepY;
        for (int i = 0; i < numberChars.length; i++) {
            this.drawCharacter(numberChars[i], this.getCharProbability(charDataSet,
                    i + upperCaseChars.length + lowerCaseChars.length),
                    currentX, currentY, graphics);
            currentX += stepX;
        }
    }

    private void configureGraphics(Graphics2D graphics) {
        // Antialiasing prevents pixelated rasterizing and smooths things out
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fractional Metrics basically means that pixels are drawn with subpixel accuracy
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // Explicitly specify interpolation algorithm (this is a faster algorithm, but isn't as high of quality)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        graphics.setFont(charRasterViewer.getCharDataSet().getFont());
    }

    private void drawCharacter(char ch, double probability, int x, int y, Graphics2D graphics) {
        GlyphVector glyphVector = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), String.valueOf(ch));
        Shape charShape = glyphVector.getGlyphOutline(0);

        Rectangle2D visualBounds = charShape.getBounds2D();

        int charAlpha = (int) Math.min(255d, noProbabiltyBaseAlpha + (255 * probability));
        int translateX = x + (int) (visualBounds.getX() * -1d);
        int translateY = y + (int) (visualBounds.getY() * -1d);

        graphics.translate(translateX, translateY);
        graphics.setColor(new Color(0, 0, 0, charAlpha));
        graphics.fill(charShape);
        graphics.translate(-translateX, -translateY); // Translate back for future use
    }

    private double getCharProbability(CharDataSet charDataSet, int charIndex) {
        return charDataSet.getLabels().getDouble(charIndex);
    }
}
