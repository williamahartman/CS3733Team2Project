package ui;

import core.EdgeAttributeManager;
import core.Instruction;
import core.Location;
import core.LocationGraph;
import dev.DevTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.EventListener;

/**
 * This class will build a frame that is pre-populated panels and buttons.
 * This runs the app.
 */
public class MainAppUI extends JFrame{
    private static final String FRAME_TITLE = "AZTEC WASH Mapper";
    private static final int SIDEPANEL_WIDTH = 250;
    private static final double DEFAULT_ZOOM = 0.4;

    private BufferedImage mapBackground;

    private MapView mapView;
    private LocationGraph graph;

    private Location startPoint;
    private Location endPoint;

    private JLabel startInfo;
    private JLabel endPointInfo;

    private JTextArea gps;

    private JButton clearButton;
    private JButton makeAStarRoute;
    public static int floorNumber;

    private DevTools devToolsPanel;
    private MouseListener devToolClickListener;

    private EdgeAttributeManager attributeManager;

    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     * @param backgroundImagePath The path to the image that will be used as a background
     */
    public MainAppUI(LocationGraph graph, String backgroundImagePath) {
        super(FRAME_TITLE);
        this.graph = graph;

        try {
            mapBackground = ImageIO.read(ClassLoader.getSystemResourceAsStream(backgroundImagePath));
        } catch (Exception e) {
            //Close the program with an error message if we can't load stuff.
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "The map image failed to load!",
                    "Asset Load Failed!",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        MapViewStyle style = new MapViewStyle(
                true,
                true,
                true,
                true,
                new Color(250, 120, 0),
                new Color(15, 78, 152),
                new Color(255, 240, 0),
                new Color(79, 189, 255));

        style.setEdgeColor(new Color(255, 60, 0), 1);
        style.setEdgeColor(new Color(255, 30, 0), 2);


        this.mapView = new MapView(graph, mapBackground, DEFAULT_ZOOM, style);

        this.attributeManager = new EdgeAttributeManager();

        startPoint = null;
        endPoint = null;
    }

    /**
     * Populate the JFrame with panels used for the applcation.
     */
    public void setUpMainApp() {
        //Set up menubar
        JPanel sidePanel = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");

        JMenuItem refreshMap = new JMenuItem("Refresh Map");
        refreshMap.addActionListener(e -> resetMap(mapView));
        JMenuItem enterDevloperMode = new JMenuItem("Edit Map (Developers Only!)");

        devToolsPanel = new DevTools(graph, mapView);
        devToolsPanel.setBackground(new Color(255, 0, 0));
        devToolsPanel.setVisible(false);
        enterDevloperMode.addActionListener(e -> {
            if (!devToolsPanel.getDevMode()) {
                devToolsPanel.setDevMode(true);
                remove(sidePanel);
                devToolsPanel.setVisible(true);
                add(devToolsPanel, BorderLayout.WEST);
                repaint();
                enterDevloperMode.setText("Exit Developer Mode");

                clearState(mapView);
                devToolClickListener = devToolsPanel.buildAddLocationListener(mapView.getMapPanel());
                mapView.getMapPanel().addMouseListener(devToolClickListener);
                devToolsPanel.rebuildGraph();
            } else {
                devToolsPanel.setDevMode(false);
                enterDevloperMode.setText("Edit Map (Developers Only!)");
                remove(devToolsPanel);
                add(sidePanel, BorderLayout.WEST);
                repaint();

                resetMap(mapView);
                mapView.removeMouseListener(devToolClickListener);
            }
        });

        //Sets up the 'view' menu bar
        JMenu view = new JMenu("View");

        //'View' contains toggleEdges, showNodes, and changeStyle
        JMenuItem toggleEdges = new JMenuItem("Toggle Edges");
        JMenuItem showNodes = new JMenuItem("Show Only Named Locations");
        JMenu changeStyle = new JMenu("Change Style");

        //changeStyle contains defaultStyle, WPIStyle, monochromaticStyle
        JMenuItem defaultStyle = new JMenuItem("Default Style");
        JMenuItem WPIStyle = new JMenuItem("WPI Style");
        JMenuItem blueStyle = new JMenuItem("Blue Style");
        JMenuItem neonFunkStyle = new JMenuItem("Neon Funk Style");
        JMenuItem vintageStyle = new JMenuItem("Vintage Style");

        //Sets up menu hierarchy
        setJMenuBar(menuBar);
        menuBar.add(editMenu);
        menuBar.add(view);
        view.add(toggleEdges);
        view.add(showNodes);
        view.add(changeStyle);
        editMenu.add(refreshMap);
        editMenu.add(enterDevloperMode);
        changeStyle.add(defaultStyle);
        changeStyle.add(WPIStyle);
        changeStyle.add(blueStyle);
        changeStyle.add(neonFunkStyle);
        changeStyle.add(vintageStyle);

        //Action listener for toggling edges. Turns all edges on or off
        toggleEdges.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setDrawAllEdges(!style.isDrawAllEdges());

            resetMap(mapView);
        });

        //Action listener for showing only named nodes, or all the nodes. Currently not functioning correctly.
        showNodes.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            if (style.isDrawAllPoints()){
                showNodes.setText("Show Only Named Locations");
                style.setDrawAllPoints(false);
                style.setDrawNamedPoints(true);
            } else {
                showNodes.setText("Show All Locations");
                style.setDrawAllPoints(true);
            }
            mapView.updateGraph(graph, floorNumber);
        });

        /**
         * Action listener for default style
         * Only changes the colors of the nodes/edges
         * Doesn't change the edge or node toggle states
         */
        defaultStyle.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setLocationColor(new Color(255, 240, 0));
            style.setEdgeColor(new Color(250, 120, 0), 0);
            style.setEdgeColor(new Color(255, 60, 0), 1);
            style.setEdgeColor(new Color(255, 30, 0), 2);
            style.setRouteLocationColor(new Color(79, 189, 255));
            style.setRouteColor(new Color(15, 78, 152));
            resetMap(mapView);
        });

        //Action listener for WPI style
        WPIStyle.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setLocationColor(new Color(0, 0, 0));
            style.setEdgeColor(new Color(100, 100, 100), 0);
            style.setEdgeColor(new Color(70, 70, 70), 1);
            style.setEdgeColor(new Color(40, 40, 40), 2);
            style.setRouteLocationColor(new Color(255, 0, 0));
            style.setRouteColor(new Color(100, 0, 0));
            resetMap(mapView);
        });

        //Action listener for Blue Style
        blueStyle.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setLocationColor(new Color(16, 78, 139));
            style.setEdgeColor(new Color(125, 158, 192), 0);
            style.setEdgeColor(new Color(105, 138, 172), 1);
            style.setEdgeColor(new Color(85, 118, 152), 2);
            style.setRouteLocationColor(new Color(0, 245, 255));
            style.setRouteColor(new Color(151, 255, 255));
            resetMap(mapView);
        });

        //Action listener for Neon Funk Style (Inspired by Chiara)
        neonFunkStyle.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setLocationColor(new Color(255, 0, 255));
            style.setEdgeColor(new Color(0, 255, 0), 0);
            style.setEdgeColor(new Color(0, 150, 0), 1);
            style.setEdgeColor(new Color(0, 100, 0), 2);
            style.setRouteLocationColor(new Color(255, 255, 0));
            style.setRouteColor(new Color(0, 245, 255));
            resetMap(mapView);
        });

        //Action listener for Vintage Style
        vintageStyle.addActionListener(e -> {
            MapViewStyle style = mapView.getStyle();
            style.setLocationColor(new Color(139, 71, 93));
            style.setEdgeColor(new Color(255, 99, 71), 0);
            style.setEdgeColor(new Color(225, 70, 40), 1);
            style.setEdgeColor(new Color(205, 49, 21), 2);
            style.setRouteLocationColor(new Color(255, 185, 15));
            style.setRouteColor(new Color(0, 134, 139));
            resetMap(mapView);
        });

        //Initialize Panels and buttons
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(350, 768));
        sidePanel.setVisible(true);
        JComboBox<Integer> floorNum = new JComboBox<>();
        floorNum.addItem(0);
        floorNum.addItem(1);
        floorNum.addItem(2);
        floorNum.setSelectedItem(0);
        floorNum.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        floorNum.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));
        floorNum.setToolTipText("Select Floor Number");
        floorNum.setFont(new Font("Arial", Font.BOLD, 20));
        floorNum.addActionListener(e ->{
            //TODO Change based on the style
                if (floorNum.getSelectedItem().equals(0))
                {
                    //display ground map
                    floorNumber = 0;
                    this.mapView.updateGraph(graph, 0);
                    repaint();


                }
                else if (floorNum.getSelectedItem().equals(1))
                {
                    floorNumber = 1;
                    this.mapView.updateGraph(graph, 1);
                    repaint();

                }
                else if (floorNum.getSelectedItem().equals(2))
                {
                    floorNumber = 2;
                    this.mapView.updateGraph(graph, 2);
                    repaint();

                }
            });


        startInfo = new JLabel();
        startInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        startInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

        endPointInfo = new JLabel();
        endPointInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        endPointInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

        gps = new JTextArea();
        gps.setVisible(true);

        clearButton = new JButton("Clear Selections");
        clearButton.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        clearButton.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        clearButton.setToolTipText("Remove the previously selected start and end points");
        clearButton.addActionListener(e -> clearState(mapView));


        makeAStarRoute = new JButton("Find Shortest Route");
        makeAStarRoute.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setToolTipText("Generate the most efficient possible route between the selected points");
        makeAStarRoute.addActionListener(e -> {
            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                JFrame frame = new JFrame("Route");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setMinimumSize(new Dimension(1024, 768));

                java.util.List<Location> route = graph.makeAStarRoute(new EdgeAttributeManager(), startPoint, endPoint);
                if (route.size() > 0) {
                    mapView.addRoute(route);
                    gps.setText("");
                    Instruction instruct = new Instruction();
                    int count = 0;
                    for (String str:instruct.stepByStepInstruction(route, 1))
                    {
                        count++;
                        gps.append(count + ") " + str);
                    }

                    repaint();
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

        //Add elements to the side panel
        JScrollPane text = new JScrollPane(gps);
        sidePanel.add(startInfo, BorderLayout.CENTER);
        sidePanel.add(endPointInfo, BorderLayout.CENTER);
        sidePanel.add(makeAStarRoute, BorderLayout.CENTER);
        sidePanel.add(floorNum);
        sidePanel.add(text, BorderLayout.CENTER);
        sidePanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        sidePanel.add(clearButton);


        //Set layout and add
        setLayout(new BorderLayout());
        add(mapView);
        add(sidePanel, BorderLayout.WEST);
        add(devToolsPanel, BorderLayout.EAST);


//        addWindowFocusListener(new WindowFocusListener() {
//            @Override
//            public void windowGainedFocus(WindowEvent e) {
//                resetMap(mapView);
//            }
//
//            @Override
//            public void windowLostFocus(WindowEvent e) {
//                resetMap(mapView);
//            }
//        });

        clearState(mapView);
    }

    /**
     * Add the listeners to all the nodes in the map.
     */
    private void addListenersToMapNodes(MapView view, EventListener listener) {
        for (LocationButton button: view.getLocationButtonList()) {
            if (listener instanceof ActionListener) {
                button.addActionListener((ActionListener) listener);
            } else if (listener instanceof MouseAdapter) {
                button.addMouseListener((MouseAdapter) listener);
            } else {
                System.err.println("Could not add listener " + listener + ". No implementation for type!");
            }
        }
    }

    /**
     * Clear the state of the App, returning it to the default state.
     */
    private void clearState(MapView toReset) {
        startPoint = null;
        endPoint = null;

        startInfo.setText("Start Point: Not selected");
        endPointInfo.setText("Destination Point: Not selected");

        makeAStarRoute.setEnabled(false);
        clearButton.setEnabled(false);

        gps.setText("");

        resetMap(toReset);
    }

    /**
     * Reset the state of just the map. (none of the class data)
     *
     * @param toReset the mapView to reset
     */
    private void resetMap(MapView toReset) {
        toReset.updateGraph(graph, 0);
        addListenersToMapNodes(mapView, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

                if (startPoint == null) {
                    startPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.GREEN);
                    if (clickedLocation.getNameList().length == 0){
                        startInfo.setText("Start Point:  Unnamed Location");
                    } else { startInfo.setText("Start Point:  " + clickedLocation.getNameList()[0]); }

                    clearButton.setEnabled(true);
                } else if (endPoint == null && clickedLocation != startPoint) {
                    endPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.RED);
                    if (clickedLocation.getNameList().length == 0){
                        endPointInfo.setText("Destination Point:  Unnamed Location");
                    } else { endPointInfo.setText("Destination Point:  " + clickedLocation.getNameList()[0]); }

                    clearButton.setEnabled(true);
                    makeAStarRoute.setEnabled(true);
                }
            }
        });

        //Make sure selected stuff is still respected
        for (LocationButton locButton: mapView.getLocationButtonList()) {
            if (locButton.getAssociatedLocation() == startPoint) {
                locButton.setBackground(Color.GREEN);
            }
            if (locButton.getAssociatedLocation() == endPoint) {
                locButton.setBackground(Color.RED);
            }
        }
    }


}
