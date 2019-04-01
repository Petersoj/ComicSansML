package edu.usu.hackathon2019.bettermnist.viewer;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import edu.usu.hackathon2019.bettermnist.CharRunner;
import edu.usu.hackathon2019.bettermnist.viewer.components.ButtonPanel;
import edu.usu.hackathon2019.bettermnist.viewer.components.InfoTextPane;
import edu.usu.hackathon2019.bettermnist.viewer.components.RasterPanel;
import edu.usu.hackathon2019.bettermnist.viewer.components.VisualOutputPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class CharRasterViewer {

    private CharRunner charRunner;

    private JFrame frame;
    private JPanel contentPanel;
    private SpringLayout springLayout;
    private ButtonPanel buttonPanel;
    private RasterPanel rasterPanel;
    private InfoTextPane infoTextPane;
    private VisualOutputPanel visualOutputPanel;
    private CharDataSet charDataSet;

    public CharRasterViewer(CharRunner charRunner) {
        this.charRunner = charRunner;
        this.frame = new JFrame();
        this.contentPanel = new JPanel();
        this.springLayout = new SpringLayout();
    }

    public void init() {
        this.setupComponents();
        this.setupLayout();
    }

    private void setupComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Do nothing if look and feel can't be set
        }

        frame.setTitle("BetterMNIST Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(contentPanel);

        contentPanel.setBackground(Color.WHITE);

        buttonPanel = new ButtonPanel(this);
        rasterPanel = new RasterPanel(this);
        infoTextPane = new InfoTextPane(this);
        visualOutputPanel = new VisualOutputPanel(this);

        buttonPanel.init();
        rasterPanel.init();
        infoTextPane.init();
        visualOutputPanel.init();

        contentPanel.add(buttonPanel);
        contentPanel.add(rasterPanel);
        contentPanel.add(infoTextPane);
        contentPanel.add(visualOutputPanel);
        contentPanel.setLayout(springLayout);
    }

    private void setupLayout() {
        Spring north = springLayout.getConstraint(SpringLayout.NORTH, contentPanel);
        Spring south = springLayout.getConstraint(SpringLayout.SOUTH, contentPanel);
        Spring east = springLayout.getConstraint(SpringLayout.EAST, contentPanel);
        Spring west = springLayout.getConstraint(SpringLayout.WEST, contentPanel);
        Spring width = springLayout.getConstraint(SpringLayout.WIDTH, contentPanel);
        Spring height = springLayout.getConstraint(SpringLayout.HEIGHT, contentPanel);

        SpringLayout.Constraints buttonPanelConstraints = springLayout.getConstraints(buttonPanel);
        SpringLayout.Constraints rasterPanelConstraints = springLayout.getConstraints(rasterPanel);
        SpringLayout.Constraints infoTextPaneConstraints = springLayout.getConstraints(infoTextPane);
        SpringLayout.Constraints visualOutputPanelConstraints = springLayout.getConstraints(visualOutputPanel);

        // ButtonPanel
        buttonPanelConstraints.setConstraint(SpringLayout.SOUTH, south);
        buttonPanelConstraints.setConstraint(SpringLayout.EAST, east);
        buttonPanelConstraints.setConstraint(SpringLayout.HEIGHT, Spring.scale(height, 0.30f));
        buttonPanelConstraints.setConstraint(SpringLayout.WIDTH, Spring.scale(width, 0.30f));

        // RasterPanel
        rasterPanelConstraints.setConstraint(SpringLayout.NORTH, north);
        rasterPanelConstraints.setConstraint(SpringLayout.SOUTH,
                springLayout.getConstraint(SpringLayout.NORTH, buttonPanel));
        rasterPanelConstraints.setConstraint(SpringLayout.EAST,
                springLayout.getConstraint(SpringLayout.WEST, buttonPanel));
        rasterPanelConstraints.setConstraint(SpringLayout.WEST, west);

        // InfoTextPane
        infoTextPaneConstraints.setConstraint(SpringLayout.NORTH, north);
        infoTextPaneConstraints.setConstraint(SpringLayout.SOUTH,
                springLayout.getConstraint(SpringLayout.NORTH, buttonPanel));
        infoTextPaneConstraints.setConstraint(SpringLayout.EAST, east);
        infoTextPaneConstraints.setConstraint(SpringLayout.WEST,
                springLayout.getConstraint(SpringLayout.WEST, buttonPanel));

        // VisualOutputPanel
        visualOutputPanelConstraints.setConstraint(SpringLayout.NORTH,
                springLayout.getConstraint(SpringLayout.NORTH, buttonPanel));
        visualOutputPanelConstraints.setConstraint(SpringLayout.SOUTH, south);
        visualOutputPanelConstraints.setConstraint(SpringLayout.EAST,
                springLayout.getConstraint(SpringLayout.WEST, buttonPanel));
        visualOutputPanelConstraints.setConstraint(SpringLayout.WEST, west);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> { // invokeLater will run the following on the AWT thread which is safer
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize((int) (screenSize.getWidth() * 0.8f), (int) (screenSize.getHeight() * 0.8f)); // 80%

            frame.revalidate(); // Marks contentPanel as invalid and re-layouts everything
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    public CharRunner getCharRunner() {
        return charRunner;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    public RasterPanel getRasterPanel() {
        return rasterPanel;
    }

    public InfoTextPane getInfoTextPane() {
        return infoTextPane;
    }

    public VisualOutputPanel getVisualOutputPanel() {
        return visualOutputPanel;
    }

    public CharDataSet getCharDataSet() {
        return charDataSet;
    }

    public void setCharDataSet(CharDataSet charDataSet) {
        this.charDataSet = charDataSet;
    }
}
