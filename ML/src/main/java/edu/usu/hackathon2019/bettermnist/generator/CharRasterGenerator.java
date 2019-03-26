package edu.usu.hackathon2019.bettermnist.generator;

import com.jhlabs.image.FieldWarpFilter;
import com.jhlabs.image.PerspectiveFilter;
import com.jhlabs.image.VariableBlurFilter;
import edu.usu.hackathon2019.bettermnist.CharDataSet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class CharRasterGenerator {

    private CharDataSetGenerator generator;
    private ThreadLocalRandom random;
    private int rasterWidth;
    private int rasterHeight;

    public CharRasterGenerator(CharDataSetGenerator generator) {
        this.generator = generator;
        this.random = generator.getRandom();

        // Set defaults
        this.rasterWidth = 64;
        this.rasterHeight = 64;
    }

    public BufferedImage generate(CharDataSet charDataSet) {
        BufferedImage charRaster = new BufferedImage(rasterWidth, rasterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = charRaster.createGraphics();

        this.prepareGraphics(graphics, charDataSet);
        this.prepareAndRasterizeChar(graphics, charDataSet);
        this.applyFilters(graphics, charDataSet);

        graphics.dispose();
        return charRaster;
    }

    private void prepareGraphics(Graphics2D graphics, CharDataSet charDataSet) {
        // Antialiasing prevents pixelated rasterizing and smooths things out
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fractional Metrics basically means that pixels are drawn with subpixel accuracy
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // Explicitly specify interpolation algorithm (this is a faster algorithm, but isn't as high of quality)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        graphics.setFont(charDataSet.getFont());

        graphics.setPaint(Color.BLACK);
    }

    private void prepareAndRasterizeChar(Graphics2D graphics, CharDataSet charDataSet) {
        // Use GlyphVector to get high precision bounds of character raster
        GlyphVector glyphVector = charDataSet.getFont().createGlyphVector(
                graphics.getFontRenderContext(),
                new char[]{charDataSet.getCharacter()});
        Shape charShape = glyphVector.getGlyphOutline(0);
        charDataSet.setShape(charShape); // This is very resource intensive!

        Rectangle2D visualBounds = charShape.getBounds2D();
        double x = rasterWidth - visualBounds.getWidth();
        double y = rasterHeight - visualBounds.getHeight();
        if (x < 0) {
            x = 0;
        } else {
            x = random.nextDouble(x);
        }
        if (y < 0) {
            y = rasterHeight;
        } else {
            y = random.nextDouble(y) + visualBounds.getHeight();
        }
        // TODO use AffineTransform to scale the font shape down to always fit in the raster
        graphics.translate(x, y);
        graphics.fill(charShape); // drawString() uses glyphOutlines internally!
    }

    private void applyFilters(Graphics2D graphics, CharDataSet charDataSet) {
        // TODO Apply filters randomly
    }

    // http://www.jhlabs.com/ip/filters/PerspectiveFilter.html
    private void applyPerspectiveFilter(BufferedImage raster, Graphics2D graphics) {
        PerspectiveFilter perspectiveFilter = new PerspectiveFilter();
        // TODO perspectiveFilter
    }

    // http://www.jhlabs.com/ip/filters/FieldWarpFilter.html
    private void applyFieldWarpFilter(BufferedImage raster, Graphics2D graphics) {
        FieldWarpFilter fieldWarpFilter = new FieldWarpFilter();
        // TODO fieldWarpFilter
    }

    // http://www.jhlabs.com/ip/filters/VariableBlurFilter.html
    private void applyVariableBlurFilter(BufferedImage raster, Graphics2D graphics) {
        VariableBlurFilter variableBlurFilter = new VariableBlurFilter();
        // TODO variableBlurFilter
    }

    public int getRasterWidth() {
        return rasterWidth;
    }

    public void setRasterWidth(int rasterWidth) {
        this.rasterWidth = rasterWidth;
    }

    public int getRasterHeight() {
        return rasterHeight;
    }

    public void setRasterHeight(int rasterHeight) {
        this.rasterHeight = rasterHeight;
    }
}
