package ui;

import core.Edge;
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
    private static final double START_XFRAC = 0.38;
    private static final double START_YFRAC = 0.3;
    private static final int NODE_BUTTON_SIZE = 10;
    private static final double MINIMUM_ZOOM = 0.1;
    private static final double MAXIMUM_ZOOM = 2;

    private JPanel mapPanel;
    private double zoomFactor;

    private List<Edge> graphEdgeList;
    private List<Location> locationList;
    private List<LocationButton> locationButtonList;
    private List<List<Location>> routeLists;
    private List<Location> searchList;

    private MapViewStyle style;

    private Image backgroundImage;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param mapBackground The image that will be used as the background
     * @param defaultZoom The zoom level the map will be at when the app starts
     * @param viewStyle The viewStyle used by the mapView
     */
    public MapView(LocationGraph graph, BufferedImage mapBackground, double defaultZoom, MapViewStyle viewStyle) {
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
                g2d.drawImage(backgroundImage, 0, 0, (int) imageRes.getWidth(), (int) imageRes.getHeight(), null);

                g2d.setStroke(new BasicStroke(4));
                if (style.isDrawAllEdges()) {
                    for (Edge e : graphEdgeList) {
                        g2d.setColor(style.getEdgeColor());

                        int x1 = (int) (e.getNode1().getPosition().x * imageRes.getWidth());
                        int y1 = (int) (e.getNode1().getPosition().y * imageRes.getHeight());
                        int x2 = (int) (e.getNode2().getPosition().x * imageRes.getWidth());
                        int y2 = (int) (e.getNode2().getPosition().y * imageRes.getHeight());

                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }

                if (style.isDrawRoutes()) {
                    for (List<Location> route: routeLists) {
                        g2d.setColor(style.getRouteColor());

                        int previousX = (int) (route.get(0).getPosition().x * imageRes.getWidth());
                        int previousY = (int) (route.get(0).getPosition().y * imageRes.getHeight());
                        for (int i = 1; i < route.size(); i++) {
                            int currentX = (int) (route.get(i).getPosition().x * imageRes.getWidth());
                            int currentY = (int) (route.get(i).getPosition().y * imageRes.getHeight());

                            g2d.drawLine(previousX, previousY, currentX, currentY);

                            previousX = currentX;
                            previousY = currentY;
                        }
                    }
                }
                if (searchList != null && searchList.size() > 0){
                    for (Location loc: searchList) {
                        g2d.setColor(Color.RED);
                        int locX = (int) (loc.getPosition().x * imageRes.getWidth());
                        int locY = (int) (loc.getPosition().y * imageRes.getHeight());
                        g2d.drawLine(locX, locY - 5, locX, locY - 30);
                        g2d.drawLine(locX, locY - 5, locX - 10, locY - 10);
                        g2d.drawLine(locX, locY - 5, locX + 10, locY - 10);
                    }
                }
            }
        };
        //this.dt = new DevTools(graph, this);
        //todo added because we never interact with dev tools?????
        this.style = viewStyle;
        this.searchList = new ArrayList<>();
        this.routeLists = new ArrayList<>();
        this.backgroundImage = mapBackground;
        locationButtonList = new ArrayList<>();
        zoomFactor = defaultZoom;

        mapPanel.setPreferredSize(getImagePixelSize());
        mapPanel.setLayout(null);

        setViewportView(mapPanel);

        Point newViewportPos = new Point();
        newViewportPos.x = (int) ((START_XFRAC * getImagePixelSize().getWidth()));
        newViewportPos.y = (int) ((START_YFRAC * getImagePixelSize().getHeight()));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, getViewport().getSize()));

        updateGraph(graph, 0);
        addDefaultListeners();
    }

    /**
     * Add a list of locations that will be displayed.
     *
     * @param locToAdd the list of locations that will be added
     */
    public void addToSearchList(List<Location> locToAdd){
        for (Location loc: locToAdd){
            searchList.add(loc);
            for (LocationButton locButton: locationButtonList) {
                if (loc.equals(locButton.getAssociatedLocation())){
                    locButton.setBackground(Color.BLACK);
                }
            }
        }
    }

    /**
     * Add a route that will be displayed.
     *
     * @param routeToAdd the route that will be added
     */
    public void addRoute(List<Location> routeToAdd) {
        routeLists.add(routeToAdd);
        positionButtons();
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
        mapPanel.addMouseListener(mouseAdapter);
        mapPanel.addMouseMotionListener(mouseAdapter);
        mapPanel.addMouseWheelListener(mouseAdapter);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Reset the map to display the edges of the passed graph.
     * Calling the method will remove and re-add all buttons, so
     * listeners will also need to be re-added
     *
     * @param graph The graph whose edges will be displayed
     */
    public final void updateGraph(LocationGraph graph, int floor) {
        this.graphEdgeList = graph.edgeByFloorNumber(floor);
        this.locationList = graph.locationByFloorNumber(floor);
        this.routeLists = new ArrayList<>();
        this.searchList = new ArrayList<>();

        for (LocationButton locButton: locationButtonList) {
            mapPanel.remove(locButton);
        }
        locationButtonList.clear();
        addButtons();
        updateNodeVisibility();
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
        return new Dimension((int) (backgroundImage.getWidth(null) * zoomFactor),
                (int) (backgroundImage.getHeight(null) * zoomFactor));
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
            currentButton.setBackground(style.getLocationColor());
            currentButton.setBorder(BorderFactory.createEmptyBorder());

            mapPanel.add(currentButton);
            locationButtonList.add(currentButton);
            currentButton.setVisible(true);
        }

        positionButtons();
        repaint();
    }

    private void updateNodeVisibility() {
        for (LocationButton button: locationButtonList) {
            Location loc = button.getAssociatedLocation();

            if (style.isDrawAllPoints()) {
                button.setVisible(true);
            } else {
                button.setVisible(false);
            }

            if (style.isDrawNamedPoints() && loc.getNameList().length > 0) {
                button.setVisible(true);
                button.setToolTipText(loc.getNameList()[0]);
            }
            button.repaint();
        }
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

            for (List<Location> route: routeLists) {
                if (route.contains(locButton.getAssociatedLocation())) {
                    locButton.setBackground(style.getRouteLocationColor());
                }
            }
        }
    }

    public MapViewStyle getStyle() {
        return style;
    }

}
