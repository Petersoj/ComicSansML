package edu.usu.hackathon2019.bettermnist.generator;

import com.jhlabs.image.FieldWarpFilter;
import com.jhlabs.image.PerspectiveFilter;
import com.jhlabs.image.VariableBlurFilter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class CharRasterGenerator {

    private CharDataSetGenerator charDataSetGenerator;
    private ThreadLocalRandom random;
    private int rasterWidth;
    private int rasterHeight;

    public CharRasterGenerator(CharDataSetGenerator charDataSetGenerator) {
        this.charDataSetGenerator = charDataSetGenerator;
        this.random = charDataSetGenerator.getRandom();

        // Set defaults
        this.rasterWidth = 64;
        this.rasterHeight = 64;
    }

    public BufferedImage generate() {
        BufferedImage charRaster = new BufferedImage(rasterWidth, rasterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = charRaster.createGraphics();

        // TODO rasterize font on to image (make sure it's in bounds) and apply filters randomly
        return charRaster;
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
