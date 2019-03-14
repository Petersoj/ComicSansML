package edu.usu.hackathon2019.charclassifier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CharDataSetViewer {

    private JFrame frame;
    private JPanel panel;

    private CharDataSet charDataSet;

    public CharDataSetViewer() {
        this.createComponents();
    }

    private void createComponents() {
        this.frame = new JFrame();
        this.panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D graphics = (Graphics2D) g;
                if (charDataSet == null) {
                    return;
                }

                // Draw here
            }
        };

        this.frame.setContentPane(panel);
        this.frame.setSize(CharDataSet.CHAR_RASTER_WIDTH,
                CharDataSet.CHAR_RASTER_HEIGHT + frame.getInsets().top);
        this.frame.setLocationRelativeTo(null);
    }

    public void show() {
        this.frame.setVisible(true);
    }

}
