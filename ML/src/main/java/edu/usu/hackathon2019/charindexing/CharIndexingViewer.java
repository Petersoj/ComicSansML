package edu.usu.hackathon2019.charindexing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CharIndexingViewer {

    private JFrame frame;
    private JPanel panel;

    private StringRasterDataSet stringRasterDataSet;

    public CharIndexingViewer() {
        this.createComponents();
    }

    private void createComponents() {
        this.frame = new JFrame();
        this.panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (stringRasterDataSet.getStringRaster() == null || stringRasterDataSet.getGlyphsOutlineRaster() == null) {
                    return;
                }
                Graphics2D graphics = (Graphics2D) g;
                graphics.drawImage(stringRasterDataSet.getStringRaster(), 0, 0, null);
                graphics.drawImage(stringRasterDataSet.getGlyphsOutlineRaster(), 0,
                        StringRasterDataSet.STRING_RASTER_HEIGHT, null);
            }
        };

        this.frame.setContentPane(panel);
        this.frame.setSize(StringRasterDataSet.STRING_RASTER_WIDTH, StringRasterDataSet.STRING_RASTER_HEIGHT * 2 + 22);
        this.frame.setLocationRelativeTo(null);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public StringRasterDataSet getStringRasterDataSet() {
        return stringRasterDataSet;
    }

    public void setStringRasterDataSet(StringRasterDataSet stringRasterDataSet) {
        this.stringRasterDataSet = stringRasterDataSet;
    }
}
