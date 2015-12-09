package ui;

import core.EdgeAttribute;
import core.EdgeAttributeManager;
import core.Location;
import core.LocationGraph;
import database.Database;
import dev.DevPassword;
import dev.DevTools;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * This class will build a frame that is pre-populated panels and buttons.
 * This runs the app.
 */
public class MainAppUI extends JFrame{
    private static final String FRAME_TITLE = "AZTEC WASH Mapper";
    private static final int SIDEPANEL_WIDTH = 250;

    //Map scale
    public static final int MAP_SCALE_X = 3355; // Map width in feet
    public static final int MAP_SCALE_Y = 1780; // Map height in feet

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
    private List<Location> route;

    private HashSet<Location> namedLocations;

    private JLabel startInfo;
    private JLabel endPointInfo;
    private JTextArea routeInfo;

    private JTextArea gps;
    private JTextField emailText;
    private String locToSearch;
    private String emailToSend;

    private JButton clearButton;
    private JButton makeAStarRoute;
    private JButton searchButton;
    private JButton emailButton;


    private JComboBox searchDropDownList;
    private JLabel searchInfo;
    private JLabel endLocInfo;
    private JButton addToStart;
    private JButton addToDestination;
    private JComboBox multipleDestination;
    private Location tempLoc;
    private List<Location> multiLoc;
    private int desNum;


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

    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     */
    public MainAppUI(LocationGraph graph) {
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
                } catch (SQLException exception) {
                    JOptionPane.showMessageDialog(mapView.getParent(),
                            "Failed to connect to the online database (be on the internet!)",
                            "Database error!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            resetMap(mapView);
        });
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
                resetMap(this.mapView);
                clearState(mapView);
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
                    for (String str : instruct.stepByStepInstruction(routeTime, MAP_SCALE_X, MAP_SCALE_Y)) {
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

        desNum = 0;
        namedLocations = graph.getNamedLocations();
        searchDropDownList = new SearchComboBox(namedLocations);
        searchDropDownList.setEditable(true);
        searchDropDownList.setPreferredSize(new Dimension(180, 30));
        searchDropDownList.setMaximumSize(new Dimension(180, 30));


        searchInfo = new JLabel("Search: ");
        endLocInfo = new JLabel("Destinations: ");
        multipleDestination = new JComboBox();
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
                //searchDropDownList.removeAllItems();
                //searchDropDownList.setSelectedItem("");
                //searchDropDownList.setPopupVisible(false);
            }
            if (multiLoc.size() > 1){
                makeAStarRoute.setEnabled(true);
            }
        });

        searchDropDownList.addActionListener(e -> {
            resetMap(mapView);
            String selectedName = (String) searchDropDownList.getSelectedItem();
            //System.out.println(selectedName);
            if (searchExactName(selectedName) != null) {
                searchDropDownList.removeAllItems();
                searchDropDownList.addItem(selectedName);
                tempLoc = searchExactName(selectedName);
                locToSearch = selectedName;
            }
            /*if (searchSelectedName(selectedName) != null) {
                searchDropDownList.removeAllItems();
                searchDropDownList.addItem(selectedName);
            }*/
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
            }
            if (multiLoc.size() > 1){
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
                        List<String> instructions =
                                instruct.stepByStepInstruction(route, MAP_SCALE_X, MAP_SCALE_Y);
                        Email email = new Email(emailToSend, instructions);
                        for (int i = 0; i < route.size(); i++) {
                            mapView.stepByStep(i, true, true);
                            mapView.clearFromSearchList();
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

        EdgeWeightMenu edgeWeightPanel = new EdgeWeightMenu(attributeManager);
        JScrollPane text = new JScrollPane(gps);
        text.setPreferredSize(new Dimension(300, 200));
        text.setMaximumSize(new Dimension(300, 200));


        JCheckBox textToVoice = new JCheckBox("Audio: Read step-by-step directions");
        textToVoice.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (textToVoice.isSelected()){
                    voiceDirections = true;
                } else {
                    voiceDirections = false;
                }
            }
        });

        //Add elements to the search panel
        sidePanel.add(searchInfo);
        sidePanel.add(searchDropDownList);
        sidePanel.add(new JLabel(""), FlowLayout.RIGHT);
        sidePanel.add(addToStart);
        sidePanel.add(addToDestination);
        sidePanel.add(Box.createHorizontalStrut(10));
        sidePanel.add(startInfo);
        sidePanel.add(Box.createHorizontalStrut(10));
        sidePanel.add(endLocInfo);
        sidePanel.add(multipleDestination);
        sidePanel.add(Box.createHorizontalStrut(10));
        sidePanel.add(makeAStarRoute);
        sidePanel.add(clearButton, BorderLayout.SOUTH);
        sidePanel.add(text);
        sidePanel.add(stepBackOnRouteButton);
        sidePanel.add(stepForwardOnRouteButton);
        sidePanel.add(emailText);
        sidePanel.add(emailButton);
        sidePanel.add(textToVoice);
        //sidePanel.add(edgeWeightPanel);

        sidePanel.add(handicapCheckbox);
        sidePanel.add(editRoutePrefs);


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
        emailButton.setEnabled(false);

        clearButton.setEnabled(false);

        multipleDestination.removeAllItems();

        gps.setText("");

        resetMap(toReset);
    }

    private ActionListener buildRouteSelectListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

                if (startPoint == null) {
                    multiLoc.add(clickedLocation);
                    startPoint = clickedLocation;
                    route.add(clickedLocation);
                    ((LocationButton) e.getSource()).setBgColor(mapView.getStyle().getStartPointColor());
                    if (clickedLocation.getNameList().length == 0){
                        startInfo.setText("Start Point:  Unnamed Location");
                    } else { startInfo.setText("Start Point:  " + clickedLocation.getNameList()[0]); }

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

    public MapView getMapView(){ return mapView;  }

    private Location searchSelectedName(String selectedName){
        Location result = null;
        try {
            //if there is no entering
            if (selectedName.length() > 0) {
                //search the name in nameLList for all locations in the graph
                List<Location> loc = graph.searchLocationByName(selectedName);
                result = addSearchToView(loc);
            }
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Enter a Location to Search."); //handle NullPointerException
        }
        return result;
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

}
