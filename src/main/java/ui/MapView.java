package ui;

import com.kitfox.svg.app.beans.SVGIcon;
import core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.*;
import java.util.List;

/**
 * This is a panel that displays edges from a map. An image background is displayed below
 * these edges.
 */
public class MapView extends JPanel {
    private static final double DEFAULT_ZOOM = 4;
    private static final double START_XFRAC = 0.2;
    private static final double START_YFRAC = 0.2;
    private static final double MINIMUM_ZOOM = 2;
    private static final double MAXIMUM_ZOOM = 50;
    private static final double ZOOM_SPEED = 0.25;

    private JScrollPane scrollPane;
    private JPanel mapPanel;
    private JSlider floorSlider;
    private double zoomFactor;

    private LocationGraph graph;
    private List<Edge> edgeHighlightList;
    private List<LocationButton> locationButtonList;
    private List<List<Location>> routeLists;
    private List<Location> searchList;

    private MapViewStyle style;

    private int currentFloorNumber;
    private int defaultFloorNumber;

    private HashMap<Integer, MapImage> svgList;
    private SVGIcon svg;
    private int svgWidth;
    private int svgHeight;

    private EventListener buttonListener;
    private JPanel floorSliderPanel = new JPanel(new BorderLayout());

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param maps The image that will be used as the background
     * @param defaultFloor The floor the be the main floor
     * @param viewStyle The viewStyle used by the mapView
     */
    public MapView(LocationGraph graph, HashMap<Integer, MapImage> maps, int defaultFloor, MapViewStyle viewStyle) {
        this.graph = graph;
        this.currentFloorNumber = defaultFloor;
        this.defaultFloorNumber = defaultFloor;
        this.style = viewStyle;
        this.searchList = new ArrayList<>();
        this.routeLists = new ArrayList<>();
        this.edgeHighlightList = new ArrayList<>();
        this.locationButtonList = new ArrayList<>();
        this.zoomFactor = DEFAULT_ZOOM;
        this.svgList = maps;

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
                List<Edge> graphEdgeList = graph.edgeByFloorNumber(currentFloorNumber);
                if (style.isDrawAllEdges()) {
                    for (Edge e : graphEdgeList) {
                        g2d.setColor(style.getEdgeColor());

                        if (edgeHighlightList.contains(e)) {
                            g2d.setColor(style.getEdgeHighlightColor());
                        }

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
                        if (loc.getFloorNumber() == currentFloorNumber) {
                            g2d.setColor(style.getStartPointColor());
                            double halfButtonSize = style.getNamedButtonSize() / 2.0;
                            int locX = (int) (loc.getPosition().x * imageRes.getWidth());
                            int locY = (int) ((loc.getPosition().y * imageRes.getHeight()) - halfButtonSize - 3);
                            g2d.drawLine(locX, locY, locX, locY - 30);
                            g2d.drawLine(locX, locY, locX - 10, locY - 10);
                            g2d.drawLine(locX, locY, locX + 10, locY - 10);
                        }
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
        this.floorSlider = new JSlider(JSlider.VERTICAL);
        floorSlider.setMinimum(0);
        floorSlider.setMaximum(svgList.size() - 1);
        floorSlider.setValue(currentFloorNumber);
        floorSlider.setPaintTicks(true);
        floorSlider.setMajorTickSpacing(1);
        floorSlider.addChangeListener(e ->  {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                currentFloorNumber = source.getValue();
                setCurrentImage();
                refreshGraph();
            }
        });
        floorSlider.setToolTipText("Change the displayed floor.");
        floorSlider.setPreferredSize(new Dimension(50, 500));

        floorSliderPanel.add(floorSlider);
        floorSlider.update(getGraphics());

        setLayout(new BorderLayout());
        add(scrollPane);
        add(floorSliderPanel, BorderLayout.WEST);

        JButton returnToDefaultFloor = new JButton("<html>Return to<br>" +
                "default<br>" +
                "floor");
        returnToDefaultFloor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                floorSlider.setValue(defaultFloor);
            }
        });
        floorSliderPanel.add(returnToDefaultFloor, BorderLayout.PAGE_END);

        //Scroll to start point
        Point newViewportPos = new Point();
        newViewportPos.x = (int) ((START_XFRAC * getCurrentPixelSize().getWidth()));
        newViewportPos.y = (int) ((START_YFRAC * getCurrentPixelSize().getHeight()));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));
        zoomIncrementBy(0);

        refreshGraph();
    }

    /**
     * Sets the postion and zoom of the scroll panel.
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
        Point newViewportPos = new Point();
        zoomFactor = zoom;
        validate();
        newViewportPos.x = (int) ((pixelWMin - 250));
        newViewportPos.y = (int) ((pixelHMin - 150));
        mapPanel.scrollRectToVisible(new Rectangle(newViewportPos, scrollPane.getViewport().getSize()));
        updateButtonAttributes();
    }

    private void setCurrentImage() {
        if (currentFloorNumber >= 0 && currentFloorNumber < svgList.size()) {
            svg = svgList.get(currentFloorNumber).getSVG();
            svgWidth = svg.getIconWidth();
            svgHeight = svg.getIconHeight();
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
                    refreshGraph();

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
            if (loc.getFloorNumber() == currentFloorNumber) {
                searchList.add(loc);
            }
            locationButtonList.forEach(locationButton -> {
                if (loc.equals(locationButton.getAssociatedLocation())){
                    locationButton.setBgColor(style.getSearchResultColor());
                }
            });
        }
    }
    public void clearSearchList()
    {
        searchList.clear();
    }
    public void clearRoutes() {
        for (List<Location> locList: routeLists) {
            locList.clear();
        }
        routeLists.clear();
    }
    public void clearHighlight()
    {
        edgeHighlightList.clear();
    }


    public void addToHighlightList(Edge e) {
        edgeHighlightList.add(e);
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

        List<Location> locationList = graph.getAllLocations();
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
                } else if (buttonListener instanceof MouseAdapter) {
                    locButton.addMouseListener((MouseAdapter) buttonListener);
                    locButton.addMouseMotionListener((MouseAdapter) buttonListener);
                    locButton.addMouseWheelListener((MouseAdapter) buttonListener);
                } else {
                    System.err.println("Unsupported listener type " + buttonListener.getClass() +
                            ". Add a case for this type in MapView.java");
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
    public void setGraph(LocationGraph graph) {
        this.graph = graph;
        this.routeLists = new ArrayList<>();
        this.searchList = new ArrayList<>();
        this.edgeHighlightList = new ArrayList<>();

        addButtons();
        updateButtonAttributes();
        setCurrentImage();
        repaint();
    }

    public void refreshGraph() {
        updateButtonAttributes();
        repaint();
    }

    public void setSvgList(HashMap<Integer, MapImage> svgList) {
        this.svgList = svgList;

        //Update the floor slider
        floorSlider.setMinimum(0);
        floorSlider.setMaximum(svgList.size() - 1);

        //If floors were deleted (the list got smaller) and the old current floor number is gone,
        //move down to a floor that exists.
        if (currentFloorNumber > svgList.size() - 1) {
            currentFloorNumber = svgList.size() - 1;
            floorSlider.setValue(currentFloorNumber);
        }
    }

    private void updateButtonAttributes() {
        for (LocationButton locButton: locationButtonList) {
            Location loc = locButton.getAssociatedLocation();

            //Set the size, based on whether or not there is a name
            if (loc.getNameList().length == 0)
            {
                int xPos = (int) (loc.getPosition().x * getCurrentPixelSize().width);
                int yPos = (int) (loc.getPosition().y * getCurrentPixelSize().height);

                int buttonSize = (int) style.getUnnamedButtonSize();
                locButton.setBounds(xPos - (buttonSize / 2), yPos - (buttonSize / 2), buttonSize, buttonSize);
            } else {
                int buttonSize = (int) style.getNamedButtonSize();
                if (loc.getEdges() != null){
                    for (Edge e:loc.getEdges()){
                        if (!e.hasAttribute(EdgeAttribute.INDOORS)){
                            buttonSize = (int) style.getNamedButtonSize();
                            break;
                        } else {
                            buttonSize = (int) style.getUnnamedButtonSize() + 2;
                        }
                    }
                }
                int xPos = (int) (loc.getPosition().x * getCurrentPixelSize().width);
                int yPos = (int) (loc.getPosition().y * getCurrentPixelSize().height);

                locButton.setBounds(xPos - (buttonSize / 2), yPos - (buttonSize / 2), buttonSize, buttonSize);
            }

            //Set colors
            locButton.setBgColor(style.getLocationColor());
            //Highlight routes
            for (List<Location> route: routeLists) {
                if (route.contains(loc)) {
                    locButton.setBgColor(style.getRouteLocationColor());
                }
                if (loc == route.get(0)) {
                    setToStartOrEnd(locButton, style.getStartPointColor(), "START",
                            (int) style.getStartOrEndButtonSize());
                }
                if (loc == route.get(route.size() - 1)) {
                    setToStartOrEnd(locButton, style.getEndPointColor(), "END",
                            (int) style.getStartOrEndButtonSize());
                }
            }
            //Highlight search results
            if (searchList.contains(loc)) {
                locButton.setBgColor(style.getSearchResultColor());
            }
            //Highlight highlight Edge buttons
            for (Edge e: edgeHighlightList) {
                if (e.getNode1() == loc || e.getNode2() == loc) {
                    locButton.setBgColor(style.getEdgeHighlightColor());
                }
            }

            //Set visibility
            boolean locOnCurrentFloor = loc.getFloorNumber() == currentFloorNumber;
            if (style.isDrawAllPoints() && locOnCurrentFloor) {
                locButton.setVisible(true);
            } else {
                locButton.setVisible(false);
            }

            if (style.isDrawNamedPoints() && loc.getNameList().length > 0 && locOnCurrentFloor) {
                locButton.setVisible(true);
                String toolTip = "<html><p>" + loc.getNameList()[0] + "</p>";

                //add image if the image attribute of the location isn't null or blank
                if (loc.getImagePath() != null && loc.getImagePath().length() != 0) {
                    toolTip = toolTip + "<img src=\"" + loc.getImagePath() + "\">";
                }
                locButton.setToolTipText(toolTip);
            }
        }
    }
    //Make the passed button even bigger
    private void setToStartOrEnd(LocationButton locationButton, Color color, String tooltip, int size) {
        int xPos = (int) (locationButton.getAssociatedLocation().getPosition().x * getCurrentPixelSize().width);
        int yPos = (int) (locationButton.getAssociatedLocation().getPosition().y * getCurrentPixelSize().height);

        locationButton.setBounds(xPos - (size / 2), yPos - (size / 2), size, size);
        locationButton.setBgColor(color);
        locationButton.setToolTipText(tooltip);
    }


    public String stepByStepHM(LinkedHashMap<StartEnd, String> locMap,
                               Location prevStart, Location prevEnd,
                               Location current, Location next, boolean emailMode) {
        String textStep = "";
        int emailCount = 0;
        StartEnd pairPrev = new StartEnd(prevStart, prevEnd);
        StartEnd pair = new StartEnd(current, next);

        //System.out.println("In step by step");

        if (locMap.containsKey(pair)) {
            // Get text
            String directions = locMap.get(pair);
            textStep = directions;
            Location cur = pair.getStart(); // Get current location

            if (pair.getStart().getFloorNumber() != currentFloorNumber) {
                System.out.println("Should change floors");
                currentFloorNumber = current.getFloorNumber();
                System.out.println("Floor: " + currentFloorNumber);
                floorSlider.setValue(currentFloorNumber);
                repaint();

                List<List<Location>> backUpList = routeLists;
                setCurrentImage();
                //updateGraph();
                routeLists = backUpList;

                updateButtonAttributes();
                repaint();
                setPosAndZoom();
            }
            if (prevStart == current) {
                //System.out.println("First time through - step count is 0");
                setPosAndZoom();

                System.out.println("Previous = current");

                for (LocationButton locButton : locationButtonList) {
                    if (locButton.getAssociatedLocation().equals(current)) {
                        System.out.println("Set current with arrow");
                        locButton.setBgColor(new Color(250, 118, 0));
                        searchList.add(locButton.getAssociatedLocation());
                        repaint();
                    }
                }

                ImageFromMap img = new ImageFromMap();
                img.saveComponentAsJPEG(this, "image" + emailCount + ".jpeg");
                emailCount++;
            } else if (current == next) {
                for (LocationButton locButton : locationButtonList) {
                    if (locButton.getAssociatedLocation().equals(current)) {
                        System.out.println("Set current with arrow");
                        locButton.setBgColor(new Color(250, 118, 0));
                        searchList.add(locButton.getAssociatedLocation());
                        repaint();
                    }
                }
            } else if (pairPrev.getStart().getFloorNumber() != pair.getEnd().getFloorNumber()) {
                System.out.println("Should change floors");
                currentFloorNumber = current.getFloorNumber();
                System.out.println("Floor: " + currentFloorNumber);
                floorSlider.setValue(currentFloorNumber);
                repaint();

                List<List<Location>> backUpList = routeLists;
                setCurrentImage();
                //updateGraph(graph);
                routeLists = backUpList;

                updateButtonAttributes();
                repaint();

                if (emailMode) {
                    ImageFromMap img = new ImageFromMap();
                    img.saveComponentAsJPEG(this, "image" + emailCount + ".jpeg");
                    emailCount++;
                }
            } else {

            }
            //System.out.println("Should happen in second call");
            for (LocationButton locButton : locationButtonList) {
                if (locButton.getAssociatedLocation().equals(current)) {
                    locButton.setBgColor(new Color(250, 118, 0));
                    searchList.add(locButton.getAssociatedLocation());
                    repaint();
                }
                if (locButton.getAssociatedLocation().equals(pairPrev.getStart())) {
                    System.out.println("Remove from search list");
                    locButton.setBgColor(style.getRouteLocationColor());
                    searchList.remove(locButton.getAssociatedLocation());
                    repaint();
                }
            }
        }

        return textStep;
    }

    public String directionClick(LinkedHashMap<StartEnd, String> locMap,
                               Location current, Location next, String str) {
        String textStep = "";
        int emailCount = 0;
        StartEnd pair = new StartEnd(current, next);

        if (locMap.containsKey(pair)) {
            // Get text
            String directions = locMap.get(pair);
            textStep = directions;

            Location cur = pair.getStart(); // Get current location

            if (pair.getStart().getFloorNumber() != currentFloorNumber) {
                System.out.println("Should change floors");
                currentFloorNumber = current.getFloorNumber();
                System.out.println("Floor: " + currentFloorNumber);
                floorSlider.setValue(currentFloorNumber);
                repaint();

                List<List<Location>> backUpList = routeLists;
                setCurrentImage();
                //updateGraph(graph);
                routeLists = backUpList;

                updateButtonAttributes();
                repaint();
                setPosAndZoom();
            }

            if (textStep.contains(str)) {
                for (LocationButton locButton : locationButtonList) {
                    if (locButton.getAssociatedLocation().equals(current)) {
                        System.out.println("Set current with arrow");
                        locButton.setBgColor(new Color(250, 118, 0));
                        searchList.add(locButton.getAssociatedLocation());
                        repaint();
                    }
                }
            }
        }

        return textStep;
    }

    public String stepByStep(int step, boolean way, boolean emailMode)
    {
        String textStep = "";
       for (List<Location> ll:routeLists) {
           if (step < ll.size()) {
               Location current = ll.get(step);
               Instruction instruct = new Instruction();
               int scaleX = getCurrentMapImage().getScaleX();
               int scaleY = getCurrentMapImage().getScaleY();
               textStep = instruct.stepByStepInstruction(ll, scaleX, scaleY).get(step)
                       + instruct.stepByStepInstruction(ll, scaleX, scaleY).get(step + 1);
               if (current.getFloorNumber() != currentFloorNumber)
               {
                   currentFloorNumber = current.getFloorNumber();
                   floorSlider.setValue(currentFloorNumber);
                   setFloor(current.getFloorNumber());

                   setCurrentImage();
                   refreshGraph();

                   updateButtonAttributes();
                   repaint();
                   setPosAndZoom();
                   if (emailMode) {
                       ImageFromMap img = new ImageFromMap();
                       img.saveComponentAsJPEG(this, "image" + step + ".jpeg");
                   }
               }
               if (step == 0) {
                   setPosAndZoom();
                   ImageFromMap img = new ImageFromMap();
                   img.saveComponentAsJPEG(this, "image" + step + ".jpeg");
               }
               if (step > 0) {
                   textStep = instruct.stepByStepInstruction(ll, scaleX, scaleY)
                           .get(step * 2)
                           + instruct.stepByStepInstruction(ll, scaleX, scaleY)
                           .get(step * 2 + 1);
                   Location previous;
                   if (way == true) {
                       previous = ll.get(step - 1);
                   }
                   else
                   {
                       previous = ll.get(step);
                       current = ll.get(step - 1);
                       textStep = instruct.stepByStepInstruction(ll, scaleX, scaleY)
                               .get((step - 1) * 2)
                               + instruct.stepByStepInstruction(ll, scaleX, scaleY)
                               .get((step - 1) * 2 + 1);
                   }
                   if (current.getFloorNumber() != previous.getFloorNumber())
                   {
                       setFloor(current.getFloorNumber());

                       setCurrentImage();
                       refreshGraph();
                   }
                   for (LocationButton locButton : locationButtonList) {
                       if (locButton.getAssociatedLocation().equals(current)) {
                           locButton.setBgColor(new Color(250, 118, 0));
                           searchList.add(locButton.getAssociatedLocation());
                           repaint();
                       }
                       if (locButton.getAssociatedLocation().equals(previous)) {
                           locButton.setBgColor(style.getRouteLocationColor());
                           searchList.remove(locButton.getAssociatedLocation());
                           repaint();
                       }
                   }
               } else {
                   for (LocationButton locButton : locationButtonList) {
                       if (way == true) {
                           if (locButton.getAssociatedLocation().equals(current)) {
                               locButton.setBgColor(new Color(250, 118, 0));
                               searchList.add(locButton.getAssociatedLocation());
                               repaint();
                           }
                       }
                       else {
                           if (locButton.getAssociatedLocation().equals(current)) {
                               locButton.setBgColor(new Color(250, 118, 0));
                               searchList.remove(locButton.getAssociatedLocation());
                               repaint();
                           }
                       }
                   }
               }
           }
       }
        return textStep;
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
     * Sets the style of the mapview.
     *
     * @param style the passed style
     */
    public void setStyle(MapViewStyle style) {
        this.style = style;

        addButtons();
        updateButtonAttributes();

        repaint();
    }

    /**
     * Return the floor currently being viewed.
     *
     * @return The currently viewed floor
     */
    public int getFloorNumber() {
        return currentFloorNumber;
    }

    public MapImage getCurrentMapImage(){
        return svgList.get(currentFloorNumber);
    }

    public HashMap<Integer, MapImage> getMapImages() {
        return svgList;
    }

    public int getDefaultFloorNumber() {
        return defaultFloorNumber;
    }

    public void setFloor(int currentFloorNumber) {
        this.currentFloorNumber = currentFloorNumber;
        floorSlider.setValue(currentFloorNumber);
        repaint();
    }

    public void setDefaultFloor(int defaultFloorNumber) {
        this.defaultFloorNumber = defaultFloorNumber;
    }

    public JPanel getFloorSliderPanel() { return floorSliderPanel; }
}
