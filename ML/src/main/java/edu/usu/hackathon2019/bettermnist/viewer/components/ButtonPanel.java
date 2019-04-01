package edu.usu.hackathon2019.bettermnist.viewer.components;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import edu.usu.hackathon2019.bettermnist.generator.CharDataSetGenerator;
import edu.usu.hackathon2019.bettermnist.network.CharClassifierNetwork;
import edu.usu.hackathon2019.bettermnist.viewer.CharRasterViewer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ButtonPanel extends JPanel implements ActionListener {

    private CharRasterViewer charRasterViewer;
    private CharDataSetGenerator charDataSetGenerator;
    private CharClassifierNetwork charClassifierNetwork;

    private SpringLayout springLayout;
    private JButton generateButton;
    private JButton chooseImageButton;
    private String generateMessage;
    private String chooseImageMessage;

    public ButtonPanel(CharRasterViewer charRasterViewer) {
        this.charRasterViewer = charRasterViewer;
        this.charDataSetGenerator = charRasterViewer.getCharRunner().getCharDataSetGenerator();
        this.charClassifierNetwork = charRasterViewer.getCharRunner().getCharClassifierNetwork();
        this.springLayout = new SpringLayout();
    }

    public void init() {
        this.setupDefaults();
        this.setupComponents();
        this.setupLayout();
        this.setupListeners();
    }

    private void setupDefaults() {
        this.chooseImageMessage = "\nPredicted Character: %c\n";
        this.generateMessage = "\nGenerated Character: %c\nPredicted Character: %c\nMatch: %s\n";
    }

    private void setupComponents() {
        generateButton = new JButton("Generate Sample");
        chooseImageButton = new JButton("Choose Image Sample");

        this.add(generateButton);
        this.add(chooseImageButton);
        this.setLayout(springLayout);

        this.setBackground(Color.WHITE);
    }

    private void setupLayout() {
        Spring height = springLayout.getConstraint(SpringLayout.HEIGHT, this);
        Spring horizontalCenter = springLayout.getConstraint(SpringLayout.HORIZONTAL_CENTER, this);

        SpringLayout.Constraints generateButtonConstraints = springLayout.getConstraints(generateButton);
        SpringLayout.Constraints chooseImageButtonConstraints = springLayout.getConstraints(chooseImageButton);
        Spring maxWidth = Spring.max(generateButtonConstraints.getWidth(), chooseImageButtonConstraints.getWidth());

        generateButtonConstraints.setConstraint(SpringLayout.VERTICAL_CENTER, Spring.scale(height, 0.33f));
        generateButtonConstraints.setConstraint(SpringLayout.HORIZONTAL_CENTER, horizontalCenter);
        generateButtonConstraints.setWidth(maxWidth);

        chooseImageButtonConstraints.setConstraint(SpringLayout.VERTICAL_CENTER, Spring.scale(height, 0.66f));
        chooseImageButtonConstraints.setConstraint(SpringLayout.HORIZONTAL_CENTER, horizontalCenter);
        chooseImageButtonConstraints.setWidth(maxWidth);
    }

    private void setupListeners() {
        generateButton.addActionListener(this);
        chooseImageButton.addActionListener(this);
    }

    // TEMP FOR DEBUGGING
    private CharDataSet tempCharDataSet;
    private boolean varyFonts = true;
    private boolean varyCharacters = true;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            if (tempCharDataSet == null) {
                tempCharDataSet = charDataSetGenerator.generate(null, null);
            } else {
                tempCharDataSet = charDataSetGenerator.generate(varyFonts ? null : tempCharDataSet.getFont(),
                        varyCharacters ? null : tempCharDataSet.getCharacter());
            }

            CharDataSet predictedCharDataSet;
            if (charClassifierNetwork.doesExist()) {
                predictedCharDataSet = charClassifierNetwork.predict(tempCharDataSet);
                tempCharDataSet.setLabels(predictedCharDataSet.getLabels());
            } else {
                predictedCharDataSet = tempCharDataSet;
            }

            String formattedMessage = String.format(generateMessage, tempCharDataSet.getCharacter(),
                    predictedCharDataSet.getCharacter(),
                    tempCharDataSet.getCharacter() == predictedCharDataSet.getCharacter() ? "YES" : "NO");
            charRasterViewer.getInfoTextPane().setText(formattedMessage);
            charRasterViewer.setCharDataSet(tempCharDataSet);
            SwingUtilities.invokeLater(() -> charRasterViewer.getFrame().repaint());

        } else if (e.getSource() == chooseImageButton) {
            JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home"));
            jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (jFileChooser.showOpenDialog(charRasterViewer.getFrame()) == JFileChooser.APPROVE_OPTION) {
                File chosenFile = jFileChooser.getSelectedFile();
                if (chosenFile != null) {
                    try {
                        BufferedImage inputImage = ImageIO.read(chosenFile);
                        // TODO feed inputImage to NN
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(charRasterViewer.getFrame(),
                                ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
