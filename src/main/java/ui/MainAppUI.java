package ui;

import core.EdgeAttributeManager;
import core.Location;
import core.LocationGraph;
import dev.DevTools;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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

    private JButton clearButton;
    private JButton makeAStarRoute;
    public static int floorNumber;

    private DevTools devToolsPanel;

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

        MapViewSyle style = new MapViewSyle(
                true,
                true,
                true,
                true,
                new Color(250, 120, 0),
                new Color(15, 78, 152),
                new Color(255, 240, 0),
                new Color(79, 189, 255));

        this.mapView = new MapView(graph, mapBackground, DEFAULT_ZOOM, style);
        startPoint = null;
        endPoint = null;
    }

    /**
     * Populate the JFrame with panels used for the applcation.
     */
    public void setUpMainApp() {
        //Set up menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");

        JMenuItem refreshMap = new JMenuItem("Refresh Map");
        refreshMap.addActionListener(e -> resetMap(mapView));
        JMenuItem enterDevloperMode = new JMenuItem("Edit Map (Developers Only!)");
        enterDevloperMode.addActionListener(e -> {
            if (!devToolsPanel.getDevMode()) {
                devToolsPanel.setDevMode(true);

                enterDevloperMode.setText("Exit Developer Mode");

                mapView.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        devToolsPanel.devModeCheck(e, mapView.getMapPanel());
                    }
                });
            } else {
                devToolsPanel.setDevMode(false);
                enterDevloperMode.setText("Edit Map (Developers Only!)");
            }

            clearState(mapView);
            devToolsPanel.setVisible(devToolsPanel.getDevMode());
        });
        JMenu view = new JMenu("View");
        JMenuItem toggleEdges = new JMenuItem("Toggle Edges");
        JMenuItem showNodes = new JMenuItem("Show All Locations");
        toggleEdges.addActionListener(e -> {
            MapViewSyle style = mapView.getStyle();
            style.setDrawAllEdges(!style.isDrawAllEdges());

            mapView.repaint();
        });
        showNodes.addActionListener(e -> {
            MapViewSyle style = mapView.getStyle();
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
        view.add(toggleEdges);
        view.add(showNodes);
        editMenu.add(refreshMap);
        editMenu.add(enterDevloperMode);
        menuBar.add(editMenu);
        menuBar.add(view);

        setJMenuBar(menuBar);

        //Initialize Panels and buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        JComboBox<Integer> floorNum = new JComboBox<>();
        floorNum.addItem(0);
        floorNum.addItem(1);
        floorNum.addItem(2);
        floorNum.setSelectedItem(0);
        floorNum.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        floorNum.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));
        floorNum.setToolTipText("Select Floor Number");
        floorNum.setForeground(Color.RED);
        floorNum.setFont(new Font("Arial", Font.BOLD, 20));
        floorNum.addActionListener(e ->{

                if (floorNum.getSelectedItem().equals(0))
                {
                    //display ground map
                    this.mapView.getStyle().setEdgeColor(new Color(0, 0, 0));
                    this.mapView.updateGraph(graph, 0);
                    repaint();
                    floorNumber = 0;

                }
                else if (floorNum.getSelectedItem().equals(1))
                {
                    this.mapView.getStyle().setEdgeColor(new Color(255, 0, 255));
                    this.mapView.updateGraph(graph, 1);
                    repaint();
                    floorNumber = 1;
                }
                else if (floorNum.getSelectedItem().equals(2))
                {
                    this.mapView.getStyle().setEdgeColor(new Color(120, 120, 120));
                    this.mapView.updateGraph(graph, 2);
                    repaint();
                    floorNumber = 2;
                }
            });


        startInfo = new JLabel();
        startInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        startInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

        endPointInfo = new JLabel();
        endPointInfo.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 30));
        endPointInfo.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 30));

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
        sidePanel.add(startInfo);
        sidePanel.add(endPointInfo);
        sidePanel.add(makeAStarRoute);
        sidePanel.add(floorNum);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(clearButton);


        //Set layout and add
        setLayout(new BorderLayout());
        add(mapView);
        add(sidePanel, BorderLayout.WEST);
        devToolsPanel = new DevTools(graph, mapView);
        add(devToolsPanel, BorderLayout.EAST);
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                resetMap(mapView);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                resetMap(mapView);
            }
        });

        clearState(mapView);
    }

    /**
     * Add the listeners to all the nodes in the map.
     */
    private void addListenersToMapNodes(ChangeListener listener) {
        for (LocationButton button: mapView.getLocationButtonList()) {
            button.addChangeListener(listener);
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

        resetMap(toReset);
    }

    /**
     * Reset the state of just the map. (none of the class data)
     *
     * @param toReset the mapView to reset
     */
    private void resetMap(MapView toReset) {
        toReset.updateGraph(graph, 0);
        addListenersToMapNodes(e -> {
            Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

            if (startPoint == null) {
                startPoint = clickedLocation;
                ((JButton) e.getSource()).setBackground(Color.GREEN);
                startInfo.setText("Start Point: ("
                        + clickedLocation.getPosition().x + ", "
                        + clickedLocation.getPosition().y + ")");

                clearButton.setEnabled(true);
            } else if (endPoint == null && clickedLocation != startPoint) {
                endPoint = clickedLocation;
                ((JButton) e.getSource()).setBackground(Color.RED);
                endPointInfo.setText("Destination Point: ("
                        + clickedLocation.getPosition().x + ", "
                        + clickedLocation.getPosition().y + ")");

                clearButton.setEnabled(true);
                makeAStarRoute.setEnabled(true);
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
