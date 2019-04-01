package edu.usu.hackathon2019.bettermnist.viewer.components;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import edu.usu.hackathon2019.bettermnist.viewer.CharRasterViewer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class RasterPanel extends JPanel {

    private CharRasterViewer charRasterViewer;

    public RasterPanel(CharRasterViewer charRasterViewer) {
        this.charRasterViewer = charRasterViewer;
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
        if (charRaster == null) {
            return;
        }

        Graphics2D graphics = (Graphics2D) g;
        this.configureGraphics(graphics);

        int sideLength = Math.min(this.getWidth(), this.getHeight());

        int x = (this.getWidth() - sideLength) / 2;
        int y = (this.getHeight() - sideLength) / 2;

        graphics.drawImage(charDataSet.getRaster(), x, y, sideLength, sideLength, null);
    }

    private void configureGraphics(Graphics2D graphics) {
        // Antialiasing prevents pixelated rasterizing and smooths things out
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Explicitly specify interpolation algorithm (this is a faster algorithm, but isn't as high of quality)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }
}
