package ui;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed bellow
 * these edges.
 */
public class MapView extends JPanel{
    private static final int NODE_BUTTON_SIZE = 20;

    private java.util.List<Edge> graphEdgeList;
    private java.util.List<Location> locationList;
    private java.util.List<LocationButton> locationButtonList;

    private BufferedImage mapBackground;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param mapBackgroundImagePath The path to the image that will be used as the background
     */
    public MapView(LocationGraph graph, String mapBackgroundImagePath) {
        super(true);
        try {
            mapBackground = ImageIO.read(new File(mapBackgroundImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationButtonList = new ArrayList<>();

        setPreferredSize(getImageResolution());
        setLayout(null);
        resetGraphData(graph);
    }

    /**
     * Reset the map to display the edges of the passed graph.
     *
     * @param graph The graph whose edges will be displayed
     */
    public final void resetGraphData(LocationGraph graph) {
        this.graphEdgeList = graph.getAllEdges();
        this.locationList = graph.getAllLocations();

        redrawButtons();
    }

    /**
     * Remove and replace all buttons, setting them to thier original state
     */
    public void redrawButtons() {
        locationButtonList.forEach(this::remove);
        locationButtonList.clear();
        drawButtons();
    }

    /**
     * Returns the list of LocationButtons contained in the MapView.
     *
     * @return the list of LocationButtons contained in the MapView
     */
    public List<LocationButton> getLocationButtonList() {
        return locationButtonList;
    }

    /**
     * Returns a Dimension that describes the resolution of the background image. Or 0 if the image
     * failed to load
     *
     * @return The dimension of the image, or 0 if the image didn't load
     */
    public Dimension getImageResolution() {
        Dimension result = new Dimension(0, 0);
        if (mapBackground != null) {
            result = new Dimension(mapBackground.getWidth(), mapBackground.getHeight());
        }

        return result;
    }

    /**
     * Adds one LocationButton to the panel for each Location in the backing Location graph.
     */
    private void drawButtons() {
        for (Location loc: locationList) {
            LocationButton currentButton = new LocationButton(loc);
            currentButton.setBackground(Color.CYAN);
            currentButton.setBorder(BorderFactory.createEmptyBorder());

            int xPos = (int) (loc.getPosition().x * getImageResolution().width);
            int yPos = (int) (loc.getPosition().y * getImageResolution().height);

            currentButton.setBounds(xPos - (NODE_BUTTON_SIZE / 2), yPos  - (NODE_BUTTON_SIZE / 2),
                    NODE_BUTTON_SIZE, NODE_BUTTON_SIZE);
            add(currentButton);
            locationButtonList.add(currentButton);
        }
        repaint();
    }

    /**
     * Draw the edges contained in this MapView, as well as the background.
     *
     * @param g The graphics object used to draw the edges and the background
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(g instanceof Graphics2D)) {
            throw new RuntimeException("The supplied graphics object is not a Graphics2D!");
        }

        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        //Draw background if loaded
        if (mapBackground != null) {
            g2d.drawImage(mapBackground, 0, 0, null);
        }

        g2d.setStroke(new BasicStroke(4));
        for (Edge e: graphEdgeList) {
            if (e.hasAttribute(EdgeAttribute.EDGE_REMOVED)) {
                g2d.setColor(Color.black);
            } else if (e.hasAttribute(EdgeAttribute.INDOORS)) {
                g2d.setColor(Color.blue);
            } else if (e.hasAttribute(EdgeAttribute.OUTDOORS)) {
                g2d.setColor(Color.yellow);
            }  else {
                g2d.setColor(Color.red);
            }

            Dimension imageRes = getImageResolution();
            int x1 = (int) (e.getNode1().getPosition().x * imageRes.getWidth());
            int y1 = (int) (e.getNode1().getPosition().y * imageRes.getHeight());
            int x2 = (int) (e.getNode2().getPosition().x * imageRes.getWidth());
            int y2 = (int) (e.getNode2().getPosition().y * imageRes.getHeight());

            g2d.drawLine(x1, y1, x2, y2);
        }

    }
}
