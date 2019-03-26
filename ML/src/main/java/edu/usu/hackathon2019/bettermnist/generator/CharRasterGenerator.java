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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class CharRasterGenerator {

    private CharDataSetGenerator generator;
    private ThreadLocalRandom random;
    private int rasterWidth;
    private int rasterHeight;
    private double scaleFactor;

    public CharRasterGenerator(CharDataSetGenerator generator) {
        this.generator = generator;
        this.random = generator.getRandom();

        // Set defaults
        this.rasterWidth = 64;
        this.rasterHeight = 64;
        this.scaleFactor = 0.7; // 70% scale to give some wiggle room for the warping/filters
    }

    public BufferedImage generate(CharDataSet charDataSet) {
        BufferedImage charRaster = new BufferedImage(rasterWidth, rasterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = charRaster.createGraphics();

        this.prepareGraphics(graphics, charDataSet);
        this.prepareAndRasterizeChar(graphics, charDataSet);
        charRaster = this.applyFilters(charRaster);

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
        Shape charShape = glyphVector.getGlyphOutline(0); // This is very resource intensive!
        charDataSet.setShape(charShape);

        Rectangle2D visualBounds = charShape.getBounds2D();

        double scaleX = (double) rasterWidth / visualBounds.getWidth();
        double scaleY = (double) rasterWidth / visualBounds.getHeight();
        if (scaleX < scaleY) {
            scaleY = scaleX;
        } else if (scaleY < scaleX) {
            scaleX = scaleY;
        }

        graphics.scale(scaleX, scaleY); // Set font visual bounds to fill up the entire raster
        graphics.translate(visualBounds.getX() * -1D, visualBounds.getY() * -1D); // set visualBounds X, Y to draw at 0, 0
        graphics.fill(charShape); // drawString() uses glyphOutlines internally so use this instead!
    }

    private BufferedImage applyFilters(BufferedImage bufferedImage) {
        // TODO Apply filters randomly
        return applyPerspectiveFilter(bufferedImage);
    }

    // http://www.jhlabs.com/ip/filters/PerspectiveFilter.html
    private BufferedImage applyPerspectiveFilter(BufferedImage raster) {
        PerspectiveFilter perspectiveFilter = new PerspectiveFilter();

        double translateScaleX = (1 - scaleFactor) * rasterWidth;
        double translateScaleY = (1 - scaleFactor) * rasterHeight;

        Point2D topLeft = new Point2D.Double(random.nextDouble(translateScaleX),
                random.nextDouble(translateScaleY));
        Point2D topRight = new Point2D.Double(-random.nextDouble(translateScaleX) + rasterWidth,
                random.nextDouble(translateScaleY));
        Point2D bottomRight = new Point2D.Double(-random.nextDouble(translateScaleX) + rasterWidth,
                -random.nextDouble(translateScaleY) + rasterHeight);
        Point2D bottomLeft = new Point2D.Double(random.nextDouble(translateScaleX),
                -random.nextDouble(translateScaleY) + rasterHeight);

        perspectiveFilter.setCorners((float) topLeft.getX(), (float) topLeft.getY(),
                (float) topRight.getX(), (float) topRight.getY(),
                (float) bottomRight.getX(), (float) bottomRight.getY(),
                (float) bottomLeft.getX(), (float) bottomLeft.getY());

        BufferedImage filteredImage = perspectiveFilter.filter(raster, null);
        BufferedImage filteredScaledImage = new BufferedImage(rasterWidth, rasterHeight, filteredImage.getType());
        Graphics2D scaledGraphics = filteredScaledImage.createGraphics();

        System.out.println(filteredImage.getWidth());

        int dx1 = (rasterWidth - filteredImage.getWidth()) / 2;
        int dy1 = (rasterHeight - filteredImage.getHeight()) / 2;

        scaledGraphics.drawImage(filteredImage, dx1, dy1, dx1 + filteredImage.getWidth(), dy1 + filteredImage.getHeight(),
                0, 0, filteredImage.getWidth(), filteredImage.getHeight(), null);
        scaledGraphics.dispose();

        return filteredScaledImage;
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
