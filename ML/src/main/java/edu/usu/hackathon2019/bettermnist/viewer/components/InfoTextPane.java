package edu.usu.hackathon2019.bettermnist.viewer.components;

import edu.usu.hackathon2019.bettermnist.viewer.CharRasterViewer;

import javax.swing.JTextArea;
import java.awt.Color;

public class InfoTextPane extends JTextArea {

    private CharRasterViewer charRasterViewer;

    public InfoTextPane(CharRasterViewer charRasterViewer) {
        this.charRasterViewer = charRasterViewer;
    }

    public void init() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setEditable(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setBackground(Color.WHITE);
    }
}
