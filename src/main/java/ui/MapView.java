package ui;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed bellow
 * these edges.
 */
public class MapView extends JScrollPane{
    private static final int NODE_BUTTON_SIZE = 10;
    private static final double MINIMUM_ZOOM = 0.1;
    private static final double MAXIMUM_ZOOM = 2;

    private JPanel mapPanel;
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
        mapPanel = new JPanel(true) {
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
                    } else if (e.hasAttribute(EdgeAttribute.INDOORS)) {
                        g2d.setColor(Color.red);
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
        };

        this.mapBackground = mapBackground;
        locationButtonList = new ArrayList<>();
        zoomFactor = defaultZoom;

        mapPanel.setPreferredSize(getImagePixelSize());
        mapPanel.setLayout(null);

        setViewportView(mapPanel);

        updateGraph(graph);
        addDefaultListeners();
    }

    private void addDefaultListeners() {
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            int mouseStartX = 0;
            int mouseStartY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                Point vpp = getViewport().getViewPosition();

                vpp.translate(mouseStartX - e.getXOnScreen(), mouseStartY - e.getYOnScreen());
                mapPanel.scrollRectToVisible(new Rectangle(vpp, getViewport().getSize()));

                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if ((zoomFactor > MINIMUM_ZOOM || e.getWheelRotation() < 0) &&
                        (zoomFactor < MAXIMUM_ZOOM || e.getWheelRotation() > 0)) {

                    Point viewPortPos = getViewport().getViewPosition();

                    double viewportWidth = getViewport().getWidth();
                    double viewportHeight = getViewport().getHeight();

                    double imageWidth = getImagePixelSize().getWidth();
                    double imageHeight = getImagePixelSize().getHeight();

                    double xPosFrac = (viewPortPos.getX() + (viewportWidth / 2.0)) / imageWidth;
                    double yPosFrac = (viewPortPos.getY() + (viewportHeight / 2.0)) / imageHeight;

                    zoomIncrementBy(e.getWheelRotation() * -0.04);
                    positionButtons();
                    validate();

                    Point newViewportPos = new Point();
                    newViewportPos.x = (int) ((xPosFrac * getImagePixelSize().getWidth()) - (viewportWidth / 2.0));
                    newViewportPos.y = (int) ((yPosFrac * getImagePixelSize().getHeight()) - (viewportHeight / 2.0));

                    mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, getViewport().getSize()));

                    repaint();
                }
            }
        };
        getViewport().addMouseListener(mouseAdapter);
        getViewport().addMouseMotionListener(mouseAdapter);
        getViewport().addMouseWheelListener(mouseAdapter);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Reset the map to display the edges of the passed graph.
     * Calling the method will remove and re-add all buttons, so
     * listeners will also need to be re-added
     *
     * @param graph The graph whose edges will be displayed
     */
    public final void updateGraph(LocationGraph graph) {
        this.graphEdgeList = graph.getAllEdges();
        this.locationList = graph.getAllLocations();

        for (LocationButton locButton: locationButtonList) {
            mapPanel.remove(locButton);
        }
        locationButtonList.clear();
        addButtons();
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
     * Return the backing JPanel for this MapView. This is where the buttons are drawn
     *
     * @return The baking JPanel
     */
    public JPanel getMapPanel() {
        return mapPanel;
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
        mapPanel.setPreferredSize(getImagePixelSize());
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
    private void addButtons() {
        for (Location loc: locationList) {
            LocationButton currentButton = new LocationButton(loc);
            currentButton.setBackground(Color.YELLOW);
            currentButton.setBorder(BorderFactory.createEmptyBorder());

            mapPanel.add(currentButton);
            locationButtonList.add(currentButton);
        }

        positionButtons();
        repaint();
    }

    /**
     * Update the positions of each button on the screen.
     */
    private void positionButtons() {
        for (LocationButton locButton: locationButtonList) {
            int xPos = (int) (locButton.getAssociatedLocation().getPosition().x * getImagePixelSize().width);
            int yPos = (int) (locButton.getAssociatedLocation().getPosition().y * getImagePixelSize().height);

            locButton.setBounds(xPos - (NODE_BUTTON_SIZE / 2), yPos  - (NODE_BUTTON_SIZE / 2),
                    NODE_BUTTON_SIZE, NODE_BUTTON_SIZE);
        }
    }
}
