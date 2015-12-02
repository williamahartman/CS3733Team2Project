package ui;

import core.Edge;
import core.Location;
import core.LocationGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed bellow
 * these edges.
 */
public class MapView extends JPanel {
    private static final double START_XFRAC = 0.38;
    private static final double START_YFRAC = 0.3;
    private static final int NODE_BUTTON_SIZE = 6;
    private static final double MINIMUM_ZOOM = 0.1;
    private static final double MAXIMUM_ZOOM = 2;

    private JScrollPane scrollPane;
    private JPanel mapPanel;
    private double zoomFactor;

    private List<Edge> graphEdgeList;
    private List<Location> locationList;
    private List<LocationButton> locationButtonList;
    private List<List<Location>> routeLists;
    private List<Location> searchList;

    private MapViewStyle style;

    private String[] floorsImagePaths;
    private int currentFloorNumber;

    private Image backgroundImage;

    private EventListener buttonListener;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param floorImagePaths The image that will be used as the background
     * @param defaultFloor The floor the be the main floor
     * @param defaultZoom The zoom level the map will be at when the app starts
     * @param viewStyle The viewStyle used by the mapView
     */
    public MapView(LocationGraph graph, String[] floorImagePaths, int defaultFloor, double defaultZoom,
                   MapViewStyle viewStyle) {
        //Make the panel
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
                        g2d.setColor(style.getEdgeColor(currentFloorNumber));

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

                        Location previousLoc = route.get(0);
                        int previousX = (int) (route.get(0).getPosition().x * imageRes.getWidth());
                        int previousY = (int) (route.get(0).getPosition().y * imageRes.getHeight());
                        for (int i = 1; i < route.size(); i++) {
                            Location currentLoc = route.get(i);
                            int currentX = (int) (route.get(i).getPosition().x * imageRes.getWidth());
                            int currentY = (int) (route.get(i).getPosition().y * imageRes.getHeight());

                            if (previousLoc.getFloorNumber() == currentFloorNumber) {
                                g2d.drawLine(previousX, previousY, currentX, currentY);
                            }

                            previousX = currentX;
                            previousY = currentY;
                        }
                    }
                }

                //Draw arrows on search results
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

        this.floorsImagePaths = floorImagePaths;
        this.currentFloorNumber = defaultFloor;
        setCurrentImage();

        this.style = viewStyle;
        this.searchList = new ArrayList<>();
        this.routeLists = new ArrayList<>();
        locationButtonList = new ArrayList<>();
        zoomFactor = defaultZoom;

        mapPanel.setPreferredSize(getImagePixelSize());
        mapPanel.setLayout(null);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(mapPanel);

        Point newViewportPos = new Point();
        newViewportPos.x = (int) ((START_XFRAC * getImagePixelSize().getWidth()));
        newViewportPos.y = (int) ((START_YFRAC * getImagePixelSize().getHeight()));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));

        updateGraph(graph);
        addDefaultListeners();

        //Add the slider
        JSlider floorSlider = new JSlider(JSlider.VERTICAL);
        floorSlider.setMinimum(0);
        floorSlider.setMaximum(floorImagePaths.length - 1);
        floorSlider.setValue(defaultFloor);
        floorSlider.setPaintTicks(true);
        floorSlider.setMajorTickSpacing(1);
        floorSlider.addChangeListener(e ->  {
            JSlider source = (JSlider) e.getSource();

            if (!source.getValueIsAdjusting()) {
                currentFloorNumber = source.getValue();

                List<List<Location>> backUpList = routeLists;
                setCurrentImage();
                updateGraph(graph);
                routeLists = backUpList;
            }
        });
        floorSlider.setToolTipText("Change the displayed floor.");
        floorSlider.setPreferredSize(new Dimension(50, 500));
        JPanel floorSliderPanel = new JPanel();
        floorSliderPanel.add(floorSlider);

        setLayout(new BorderLayout());
        add(scrollPane);
        add(floorSliderPanel, BorderLayout.WEST);
    }

    /**
     * Add a list of locations that will be displayed as search results.
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
        updateButtonAttributes();
    }

    private void setCurrentImage() {
        if (currentFloorNumber >= 0 && currentFloorNumber < floorsImagePaths.length) {
            String path = floorsImagePaths[currentFloorNumber];
            Image oldImage = backgroundImage;

            try {
                backgroundImage = ImageIO.read(ClassLoader.getSystemResourceAsStream(path));
                oldImage = null;
            } catch (Exception e) {
                //Close the program with an error message if we can't load stuff.
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "The map image failed to load!",
                        "Asset Load Failed!",
                        JOptionPane.ERROR_MESSAGE);
                backgroundImage = oldImage;
            }
        }
    }

    private void addDefaultListeners() {
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            int mouseStartX = 0;
            int mouseStartY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                Point vpp = scrollPane.getViewport().getViewPosition();

                vpp.translate(mouseStartX - e.getXOnScreen(), mouseStartY - e.getYOnScreen());
                mapPanel.scrollRectToVisible(new Rectangle(vpp, scrollPane.getViewport().getSize()));

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

                    Point viewPortPos = scrollPane.getViewport().getViewPosition();

                    double viewportWidth = scrollPane.getViewport().getWidth();
                    double viewportHeight = scrollPane.getViewport().getHeight();

                    double imageWidth = getImagePixelSize().getWidth();
                    double imageHeight = getImagePixelSize().getHeight();

                    double xPosFrac = (viewPortPos.getX() + (viewportWidth / 2.0)) / imageWidth;
                    double yPosFrac = (viewPortPos.getY() + (viewportHeight / 2.0)) / imageHeight;

                    zoomIncrementBy(e.getWheelRotation() * -0.04);
                    updateButtonAttributes();
                    validate();

                    Point newViewportPos = new Point();
                    newViewportPos.x = (int) ((xPosFrac * getImagePixelSize().getWidth()) - (viewportWidth / 2.0));
                    newViewportPos.y = (int) ((yPosFrac * getImagePixelSize().getHeight()) - (viewportHeight / 2.0));

                    mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));

                    repaint();
                }
            }
        };
        scrollPane.addMouseListener(mouseAdapter);
        scrollPane.addMouseMotionListener(mouseAdapter);
        scrollPane.addMouseWheelListener(mouseAdapter);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * @return The backing JPanel
     */
    public JPanel getMapPanel() {
        return mapPanel;
    }

    /**
     * Return the backing JScrollPane for this MapView. This is what moves around
     *
     * @return The backing JScrollPane
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Returns a Dimension that describes the resolution of the background image. Or 0 if the image
     * failed to load
     *
     * @return The dimension of the image, or 0 if the image didn't load
     */
    public Dimension getImagePixelSize() {
        return new Dimension(
                (int) (backgroundImage.getWidth(null) * zoomFactor),
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
        mapPanel.removeAll();
        locationButtonList.clear();

        for (Location loc: locationList) {
            LocationButton currentButton = new LocationButton(loc);
            currentButton.setBackground(style.getLocationColor());
            currentButton.setBorder(BorderFactory.createEmptyBorder());

            mapPanel.add(currentButton);
            locationButtonList.add(currentButton);
            currentButton.setVisible(true);
        }

        if (buttonListener != null) {
            for (LocationButton locButton: locationButtonList) {
                if (buttonListener instanceof ActionListener) {
                    locButton.addActionListener((ActionListener) buttonListener);
                }
                if (buttonListener instanceof MouseListener) {
                    locButton.addMouseListener((MouseListener) buttonListener);
                }
            }
        }

        updateButtonAttributes();
        repaint();
    }

    /**
     * Reset the map to display the edges of the passed graph.
     * Calling the method will remove and re-add all buttons, so
     * listeners will also need to be re-added
     *
     * @param graph The graph whose edges will be displayed
     */
    public final void updateGraph(LocationGraph graph) {
        this.graphEdgeList = graph.edgeByFloorNumber(currentFloorNumber);
        this.locationList = graph.locationByFloorNumber(currentFloorNumber);
        this.routeLists = new ArrayList<>();
        this.searchList = new ArrayList<>();

        addButtons();
        updateButtonAttributes();
    }

    private void updateButtonAttributes() {
        for (LocationButton locButton: locationButtonList) {

            int xPos = (int) (locButton.getAssociatedLocation().getPosition().x * getImagePixelSize().width);
            int yPos = (int) (locButton.getAssociatedLocation().getPosition().y * getImagePixelSize().height);
            locButton.setBounds(xPos - (NODE_BUTTON_SIZE / 2), yPos  - (NODE_BUTTON_SIZE / 2),
                    NODE_BUTTON_SIZE, NODE_BUTTON_SIZE);
            locButton.setBorder(BorderFactory.createLineBorder(new Color(250, 250, 250)));
            locButton.setBorderPainted(true);

            for (List<Location> route: routeLists) {
                locButton.setBackground(style.getLocationColor());

                if (route.contains(locButton.getAssociatedLocation())) {
                    locButton.setBackground(style.getRouteLocationColor());
                }
            }

            Location loc = locButton.getAssociatedLocation();
            if (style.isDrawAllPoints()) {
                locButton.setVisible(true);
            } else {
                locButton.setVisible(false);
            }
            if (style.isDrawNamedPoints() && loc.getNameList().length > 0) {
                locButton.setVisible(true);
                locButton.setToolTipText(loc.getNameList()[0]);
            }
            repaint();
        }
    }

    public void setButtonListener(EventListener buttonListener) {
        this.buttonListener = buttonListener;
        addButtons();
    }

    public MapViewStyle getStyle() {
        return style;
    }

    public int getFloorNumber() {
        return currentFloorNumber;
    }
}
