package ui;

import core.EdgeAttributeManager;
import core.Location;
import core.LocationGraph;
import dev.DevPassword;
import dev.DevTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This class will build a frame that is pre-populated panels and buttons.
 * This runs the app.
 */
public class MainAppUI extends JFrame{
    private static final String FRAME_TITLE = "AZTEC WASH Mapper";
    private static final int SIDEPANEL_WIDTH = 250;

    //Button sized
    private static final int UNNAMED_SIZE = 7;
    private static final int NAMED_SIZE = 12;
    private static final int START_SIZE = 15;
    private static final int DEV_UNNAMED_SIZE = 10;
    private static final int DEV_NAMED_SIZE = 15;
    private static final int DEV_START_SIZE = 15;

    private MapView mapView;
    private MapViewStyle defaultMapViewStyle;
    private LocationGraph graph;

    private Location startPoint;
    private Location endPoint;
    private java.util.List<Location> route;

    private JLabel startInfo;
    private JLabel endPointInfo;
    private JTextArea routeInfo;

    private JTextArea gps;
    private JTextField searchText;
    private String locToSearch;

    //private JButton clearButton;
    private JButton makeAStarRoute;
    private JButton searchButton;

    private DevTools devToolsPanel;
    private MouseListener devToolClickListener;

    private EdgeAttributeManager attributeManager;
    private boolean userLoggedIn;

    private boolean drawEdges = false;
    private boolean drawAllLocations = false;

    //Save start and end colors as class data to help allow restoring when changing
    //in and out of devmode
    private Color oldStartColor;
    private Color oldEndColor;

    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     */
    public MainAppUI(LocationGraph graph) {
        super(FRAME_TITLE);
        this.graph = graph;

        defaultMapViewStyle = new MapViewStyle(
                false,  //Draw Edges
                false,  //Draw all points
                true,   //Draw named points
                true,   //Draw routes
                UNNAMED_SIZE,   //Unnamed button size
                NAMED_SIZE,     //Name button size
                START_SIZE,     //Start or end button size
                new Color(250, 120, 0),  //Edge color
                new Color(15, 78, 152),  //Route Edge color
                new Color(255, 240, 0),  //Location Color
                new Color(79, 189, 255), //Route Location color
                Color.RED,      //Start Point color
                Color.GREEN,    //End Point color
                Color.CYAN,     //Selected Point color
                Color.BLUE,     //Previously Selected Point color
                Color.CYAN,     //Edge highlight color
                Color.GRAY      //Search Result Color
        );

        this.mapView = new MapView(graph,
                new String[]{
                        "campusmap-3.svg",
                        "campusmap-2.svg",
                        "campusmap-1.svg",
                        "campusmap1.svg",
                        "campusmap2.svg",
                        "campusmap3.svg",
                        "campusmap4.svg",
                        "campusmap5.svg"},
                3, defaultMapViewStyle);
        this.mapView.setButtonListener(buildRouteSelectListener());
        this.attributeManager = new EdgeAttributeManager();

        startPoint = null;
        endPoint = null;
        route = new ArrayList<>();
    }

    /**
     * Populate the JFrame with panels used for the application.
     */
    public void setUpMainApp() {
        //Set up menubar
        JPanel sidePanel = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");

        JMenuItem refreshMap = new JMenuItem("Refresh Map");
        refreshMap.addActionListener(e -> resetMap(mapView));
        JMenuItem enterDeveloperMode = new JMenuItem("Edit Map (Developers Only!)");

        DevPassword passBox = new DevPassword("aztec", "wash");

        devToolsPanel = new DevTools(graph, mapView);
        devToolsPanel.setVisible(false);

        userLoggedIn = false;


        //Sets up the 'view' menu bar
        JMenu view = new JMenu("View");

        //'View' contains toggleEdges, showNodes, and changeStyle
        JMenuItem toggleEdges = new JMenuItem("Toggle Edges");
        JMenuItem showNodes = new JMenuItem("Toggle Showing All Locations");
        JMenu changeStyle = new JMenu("Change Style");

        //changeStyle contains defaultStyle, WPIStyle, monochromaticStyle
        JMenuItem defaultStyle = new JMenuItem("Default Style");
        JMenuItem aWPIStyle = new JMenuItem("WPI Style");
        JMenuItem blueStyle = new JMenuItem("Blue Style");
        JMenuItem neonFunkStyle = new JMenuItem("Neon Funk Style");
        JMenuItem vintageStyle = new JMenuItem("Vintage Style");
        JMenuItem colorBlindStyle = new JMenuItem("Colorblind Accessible Style");

        //Sets up menu hierarchy
        setJMenuBar(menuBar);
        menuBar.add(editMenu);
        menuBar.add(view);
        view.add(toggleEdges);
        view.add(showNodes);
        view.add(changeStyle);
        editMenu.add(refreshMap);
        editMenu.add(enterDeveloperMode);
        changeStyle.add(defaultStyle);
        changeStyle.add(aWPIStyle);
        changeStyle.add(blueStyle);
        changeStyle.add(neonFunkStyle);
        changeStyle.add(vintageStyle);
        changeStyle.add(colorBlindStyle);

        enterDeveloperMode.addActionListener(e -> {
            if (!devToolsPanel.getDevMode()) {
                devToolsPanel.reset();

                if (!userLoggedIn) {
                    int passResult = 0;
                    //Open the password sign-in
                    passResult = passBox.passwordBox();
                    if (passResult == 1) {
                        userLoggedIn = true;
                    } else if (passResult == 2) {
                        JOptionPane.showMessageDialog(null, "Error: incorrect credentials. Please try again",
                                "Incorrect!", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (userLoggedIn) {
                    devToolsPanel.setDevMode(true);
                    remove(sidePanel);
                    devToolsPanel.setVisible(true);
                    add(devToolsPanel, BorderLayout.WEST);
                    showNodes.setText("Show Only Named Locations");
                    enterDeveloperMode.setText("Exit Developer Mode");

                    //Save the old style
                    oldStartColor = mapView.getStyle().getStartPointColor();
                    oldEndColor = mapView.getStyle().getEndPointColor();

                    //Update default view things
                    drawAllLocations = true;
                    drawEdges = true;
                    mapView.getStyle().setDrawAllEdges(true);
                    mapView.getStyle().setDrawAllPoints(true);

                    //Update the styles
                    mapView.getStyle().setUnnamedButtonSize(DEV_UNNAMED_SIZE);
                    mapView.getStyle().setNamedButtonSize(DEV_NAMED_SIZE);
                    mapView.getStyle().setStartOrEndButtonSize(DEV_START_SIZE);
                    mapView.getStyle().setStartPointColor(mapView.getStyle().getSelectedPointColor());
                    mapView.getStyle().setEndPointColor(mapView.getStyle().getSelectedPointColor());

                    //Update mapView for use with devtools
                    clearState(mapView);
                    devToolClickListener = devToolsPanel.buildAddLocationListener(mapView.getMapPanel());
                    mapView.getScrollPane().addMouseListener(devToolClickListener);
                    mapView.setButtonListener(devToolsPanel.buildEditListener(graph));

                    repaint();
                }
            } else {
                //todo restore the default options
                devToolsPanel.setDevMode(false);
                enterDeveloperMode.setText("Edit Map (Developers Only!)");
                remove(devToolsPanel);
                add(sidePanel, BorderLayout.WEST);
                repaint();

                //Update default view things
                drawAllLocations = false;
                drawEdges = false;
                mapView.getStyle().setDrawAllEdges(false);
                mapView.getStyle().setDrawAllPoints(false);

                //Update the styles
                mapView.getStyle().setUnnamedButtonSize(UNNAMED_SIZE);
                mapView.getStyle().setNamedButtonSize(NAMED_SIZE);
                mapView.getStyle().setStartOrEndButtonSize(START_SIZE);
                mapView.getStyle().setStartPointColor(oldStartColor);
                mapView.getStyle().setEndPointColor(oldEndColor);

                System.out.println(oldStartColor);

                //Get mapView back to the way it was
                mapView.removeMouseListener(devToolClickListener);
                mapView.setButtonListener(buildRouteSelectListener());

                resetMap(mapView);

                repaint();
            }
        });
        //Action listener for toggling edges. Turns all edges on or off
        toggleEdges.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            drawEdges = !drawEdges;
            style.setDrawAllEdges(drawEdges);

            mapView.repaint();
        });

        //Action listener for showing only named nodes, or all the nodes.
        showNodes.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            drawAllLocations = !drawAllLocations;
            style.setDrawAllPoints(drawAllLocations);

            mapView.updateButtonAttributes();
            mapView.repaint();
        });

        /**
         * Action listener for default style
         * Only changes the colors of the nodes/edges
         * Doesn't change the edge or node toggle states
         */
        defaultStyle.addActionListener(e -> {
            defaultMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(250, 120, 0),     //Edge color
                    new Color(15, 78, 152),     //Route Edge color
                    new Color(255, 240, 0),     //Location Color
                    new Color(79, 189, 255),    //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.CYAN,  //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(defaultMapViewStyle);
            updateStartEndColors();
        });

        //Action listener for WPI style
        aWPIStyle.addActionListener(e -> {
            MapViewStyle wpiMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(169, 176, 183),   //Edge color
                    new Color(169, 176, 183),   //Route Edge color
                    new Color(0, 0, 0),         //Location Color
                    new Color(100, 0, 0),       //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.MAGENTA,  //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(wpiMapViewStyle);
            updateStartEndColors();
        });

        //Action listener for Blue Style
        blueStyle.addActionListener(e -> {
            MapViewStyle blueMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(105, 138, 172), //Edge color
                    new Color(169, 176, 183), //Route Edge color
                    new Color(16, 78, 139),   //Location Color
                    new Color(0, 245, 255),   //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.MAGENTA,  //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(blueMapViewStyle);
            updateStartEndColors();
        });

        //Action listener for Neon Funk Style (Inspired by Chiara)
        neonFunkStyle.addActionListener(e -> {
            MapViewStyle neonFunkMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(0, 255, 0),   //Edge color
                    new Color(0, 245, 255), //Route Edge color
                    new Color(255, 0, 255), //Location Color
                    new Color(255, 255, 0), //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.MAGENTA,  //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(neonFunkMapViewStyle);
            updateStartEndColors();
        });

        //Action listener for Vintage Style
        vintageStyle.addActionListener(e -> {
            MapViewStyle vintageMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(255, 99, 71),     //Edge color
                    new Color(0, 134, 139),     //Route Edge color
                    new Color(139, 71, 93),     //Location Color
                    new Color(255, 185, 15),    //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.CYAN,     //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(vintageMapViewStyle);
            updateStartEndColors();
        });

        //Action listener for Colorblind Style
        colorBlindStyle.addActionListener(e -> {
            MapViewStyle colorblindMapViewStyle = new MapViewStyle(
                    devToolsPanel.getDevMode() || drawEdges,        //Draw Edges
                    devToolsPanel.getDevMode() || drawAllLocations, //Draw all points
                    true,   //Draw named points
                    true,   //Draw routes
                    devToolsPanel.getDevMode() ? DEV_UNNAMED_SIZE : UNNAMED_SIZE, //Unnamed button size
                    devToolsPanel.getDevMode() ? DEV_NAMED_SIZE : NAMED_SIZE,     //Name button size
                    devToolsPanel.getDevMode() ? DEV_START_SIZE : START_SIZE,     //Start or end button size
                    new Color(200, 0, 0),       //Edge color
                    new Color(255, 185, 15),    //Route Edge color
                    new Color(0, 0, 255),       //Location Color
                    new Color(0, 0, 0),         //Route Location color
                    new Color(10, 255, 200),    //Start Point color
                    new Color(191, 0, 255),     //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.PINK,     //Edge highlight color
                    Color.GRAY      //Search Result Color
            );

            mapView.setStyle(colorblindMapViewStyle);
            updateStartEndColors();
        });

        //Initialize Panels and buttons
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sidePanel.setPreferredSize(new Dimension(350, 768));
        sidePanel.setVisible(true);

        startInfo = new JLabel();
        startInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        startInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

        endPointInfo = new JLabel();
        endPointInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        endPointInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

        routeInfo = new JTextArea();
        routeInfo.setVisible(true);
        routeInfo.setEditable(false);

        gps = new JTextArea();
        gps.setVisible(true);
        gps.setEditable(false);

//        clearButton = new JButton("Clear Selections");
//        clearButton.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
//        clearButton.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
//        clearButton.setToolTipText("Remove the previously selected start and end points");
//        clearButton.addActionListener(e -> clearState(mapView));

        makeAStarRoute = new JButton("Find Shortest Route");
        makeAStarRoute.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setToolTipText("Generate the most efficient possible route between the selected points");
        makeAStarRoute.addActionListener(e -> {
            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                resetMap(this.mapView);
                java.util.List<Location> route = graph.makeAStarRoute(attributeManager, startPoint, endPoint);
                if (route.size() > 0) {
                    mapView.addRoute(route);
                    gps.setText("");
                    Instruction instruct = new Instruction();
                    int count = 0;
                    for (String str : instruct.stepByStepInstruction(route, 1)) {
                        count++;
                        gps.append(count + ") " + str);
                    }

                    repaint();
                    startPoint = null;
                    endPoint = null;

                    startInfo.setText("Start Point: Not selected");
                    endPointInfo.setText("End Point: Not selected");

                    makeAStarRoute.setEnabled(false);
                    //clearButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "There is no path between the selected points!",
                            "Routing Error!",
                            JOptionPane.ERROR_MESSAGE);
                    clearState(mapView);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "You must select two points in order to generate a path.",
                        "Routing Error!",
                        JOptionPane.ERROR_MESSAGE);
                clearState(mapView);
            }
        });

        searchText = new JTextField(20);
        searchText.setVisible(true);
        searchText.setPreferredSize(new Dimension(170, 30));
        searchText.setMaximumSize(new Dimension(170, 30));

        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(90, 30));
        searchButton.setMaximumSize(new Dimension(90, 30));
        searchButton.setToolTipText("Search the location on map.");
        searchButton.addActionListener(e -> {
            locToSearch = null; //the location user want to search
            try {
                clearState(mapView);
                locToSearch = searchText.getText(); //gets the name from text field
                //if there is no entering
                if (locToSearch.length() == 0) {
                    //if no location is entered
                    JOptionPane.showMessageDialog(this, "Please Enter a Location.");
                } else {
                    //search the name in nameLList for all locations in the graph
                    java.util.List<Location> loc = graph.searchLocationByName(locToSearch);
                    if (loc.size() > 0) {
                        //if locations are found
                        JFrame frameSearch = new JFrame("Search");
                        frameSearch.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        frameSearch.setMinimumSize(new Dimension(1024, 768));
                        mapView.addToSearchList(loc);
                        repaint();
                        //clearButton.setEnabled(true);
                    } else {
                        //if no location is found
                        JOptionPane.showMessageDialog(this, "Location is not found.");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a Location to Search."); //handle NullPointerException
            }

        });
        EdgeWeightMenu edgeWeightPanel = new EdgeWeightMenu(attributeManager);
        JScrollPane text = new JScrollPane(gps);
        JScrollPane routePane = new JScrollPane(routeInfo);
        routePane.setPreferredSize(new Dimension(300, 50));
        routePane.setMaximumSize(new Dimension(300, 50));
        text.setPreferredSize(new Dimension(300, 300));
        text.setMaximumSize(new Dimension(300, 300));
        //Add elements to the search panel
        sidePanel.add(searchText);
        sidePanel.add(searchButton);
        sidePanel.add(startInfo);
        sidePanel.add(endPointInfo);
        sidePanel.add(routePane);
        sidePanel.add(makeAStarRoute);
        sidePanel.add(text);
        sidePanel.add(edgeWeightPanel);
        //sidePanel.add(clearButton, BorderLayout.SOUTH);


        //Set layout and add
        setLayout(new BorderLayout());
        add(mapView);
        add(sidePanel, BorderLayout.WEST);
        add(devToolsPanel, BorderLayout.EAST);

        clearState(mapView);
    }
    /**
     * Clear the state of the App, returning it to the default state.
     */
    private void clearState(MapView toReset) {
        startPoint = null;
        endPoint = null;

        startInfo.setText("Start Point: Not selected");
        endPointInfo.setText("End Point: Not selected");

        makeAStarRoute.setEnabled(false);
       // clearButton.setEnabled(false);

        gps.setText("");

        resetMap(toReset);
    }

    private ActionListener buildRouteSelectListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

                if (startPoint == null) {
                    startPoint = clickedLocation;
                    route.add(clickedLocation);
                    ((LocationButton) e.getSource()).setBgColor(mapView.getStyle().getStartPointColor());
                    if (clickedLocation.getNameList().length == 0){
                        startInfo.setText("Start Point:  Unnamed Location");
                    } else { startInfo.setText("Start Point:  " + clickedLocation.getNameList()[0]); }

                    //clearButton.setEnabled(true);
                } else if (endPoint == null && clickedLocation != startPoint) {
                    endPoint = clickedLocation;
                    route.add(clickedLocation);
                    ((LocationButton) e.getSource()).setBgColor(mapView.getStyle().getEndPointColor());
                    if (clickedLocation.getNameList().length == 0){
                        endPointInfo.setText("End Point:  Unnamed Location");
                    } else { endPointInfo.setText("End Point:  " + clickedLocation.getNameList()[0]); }

                    //clearButton.setEnabled(true);
                    makeAStarRoute.setEnabled(true);
                }
                routeInfo.setText("");
                if (!route.isEmpty())
                {
                    String str = "";
                    for (int i = 0; i < route.size(); i++)
                    {
                        if (route.get(i).getNameList().length != 0)
                        {
                            str = i + ": " + route.get(i).getNameList()[0] + "\n";
                            routeInfo.append(str);
                        }
                    }

                }
            }
        };
    }

    /**
     * Reset the state of just the map. (none of the class data)
     *
     * @param toReset the mapView to reset
     */
    private void resetMap(MapView toReset) {
        //todo fix this?
        gps.setText("");
        toReset.updateGraph(graph);

        updateStartEndColors();
    }

    private void updateStartEndColors() {
        for (LocationButton locButton: mapView.getLocationButtonList()) {
            if (locButton.getAssociatedLocation() == startPoint) {
                locButton.setBgColor(mapView.getStyle().getStartPointColor());
            }
            if (locButton.getAssociatedLocation() == endPoint) {
                locButton.setBgColor(mapView.getStyle().getEndPointColor());
            }
        }
    }
}
