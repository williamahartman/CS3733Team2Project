package ui;

import core.*;
import database.Database;
import dev.DevPassword;
import dev.DevTools;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * This class will build a frame that is pre-populated panels and buttons.
 * This runs the app.
 */
public class MainAppUI extends JFrame{
    private static final String FRAME_TITLE = "AZTEC WASH Mapper";
    private static final int SIDEPANEL_WIDTH = 250;

    //Button sized
    private static final int UNNAMED_SIZE = 5;
    private static final int NAMED_SIZE = 10;
    private static final int START_SIZE = 15;
    private static final int DEV_UNNAMED_SIZE = 10;
    private static final int DEV_NAMED_SIZE = 15;
    private static final int DEV_START_SIZE = 15;

    private MapView mapView;
    private MapViewStyle defaultMapViewStyle;
    private LocationGraph graph;

    private Location startPoint;
    private Location endPoint;
    private List<Location> route;

    private JLabel startInfo;
    private JLabel endPointInfo;
    private JTextArea routeInfo;

    private JTextArea gps;
    private JTextField emailText;
    private String locToSearch;
    private String emailToSend;

    private JButton clearButton;
    private JButton makeAStarRoute;
    private JButton emailButton;


    private JComboBox searchDropDownList;
    private JComboBox searchBox;

    private JLabel searchInfo;
    private JLabel endLocInfo;
    private JButton addToStart;
    private JButton addToDestination;
    private JComboBox<String> multipleDestination;
    private Location tempLoc;
    private List<Location> multiLoc;
    private int desNum;
    private boolean isClosed = false;

    private DevTools devToolsPanel;
    private MouseListener devToolClickListener;

    private EdgeAttributeManager attributeManager;
    private boolean userLoggedIn;

    private boolean drawEdges = false;
    private boolean drawAllLocations = false;
    private boolean voiceDirections = false;

    //Save start and end colors as class data to help allow restoring when changing
    //in and out of devmode
    private Color oldStartColor;
    private Color oldEndColor;

    private int stepCount = 0;
    private int time = 0;


    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     */
    public MainAppUI(LocationGraph graph, HashMap<Integer, MapImage> maps, int defaultFloor) {
        super(FRAME_TITLE);

        //change the look and feel to the BeautyEye style
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("FrameBorderStyle.osLookAndFeelDecorated", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                Color.BLACK      //Search Result Color
        );

        this.mapView = new MapView(graph, maps, defaultFloor, defaultMapViewStyle);
        this.mapView.setButtonListener(buildRouteSelectListener());
        this.attributeManager = new EdgeAttributeManager();

        devToolsPanel = new DevTools(graph, mapView);

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
        editMenu.addChangeListener(e -> menuBar.repaint());

        JMenuItem refreshMap = new JMenuItem("Refresh Map");
        refreshMap.addActionListener(e -> {
            int result = JOptionPane.OK_OPTION;
            if (devToolsPanel.getDevMode()) {
                result = JOptionPane.showConfirmDialog(null, new JLabel(
                        "<html>Refreshing the map will pull a new version from the database.<br>" +
                        "Any local changes will be lost." +
                        "<br><br>Is that ok?</html>"),
                        "Are You a Developer?", JOptionPane.OK_CANCEL_OPTION);
                System.out.println(result);
            }

            if (result == JOptionPane.OK_OPTION) {
                try {
                    Database graphData = new Database();
                    graph = graphData.createGraph();

                    mapView.setSvgList(graphData.getMaps());
                    mapView.setDefaultFloor(graphData.getDefaultFloor());
                    mapView.setFloor(mapView.getDefaultFloorNumber());

                    refreshMap();
                    graphData.closeConnection();
                } catch (SQLException exception) {
                    JOptionPane.showMessageDialog(mapView.getParent(),
                            "Failed to connect to the online database (be on the internet!)",
                            "Database error!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            mapView.setGraph(graph);
            devToolsPanel.setLocationGraph(graph);
            SearchBoxModel model = new SearchBoxModel(searchDropDownList, graph.getNamedLocations(), mapView);
            searchDropDownList.setModel(model);

            updateStartEndColors();
        });
        JMenuItem enterDeveloperMode = new JMenuItem("Edit Map (Developers Only!)");

        DevPassword passBox = new DevPassword();
        devToolsPanel.setVisible(false);

        userLoggedIn = false;


        //Sets up the 'view' menu bar
        JMenu view = new JMenu("View");
        view.addChangeListener(e -> menuBar.repaint());

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

        // -- ABOUT MENU STARTS HERE -- //

        JMenu help = new JMenu("Help");
        help.addChangeListener(e -> {
            menuBar.repaint();
        });
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> {
            JDialog aboutFrame = new JDialog();
            aboutFrame.setTitle("About");
            aboutFrame.setLayout(new FlowLayout());
            aboutFrame.setVisible(true);
            aboutFrame.setMinimumSize(new Dimension(400, 300));
            JPanel aboutPanel = new JPanel(new FlowLayout());
            aboutPanel.setMinimumSize(new Dimension(400, 300));
            JLabel aboutLabel = new JLabel("<html><center>Worcester Polytechnic Institute<br>" +
                    "CS3733 2015 B-Term<br>" +
                    "<br>" +
                    "<u>Developed by Team AZTEC WASH:</u><br><br>" +
                    "<b>Alex Locke</b><br>[Map Artist / UI Designer]<br><br>" +
                    "<b>Zoe Ding</b><br>[A* Specialist / Search Algorithm Specialist]<br><br>" +
                    "<b>Tom Farro</b><br>[Project Manager / Developer Tools Specialist]<br><br>" +
                    "<b>Elizabeth Tomko</b><br>[Test Engineer / Documentation Specialist]<br><br>" +
                    "<b>Callum Taylor</b><br>[A* Specialist / Software Engineer]<br><br>" +
                    "<b>Will Hartman</b><br>[Lead Software Engineer]<br><br>" +
                    "<b>Alora Hillman</b><br>[Developer Tools Specialist / UI Designer]<br><br>" +
                    "<b>Scott Iwanicki</b><br>[Database Specialist / Software Engineer]<br><br>" +
                    "<b>Holly Nguyen</b><br>[Database Specialist / Project Manager]<br>" +
                    "<br>" +
                    "<u>Special Thanks to:</u><br><br>" +
                    "Coach <b>Caitlin Malone</b> and Prof. <b>Wilson Wong</b><br>" +
                    "</center></html>");
            aboutPanel.add(aboutLabel);
            aboutFrame.add(aboutPanel);
            aboutFrame.pack();
            about.repaint();
        });
        help.add(about);
        JMenuItem tutorial = new JMenuItem("Tutorial");
        about.addActionListener(e -> {

        });
        help.add(tutorial);

        // -- ABOUT MENU ENDS HERE -- //

        //Sets up menu hierarchy
        setJMenuBar(menuBar);
        menuBar.add(editMenu);
        menuBar.add(view);
        menuBar.add(help);
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
                        devToolsPanel.setUsername(passBox.getSuccessfulUsername());
                        devToolsPanel.setPassword(passBox.getSuccessfulPassword());
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
                    clearState();
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

                SearchBoxModel model = new SearchBoxModel(searchDropDownList, graph.getNamedLocations(), mapView);
                searchDropDownList.setModel(model);

                refreshMap();

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

            mapView.refreshGraph();
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
                    Color.BLACK      //Search Result Color
            );

            mapView.setStyle(defaultMapViewStyle);
            refreshMap();
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
                    new Color(100, 100, 100),   //Edge color
                    new Color(100, 0, 0),   //Route Edge color
                    new Color(0, 0, 0),         //Location Color
                    new Color(200, 0, 0),       //Route Location color
                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.MAGENTA,  //Edge highlight color
                    Color.BLACK      //Search Result Color
            );

            mapView.setStyle(wpiMapViewStyle);
            refreshMap();
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
                    new Color(0, 245, 255), //Route Edge color
                    new Color(16, 78, 139),  //Location Color
                    new Color(0, 50, 60), //Route Location color

                    Color.RED,      //Start Point color
                    Color.GREEN,    //End Point color
                    Color.CYAN,     //Selected Point color
                    Color.BLUE,     //Previously Selected Point color
                    Color.MAGENTA,  //Edge highlight color
                    Color.BLACK     //Search Result Color
            );

            mapView.setStyle(blueMapViewStyle);
            refreshMap();
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
                    Color.BLACK      //Search Result Color
            );

            mapView.setStyle(neonFunkMapViewStyle);
            refreshMap();
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
                    Color.BLACK     //Search Result Color
            );

            mapView.setStyle(vintageMapViewStyle);
            refreshMap();
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
                    Color.BLACK      //Search Result Color
            );

            mapView.setStyle(colorblindMapViewStyle);
            refreshMap();
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

        clearButton = new JButton("Clear Selections");
        clearButton.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        clearButton.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        clearButton.setToolTipText("Remove the previously selected start and end points");
        clearButton.addActionListener(e -> clearState());

        makeAStarRoute = new JButton("Find Shortest Route");
        makeAStarRoute.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setToolTipText("Generate the most efficient possible route between the selected points");
        makeAStarRoute.addActionListener(e -> {
            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                refreshMap();
                mapView.clearRoutes();
                route.clear();

                //changed makeAStarRoute to makeMultipleRoute
                route = graph.makeMultipleRoute(attributeManager, multiLoc);
                List<Location> routeTime = graph.makeMultipleRoute(attributeManager, multiLoc);
                if (route.size() > 0) {
                    stepCount = 0;
                    mapView.addRoute(routeTime);
                    gps.setText("");
                    Instruction instruct = new Instruction();
                    int count = 0;

                    int scaleX = mapView.getCurrentMapImage().getScaleX();
                    int scaleY = mapView.getCurrentMapImage().getScaleY();
                    for (String str : instruct.stepByStepInstruction(routeTime, scaleX, scaleY)) {
                        if (!str.equals("Continue straight\n") && !str.equals(""))
                        {
                            count++;
                            gps.append(count + ") " + str);
                        }
                    }

                    repaint();
                    startPoint = null;
                    endPoint = null;

                    startInfo.setText("Start Point: Not selected");
                    endPointInfo.setText("End Point: Not selected");

                    makeAStarRoute.setEnabled(false);
                    emailButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    multipleDestination.removeAllItems();
                    desNum = 0;

                    multiLoc.clear();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "There is no path between the selected points!",
                            "Routing Error!",
                            JOptionPane.ERROR_MESSAGE);
                    clearState();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "You must select two points in order to generate a path.",
                        "Routing Error!",
                        JOptionPane.ERROR_MESSAGE);
                clearState();
            }
        });

        desNum = 0;
        searchDropDownList = new JComboBox();
        searchDropDownList.setEditable(true);
        SearchBoxModel model = new SearchBoxModel(searchDropDownList, graph.getNamedLocations(), mapView);
        searchDropDownList.setModel(model);
        searchDropDownList.addItemListener(model);
        searchDropDownList.setPreferredSize(new Dimension(180, 30));
        searchDropDownList.setMaximumSize(new Dimension(180, 30));


        searchInfo = new JLabel("Search: ");
        endLocInfo = new JLabel("Destinations: ");
        multipleDestination = new JComboBox<>();
        multipleDestination.setPreferredSize(new Dimension(160, 30));
        multipleDestination.setMaximumSize(new Dimension(160, 30));
        tempLoc = null;
        multiLoc = new ArrayList<>();

        addToStart = new JButton("Add as Start");
        addToDestination = new JButton("Add as Destination");
        addToStart.addActionListener(e -> {
            if (tempLoc != null){
                startPoint = tempLoc;
                startInfo.setText("Start Point: " + locToSearch);
                multiLoc.add(0, startPoint);
                mapView.clearSearchList();
                repaint();
                updateStartEndColors();
            }
            if (multiLoc.size() > 1){
                makeAStarRoute.setEnabled(true);
            }
        });

        searchDropDownList.addActionListener(e -> {
            refreshMap();
            mapView.clearSearchList();

            String selectedName = (String) searchDropDownList.getSelectedItem();
            if (searchExactName(selectedName) != null){
                tempLoc = searchExactName(selectedName);
                locToSearch = selectedName;
            }

            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                makeAStarRoute.setEnabled(true);
            }
        });


        addToDestination.addActionListener(e -> {
            if (tempLoc != null){
                endPoint = tempLoc;
                int size = multiLoc.size();
                if (size == 0) {
                    multipleDestination.addItem(locToSearch);
                    multiLoc.add(endPoint);
                    desNum += 1;
                    multipleDestination.setSelectedIndex(desNum - 1);
                } else if (size > 0 && (!multiLoc.get(size - 1).equals(endPoint))){
                    multipleDestination.addItem(locToSearch);
                    multiLoc.add(endPoint);
                    desNum += 1;
                    multipleDestination.setSelectedIndex(desNum - 1);

                } else if (size > 0 && (multiLoc.get(size - 1).equals(endPoint))){
                    JOptionPane.showMessageDialog(this, "You've already added this location.");
                }
                mapView.clearSearchList();
                repaint();
                updateStartEndColors();
            }
            if (startPoint != null && multiLoc.size() > 1){
                makeAStarRoute.setEnabled(true);
            }
        });

        //Back and forward buttons
        JButton stepForwardOnRouteButton = new JButton("Next Step");
        stepForwardOnRouteButton.setPreferredSize(new Dimension(150, 30));
        stepForwardOnRouteButton.setMaximumSize(new Dimension(150, 30));
        stepForwardOnRouteButton.setToolTipText("Go Forward One Step");
        stepForwardOnRouteButton.addActionListener(e ->
        {
            if (stepCount < route.size())
            {
                gps.setText(mapView.stepByStep(stepCount, true, false));
                stepCount++;
                TextToVoice tv = new TextToVoice(gps.getText());
                if (voiceDirections) {
                    tv.start();
                }
            }
        });
        JButton stepBackOnRouteButton = new JButton("Previous Step");
        stepBackOnRouteButton.setPreferredSize(new Dimension(150, 30));
        stepBackOnRouteButton.setMaximumSize(new Dimension(150, 30));
        stepBackOnRouteButton.setToolTipText("Go Back One Step");
        stepBackOnRouteButton.addActionListener(e ->
        {
            if (stepCount > 0)
            {
                stepCount--;
                gps.setText(mapView.stepByStep(stepCount, false, false));
            }

        });

        //Edge Prefs Panel
        JCheckBox handicapCheckbox = new JCheckBox("Only Make Handicap Accessible Routes");
        handicapCheckbox.addActionListener(e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            attributeManager.addModifierForAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE,
                    source.isSelected() ? 0 : 1);
            attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS,
                    source.isSelected() ? 0 : 1);
        });
        JButton editRoutePrefs = new JButton("Change Route Settings");
        editRoutePrefs.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 50));
        editRoutePrefs.addActionListener(e -> {
            JDialog editWindow = new JDialog(this, "Edit Route Preferences", Dialog.ModalityType.APPLICATION_MODAL);
            editWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            editWindow.setMinimumSize(new Dimension(640, 480));
            editWindow.setLocationRelativeTo(null);
            editWindow.getContentPane().setLayout(new BorderLayout());

            JButton okayButton = new JButton("OK");
            okayButton.setPreferredSize(new Dimension(300, 75));
            okayButton.addActionListener(actionEvent -> editWindow.dispose());

            editWindow.add(new EdgeWeightMenu(attributeManager));
            editWindow.add(okayButton, BorderLayout.SOUTH);
            editWindow.setVisible(true);
        });

        emailText = new JTextField(20);
        emailText.setVisible(true);
        emailText.setPreferredSize(new Dimension(170, 30));
        emailText.setMaximumSize(new Dimension(170, 30));

        emailButton = new JButton("Send Email");
        emailButton.setPreferredSize(new Dimension(90, 30));
        emailButton.setMaximumSize(new Dimension(90, 30));
        emailButton.setToolTipText("Send an email.");
        emailButton.addActionListener(e -> {
            emailButton.setEnabled(false);
            emailToSend = null;
            try {
                emailToSend = emailText.getText(); //gets the name from text field
                if (emailToSend.length() == 0) {
                    //if no location is entered
                    JOptionPane.showMessageDialog(this, "Please Enter an Email.");
                } else {
                    //search the name in nameLList for all locations in the graph
                    if (emailToSend.length() > 0) {

                        Instruction instruct = new Instruction();

                        int scaleX = mapView.getCurrentMapImage().getScaleX();
                        int scaleY = mapView.getCurrentMapImage().getScaleY();
                        List<String> instructions = instruct.stepByStepInstruction(route, scaleX, scaleY);
                        Email email = new Email(emailToSend, instructions);
                        for (int i = 0; i < route.size(); i++) {
                            mapView.stepByStep(i, true, true);
                            mapView.clearSearchList();
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ex){
                                ex.printStackTrace();
                            }
                        }
                        email.sendEmail();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: Invalid Email!",
                        "Incorrect!", JOptionPane.ERROR_MESSAGE);
            }
            emailButton.setEnabled(true);

        });

        JScrollPane text = new JScrollPane(gps);
        text.setPreferredSize(new Dimension(300, 200));
        text.setMaximumSize(new Dimension(300, 200));


        JCheckBox textToVoice = new JCheckBox("Audio: Read step-by-step directions");
        textToVoice.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                    voiceDirections = textToVoice.isSelected();
            }
        });

        JButton closeWindow = new JButton("<<<");
        closeWindow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                animate(sidePanel, !isClosed, 350);
                isClosed = !isClosed;
            }
        });
        mapView.getFloorSliderPanel().add(closeWindow, BorderLayout.PAGE_START);
        closeWindow.setPreferredSize(new Dimension(40, 40));

        //Build child panels for organizing search stuff
        JPanel searchPanel = new JPanel();
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBarPanel.setLayout(new BoxLayout(searchBarPanel, BoxLayout.X_AXIS));

        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButtonPanel.setLayout(new BoxLayout(searchButtonPanel, BoxLayout.X_AXIS));

        JPanel destPanel = new JPanel();
        destPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        destPanel.setLayout(new BoxLayout(destPanel, BoxLayout.X_AXIS));
        //Add search stuff to these panels
        searchBarPanel.add(searchInfo);
        searchBarPanel.add(searchDropDownList);
        searchButtonPanel.add(addToStart);
        searchButtonPanel.add(addToDestination);
        searchPanel.add(searchBarPanel);
        searchPanel.add(searchButtonPanel);
        searchPanel.add(startInfo);
        destPanel.add(endLocInfo);
        destPanel.add(multipleDestination);
        searchPanel.add(destPanel);

        //Make a panel for the main buttons
        JPanel aStarPanel = new JPanel();
        aStarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        aStarPanel.setLayout(new BoxLayout(aStarPanel, BoxLayout.Y_AXIS));
        aStarPanel.add(makeAStarRoute);
        aStarPanel.add(clearButton);

        //Make a panel for the directions stuff
        JPanel directionsPanel = new JPanel();
        directionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        directionsPanel.setLayout(new BoxLayout(directionsPanel, BoxLayout.Y_AXIS));
        JPanel directionsButtonPanel = new JPanel();
        directionsButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        directionsButtonPanel.setLayout(new BoxLayout(directionsButtonPanel, BoxLayout.X_AXIS));
        directionsButtonPanel.add(stepBackOnRouteButton);
        directionsButtonPanel.add(stepForwardOnRouteButton);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        directionsPanel.add(text);
        directionsPanel.add(directionsButtonPanel);

        //Make a panel for email
        JPanel emailPanel = new JPanel();
        emailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.X_AXIS));
        emailPanel.add(emailText);
        emailPanel.add(emailButton);

        //Make prefs
        JPanel routePrefsPanel = new JPanel();
        routePrefsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        routePrefsPanel.setLayout(new BoxLayout(routePrefsPanel, BoxLayout.Y_AXIS));
        routePrefsPanel.add(textToVoice);
        routePrefsPanel.add(handicapCheckbox);
        routePrefsPanel.add(editRoutePrefs);

        sidePanel.setBackground(Color.DARK_GRAY);
        searchPanel.setBackground(Color.RED);
        aStarPanel.setBackground(Color.GREEN);
        directionsPanel.setBackground(Color.BLUE);
        emailPanel.setBackground(Color.CYAN);
        routePrefsPanel.setBackground(Color.PINK);

        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(searchPanel);
        sidePanel.add(Box.createVerticalStrut(50));
        sidePanel.add(aStarPanel);
        sidePanel.add(Box.createVerticalStrut(50));
        sidePanel.add(directionsPanel);
        sidePanel.add(emailPanel);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(routePrefsPanel);

        //Set layout and add
        setLayout(new BorderLayout());
        add(mapView);
        add(sidePanel, BorderLayout.WEST);

        add(devToolsPanel, BorderLayout.EAST);

        clearState();
    }

    /**
     * Clear the state of the App, returning it to the default state.
     */
    private void clearState() {
        startPoint = null;
        endPoint = null;

        multiLoc.clear();
        desNum = 0;

        startInfo.setText("Start Point: Not selected");
        endPointInfo.setText("End Point: Not selected");
        multipleDestination.removeAllItems();

        makeAStarRoute.setEnabled(false);
        emailButton.setEnabled(false);
        clearButton.setEnabled(false);

        gps.setText("");

        mapView.setGraph(graph);
        refreshMap();
    }

    private ActionListener buildRouteSelectListener() {
        return e -> {
            Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

            if (startPoint == null) {
                multiLoc.add(0, clickedLocation);
                startPoint = clickedLocation;
                route.add(clickedLocation);
                ((LocationButton) e.getSource()).setBgColor(mapView.getStyle().getStartPointColor());
                if (clickedLocation.getNameList().length == 0){
                    startInfo.setText("Start Point:  Unnamed Location");
                } else { startInfo.setText("Start Point:  " + clickedLocation.getNameList()[0]); }
                if (startPoint != null && multiLoc.size() > 1) {
                    makeAStarRoute.setEnabled(true);
                }
                clearButton.setEnabled(true);
            } else if (startPoint != null && clickedLocation != endPoint) {
                endPoint = clickedLocation;
                route.add(clickedLocation);
                multiLoc.add(clickedLocation);
                ((LocationButton) e.getSource()).setBgColor(mapView.getStyle().getEndPointColor());
                if (clickedLocation.getNameList().length == 0){
                    multipleDestination.addItem("Unnamed Location");
                    desNum += 1;
                    multipleDestination.setSelectedIndex(desNum - 1);
                } else {
                    multipleDestination.addItem(clickedLocation.getNameList()[0]);
                    desNum += 1;
                    multipleDestination.setSelectedIndex(desNum - 1);
                }

                clearButton.setEnabled(true);
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
        };
    }

    /**
     * Reset the state of just the map. (none of the class data)
     *
     */
    private void refreshMap() {
        gps.setText("");
        mapView.refreshGraph();
    }

    /**
     * Update the colors of the slected route buttons to conform to the current theme.
     * This is only used for locally selected points (not route start or end in mapview)
     */
    private void updateStartEndColors() {
        for (LocationButton locButton: mapView.getLocationButtonList()) {
            if (multiLoc.contains(locButton.getAssociatedLocation())) {
                locButton.setBgColor(mapView.getStyle().getEndPointColor());
            }
            if (locButton.getAssociatedLocation() == startPoint) {
                locButton.setBgColor(mapView.getStyle().getStartPointColor());
            }
            locButton.repaint();
        }
    }

    private Location searchExactName(String selectedName){
        Location result = null;
        try {
            //if there is no entering
            if (selectedName.length() > 0) {
                //search the name in nameLList for all locations in the graph
                List<Location> loc = graph.searchLocationByExacName(selectedName);
                result = addSearchToView(loc);
            }
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Enter a Location to Search."); //handle NullPointerException
        }
        return result;
    }

    private Location addSearchToView(List<Location> loc){
        Location result = null;
        if (loc.size() > 0) {
            result = loc.get(0);
            //if locations are found
            JFrame frameSearch = new JFrame("Search");
            frameSearch.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frameSearch.setMinimumSize(new Dimension(1024, 768));
            mapView.addToSearchList(loc);
            repaint();
            clearButton.setEnabled(true);
        }
        return result;
    }

    //Animate the panel
    private void animate(JPanel panel, boolean closing, int stepTranslation){
        int totalTime = 350 / stepTranslation; //350
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (closing) {
                    panel.setBounds(panel.getX() - stepTranslation, panel.getY(), panel.getWidth(), panel.getHeight());
                    mapView.setBounds(mapView.getX() - stepTranslation, mapView.getY(), mapView.getWidth() +
                            stepTranslation, mapView.getHeight());
                    mapView.validate();
                    time++;
                } else {
                    panel.setBounds(panel.getX() + stepTranslation, panel.getY(), panel.getWidth(), panel.getHeight());
                    mapView.setBounds(mapView.getX() + stepTranslation, mapView.getY(), mapView.getWidth()
                            - stepTranslation, mapView.getHeight());
                    mapView.validate();
                    panel.validate();
                    time++;
                }

                if (time >= totalTime) {
                    time = 0;
                    timer.cancel();
                    mapView.validate();
                }
            }
        }, 0, 5);

    }



}
