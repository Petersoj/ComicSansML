package edu.usu.hackathon2019.charindexing;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

public class StringRasterDataSet extends DataSet {

    public static final int STRING_RASTER_WIDTH = 512;
    public static final int STRING_RASTER_HEIGHT = 128;

    private String string;
    private Font font;
    private BufferedImage stringRaster;
    private GlyphVector glyphVector;
    private BufferedImage glyphsOutlineRaster;

    public StringRasterDataSet(String string, Font font) {
        super(Nd4j.create(STRING_RASTER_HEIGHT, STRING_RASTER_WIDTH),
                Nd4j.create(STRING_RASTER_HEIGHT, STRING_RASTER_WIDTH));
        this.string = string;
        this.font = font;
    }

    public void createGlyphsOutlineRaster() {
        this.stringRaster = new BufferedImage(STRING_RASTER_WIDTH, STRING_RASTER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D stringGraphics = stringRaster.createGraphics();
        stringGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        stringGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        this.glyphVector = font.createGlyphVector(stringGraphics.getFontRenderContext(), string);

        // TODO Font size may cause drawString to be outside 128x32 image size
        stringGraphics.setBackground(Color.WHITE);
        stringGraphics.clearRect(0, 0, stringRaster.getWidth(), stringRaster.getHeight());
        stringGraphics.setFont(font);
        stringGraphics.setColor(Color.BLACK);
        stringGraphics.drawString(string, 0, (int) glyphVector.getOutline().getBounds2D().getHeight());
        stringGraphics.dispose();

        this.glyphsOutlineRaster = new BufferedImage(STRING_RASTER_WIDTH, STRING_RASTER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D glyphsGraphics = glyphsOutlineRaster.createGraphics();
        glyphsGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        glyphsGraphics.setBackground(Color.WHITE);
        glyphsGraphics.clearRect(0, 0, stringRaster.getWidth(), stringRaster.getHeight());
        glyphsGraphics.setColor(Color.BLACK);

        int yTransform = (int) glyphVector.getOutline().getBounds2D().getHeight();

        for (int i = 0; i < glyphVector.getNumGlyphs(); i++) {
            Shape glyphShape = glyphVector.getGlyphOutline(i, 0, yTransform);
            glyphsGraphics.draw(glyphShape);
        }
        glyphsGraphics.dispose();
    }

    public String getString() {
        return string;
    }

    public Font getFont() {
        return font;
    }

    public BufferedImage getStringRaster() {
        return stringRaster;
    }

    public GlyphVector getGlyphVector() {
        return glyphVector;
    }

    public BufferedImage getGlyphsOutlineRaster() {
        return glyphsOutlineRaster;
    }
}
