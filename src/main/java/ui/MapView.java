package ui;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed bellow
 * these edges.
 */
public class MapView extends JPanel{
    private static final int NODE_BUTTON_SIZE = 10;

    private double zoomFactor;

    private java.util.List<Edge> graphEdgeList;
    private java.util.List<Location> locationList;
    private java.util.List<LocationButton> locationButtonList;

    private Image mapBackground;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param mapBackground The image that will be used as the background
     * @param defaultZoom The zoom level the map will be at when the app starts
     */
    public MapView(LocationGraph graph, BufferedImage mapBackground, double defaultZoom) {
        super(true);

        this.mapBackground = mapBackground;
        locationButtonList = new ArrayList<>();
        zoomFactor = defaultZoom;

        setPreferredSize(getImagePixelSize());
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
     * Remove and replace all buttons, setting them to thier original state.
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
    public Dimension getImagePixelSize() {
        return new Dimension((int) (mapBackground.getWidth(null) * zoomFactor),
                (int) (mapBackground.getHeight(null) * zoomFactor));
    }

    /**
     * Adjust the zoom value by adding the passed value.
     *
     * @param toAdd the value to add
     */
    public void zoomIncrementBy(double toAdd) {
        zoomFactor += toAdd;
        setPreferredSize(getImagePixelSize());
    }

    /**
     * Returns the current zoom factor.
     *
     * @return the current zoom factor
     */
    public double getZoomFactor() {
        return zoomFactor;
    }

    /**
     * Adds one LocationButton to the panel for each Location in the backing Location graph.
     */
    protected void drawButtons() {
        for (Location loc: locationList) {
            LocationButton currentButton = new LocationButton(loc);
            currentButton.setBackground(Color.YELLOW);
            currentButton.setBorder(BorderFactory.createEmptyBorder());

            int xPos = (int) (loc.getPosition().x * getImagePixelSize().width);
            int yPos = (int) (loc.getPosition().y * getImagePixelSize().height);

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
        Dimension imageRes = getImagePixelSize();
        g2d.drawImage(mapBackground, 0, 0, (int) imageRes.getWidth(), (int) imageRes.getHeight(), null);

        g2d.setStroke(new BasicStroke(4));
        for (Edge e: graphEdgeList) {
            if (e.hasAttribute(EdgeAttribute.EDGE_REMOVED)) {
                g2d.setColor(Color.black);
            } else if (e.hasAttribute(EdgeAttribute.OUTDOORS)) {
                g2d.setColor(Color.yellow);
            }  else {
                g2d.setColor(Color.ORANGE);
            }

            int x1 = (int) (e.getNode1().getPosition().x * imageRes.getWidth());
            int y1 = (int) (e.getNode1().getPosition().y * imageRes.getHeight());
            int x2 = (int) (e.getNode2().getPosition().x * imageRes.getWidth());
            int y2 = (int) (e.getNode2().getPosition().y * imageRes.getHeight());

            g2d.drawLine(x1, y1, x2, y2);
        }

    }
}
