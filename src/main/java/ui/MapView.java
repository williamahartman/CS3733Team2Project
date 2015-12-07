package ui;

import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;
import core.Edge;
import core.Location;
import core.LocationGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed below.
 * these edges.
 */
public class MapView extends JPanel {
    private static final double DEFAULT_ZOOM = 4;
    private static final double START_XFRAC = 0.2;
    private static final double START_YFRAC = 0.2;
    private static final double MINIMUM_ZOOM = 2;
    private static final double MAXIMUM_ZOOM = 50;
    private static final double ZOOM_SPEED = 0.25;
    private static final int NODE_BUTTON_SIZE = 7;
    private static final int NODE_BUTTON_SIZE_NAME = 12;
    private static final int NODE_BUTTON_SIZE_END = 15;
    //TODO make un-named points bigger in edit mode

    private JScrollPane scrollPane;
    private JPanel mapPanel;
    private double zoomFactor;

    //TODO locationList is redundant with locationButtonList
    private List<Edge> graphEdgeList; //The list of edges from the represented graph
    private List<Location> locationList; //The list of locations from the represented graph
    private List<LocationButton> locationButtonList;
    private List<List<Location>> routeLists;
    private List<Location> searchList;

    private MapViewStyle style;

    private String[] floorsImagePaths;
    private int currentFloorNumber;

    private SVGUniverse universe;
    private SVGIcon svg;
    private int svgWidth;
    private int svgHeight;

    private EventListener buttonListener;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param floorImagePaths The image that will be used as the background
     * @param defaultFloor The floor the be the main floor
     * @param viewStyle The viewStyle used by the mapView
     */
    public MapView(LocationGraph graph, String[] floorImagePaths, int defaultFloor, MapViewStyle viewStyle) {
        this.floorsImagePaths = floorImagePaths;
        this.currentFloorNumber = defaultFloor;
        this.style = viewStyle;
        this.searchList = new ArrayList<>();
        this.routeLists = new ArrayList<>();
        this.locationButtonList = new ArrayList<>();
        this.universe = new SVGUniverse();
        this.zoomFactor = DEFAULT_ZOOM;

        svg = new SVGIcon();
        setCurrentImage();

        //Make the panel
        mapPanel = new JPanel(true) {
            @Override
            public Dimension getPreferredSize() {
                return getCurrentPixelSize();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!(g instanceof Graphics2D)) {
                    throw new RuntimeException("The supplied graphics object is not a Graphics2D!");
                }

                Graphics2D g2d = (Graphics2D) g;
                RenderingHints rh = new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHints(rh);

                //Draw the current svg background image
                g2d.scale(zoomFactor, zoomFactor);
                svg.paintIcon(null, g2d, 0, 0);
                g2d.scale(1 / zoomFactor, 1 / zoomFactor);

                Dimension imageRes = getCurrentPixelSize();

                //Draw edges
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

                //Draw routes
                if (style.isDrawRoutes()) {
                    for (List<Location> route: routeLists) {
                        //TODO previousX and Y are redundant
                        Location previousLoc = route.get(0);
                        int previousX = (int) (route.get(0).getPosition().x * imageRes.getWidth());
                        int previousY = (int) (route.get(0).getPosition().y * imageRes.getHeight());

                        for (int i = 1; i < route.size(); i++) {
                            Location currentLoc = route.get(i);
                            int currentX = (int) (route.get(i).getPosition().x * imageRes.getWidth());
                            int currentY = (int) (route.get(i).getPosition().y * imageRes.getHeight());

                            if (previousLoc.getFloorNumber() == currentFloorNumber
                                    && previousLoc.getFloorNumber() == currentLoc.getFloorNumber()) {
                                g2d.setColor(style.getRouteColor());
                                g2d.drawLine(previousX, previousY, currentX, currentY);
                            }

                            previousX = currentX;
                            previousY = currentY;
                            previousLoc = currentLoc;
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
        mapPanel.setLayout(null);

        //Set up the scroll panel
        scrollPane = new JScrollPane();
        scrollPane.setWheelScrollingEnabled(false);
        scrollPane.setViewportView(mapPanel);
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
                updateButtonAttributes();

            }
        });
        floorSlider.setToolTipText("Change the displayed floor.");
        floorSlider.setPreferredSize(new Dimension(50, 500));
        JPanel floorSliderPanel = new JPanel();
        floorSliderPanel.add(floorSlider);

        setLayout(new BorderLayout());
        add(scrollPane);
        add(floorSliderPanel, BorderLayout.WEST);

        //Scroll to start point
        Point newViewportPos = new Point();
        newViewportPos.x = (int) ((START_XFRAC * getCurrentPixelSize().getWidth()));
        newViewportPos.y = (int) ((START_YFRAC * getCurrentPixelSize().getHeight()));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));
        zoomIncrementBy(0);

        updateGraph(graph);
        repaint();
    }

    /**
     * Sets the postion and zoom of the scroll panel
     */
    public void setPosAndZoom(){
        Iterator<List<Location>> routes = routeLists.iterator();
        double zoomw = 0;
        double zoomh = 0;
        double widthMax = 1.1;
        double heightMax = 1.1;
        double widthMin = 1.1;
        double heightMin = 1.1;
        //iterate through the enter 2D list
        //and find the max and min of the
        while (routes.hasNext()){
            List<Location> locList = routes.next();
            Iterator<Location> locs = locList.iterator();

            while (locs.hasNext()){
                Location curLoc = locs.next();
                double h = curLoc.getPosition().getY();
                double w = curLoc.getPosition().getX();

                if (currentFloorNumber == curLoc.getFloorNumber()) {
                    if (w > widthMax) {
                        widthMax = w;
                    }
                    if (w < widthMin) {
                        widthMin = w;
                    }
                    if (h > heightMax) {
                        heightMax = h;
                    }
                    if (h < heightMin) {
                        heightMin = h;
                    }
                }
            }
        }
        zoomFactor = DEFAULT_ZOOM;
        validate();
        double pixelWMin = widthMin * getCurrentPixelSize().getWidth();
        double pixelHMin = heightMin * getCurrentPixelSize().getHeight();
        double pixelWMax = widthMax * getCurrentPixelSize().getWidth();
        double pixelHMax = heightMax * getCurrentPixelSize().getHeight();
        Rectangle rect = mapPanel.getVisibleRect();
        double paneHeight = rect.getHeight();
        double paneWidth = rect.getWidth();

        zoomw = 2.5 * (paneWidth + getCurrentPixelSize().getWidth()) / (pixelWMax - pixelWMin);
        zoomh = 2.5 * (paneHeight + getCurrentPixelSize().getHeight()) / (pixelHMax - pixelHMin);
        double zoom = 0;
        if (zoomh > zoomw){
            zoom = zoomw;
        } else {
            zoom = zoomh;
        }

        System.out.println("Zoom factor: " + zoom + "\npixelWidth: " + paneWidth);
        Point newViewportPos = new Point();
        zoomFactor = zoom;
        validate();
        newViewportPos.x = (int) ((pixelWMin - 250));
        newViewportPos.y = (int) ((pixelHMin - 150));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));
        updateButtonAttributes();
    }

    private void setCurrentImage() {
        if (currentFloorNumber >= 0 && currentFloorNumber < floorsImagePaths.length) {
            String path = floorsImagePaths[currentFloorNumber];

                try {
                universe.loadSVG(ClassLoader.getSystemResourceAsStream(path), "bg" + currentFloorNumber);
                svg.setSvgURI(ClassLoader.getSystemResource(path).toURI());
                svg.setAntiAlias(true);
                svg.setClipToViewbox(false);
                svg.setAutosize(SVGIcon.AUTOSIZE_STRETCH);

                svgWidth = svg.getIconWidth();
                svgHeight = svg.getIconHeight();

                svg.setPreferredSize(new Dimension(svgWidth, svgHeight));
            } catch (Exception e) {
                e.printStackTrace();
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

                    double imageWidth = getCurrentPixelSize().getWidth();
                    double imageHeight = getCurrentPixelSize().getHeight();

                    double xPosFrac = (viewPortPos.getX() + (viewportWidth / 2.0)) / imageWidth;
                    double yPosFrac = (viewPortPos.getY() + (viewportHeight / 2.0)) / imageHeight;

                    zoomIncrementBy(e.getWheelRotation() * -ZOOM_SPEED);
                    validate();
                    updateButtonAttributes();

                    Point newViewportPos = new Point();
                    newViewportPos.x = (int) ((xPosFrac * getCurrentPixelSize().getWidth()) - (viewportWidth / 2.0));
                    newViewportPos.y = (int) ((yPosFrac * getCurrentPixelSize().getHeight()) - (viewportHeight / 2.0));

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
     * Adjust the zoom value by adding the passed value.
     *
     * @param toAdd the value to add
     */
    public void zoomIncrementBy(double toAdd) {
        zoomFactor += toAdd;
        validate();
    }

    /**
     * Returns a Dimension that describes the resolution of the background image. Or 0 if the image
     * failed to load
     *
     * @return The dimension of the image, or 0 if the image didn't load
     */
    public Dimension getCurrentPixelSize() {
        return new Dimension(
                (int) (svgWidth * zoomFactor),
                (int) (svgHeight * zoomFactor));
    }

    /**
     * Add a list of locations that will be displayed as search results.
     *
     * @param locToAdd the list of locations that will be added
     */
    public void addToSearchList(List<Location> locToAdd){
        for (Location loc: locToAdd){
            searchList.add(loc);
            locationButtonList.forEach(locationButton -> {
                if (loc.equals(locationButton.getAssociatedLocation())){
                    //TODO make a style attribute for this
                    locationButton.setBgColor(Color.BLACK);
                }
            });
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
     * Adds one LocationButton to the panel for each Location in the backing Location graph.
     */
    private void addButtons() {
        mapPanel.removeAll();
        locationButtonList.clear();

        for (Location loc: locationList) {
            LocationButton currentButton = new LocationButton(loc, getStyle().getLocationColor());
            mapPanel.add(currentButton);
            locationButtonList.add(currentButton);
            currentButton.setVisible(true);
        }

        if (buttonListener != null) {
            for (LocationButton locButton: locationButtonList) {
                if (buttonListener instanceof ActionListener) {
                    locButton.addActionListener((ActionListener) buttonListener);
                } else if (buttonListener instanceof MouseListener) {
                    locButton.addMouseListener((MouseListener) buttonListener);
                } else {
                    System.err.println("Unsupported listener type " + buttonListener.getClass());
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
            if (locButton.getAssociatedLocation().getNameList().length == 0)
            {
                int xPos = (int) (locButton.getAssociatedLocation().getPosition().x * getCurrentPixelSize().width);
                int yPos = (int) (locButton.getAssociatedLocation().getPosition().y * getCurrentPixelSize().height);
                locButton.setBounds(xPos - (NODE_BUTTON_SIZE / 2), yPos - (NODE_BUTTON_SIZE / 2),
                        NODE_BUTTON_SIZE, NODE_BUTTON_SIZE);
            } else {
                int xPos = (int) (locButton.getAssociatedLocation().getPosition().x * getCurrentPixelSize().width);
                int yPos = (int) (locButton.getAssociatedLocation().getPosition().y * getCurrentPixelSize().height);
                locButton.setBounds(xPos - (NODE_BUTTON_SIZE_NAME / 2), yPos - (NODE_BUTTON_SIZE_NAME / 2),
                        NODE_BUTTON_SIZE_NAME, NODE_BUTTON_SIZE_NAME);
            }
            for (List<Location> route: routeLists) {
                if (route.contains(locButton.getAssociatedLocation())) {
                    locButton.setBgColor(style.getRouteLocationColor());
                }
                if (locButton.getAssociatedLocation() == route.get(0)) {
                    setToStartOrEnd(locButton, style.getDestinationColor(), "START");
                }
                if (locButton.getAssociatedLocation() == route.get(route.size() - 1)) {
                    setToStartOrEnd(locButton, style.getDestinationColor(), "END");
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
    //Make the passed button even bigger
    private void setToStartOrEnd(LocationButton locationButton, Color color, String tooltip) {
        int xPos = (int) (locationButton.getAssociatedLocation().getPosition().x * getCurrentPixelSize().width);
        int yPos = (int) (locationButton.getAssociatedLocation().getPosition().y * getCurrentPixelSize().height);
        locationButton.setBounds(xPos - (NODE_BUTTON_SIZE_END / 2), yPos - (NODE_BUTTON_SIZE_END / 2),
                NODE_BUTTON_SIZE_END, NODE_BUTTON_SIZE_END);
        locationButton.setBgColor(color);
        locationButton.setToolTipText(tooltip);
    }

    /**
     * Sets the listener that will be associated with all buttons in the MapView.
     *
     * @param buttonListener The listener that will be associated
     */
    public void setButtonListener(EventListener buttonListener) {
        this.buttonListener = buttonListener;
        addButtons();
    }

    /**
     * Return the MapStyle associated with this MapView.
     *
     * @return The associated MapStyle
     */
    public MapViewStyle getStyle() {
        return style;
    }

    /**
     * Return the floor currently being viewed.
     *
     * @return The currently viewed floor
     */
    public int getFloorNumber() {
        return currentFloorNumber;
    }
}
