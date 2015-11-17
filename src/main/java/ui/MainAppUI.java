package ui;

import core.EdgeAttributeManager;
import core.Location;
import core.LocationGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class will build a frame that is pre-populated panels and buttons.
 * This runs the app.
 */
public class MainAppUI extends JFrame{
    private static final int SIDEPANEL_WIDTH = 250;

    private MapView mapView;
    private LocationGraph graph;
    private String backgroundImagePath;

    private Location startPoint;
    private Location endPoint;

    private JLabel startInfo;
    private JLabel endPointInfo;
    private JButton clearButton;
    private JButton makeAStarRoute;

    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     * @param backgroundImagePath The path to the image that will be used as a background
     */
    public MainAppUI(LocationGraph graph, String backgroundImagePath) {
        this.graph = graph;
        this.backgroundImagePath = backgroundImagePath;
        this.mapView = new MapView(graph, backgroundImagePath);

        startPoint = null;
        endPoint = null;
    }

    /**
     * Populate the JFrame with panels used for the applcation.
     */
    public void setUpMainApp() {
        //Initialize Panels and buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

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
        clearButton.addActionListener(e -> clearState());

        makeAStarRoute = new JButton("Find Shortest Route");
        makeAStarRoute.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setMaximumSize(new Dimension(SIDEPANEL_WIDTH, 60));
        makeAStarRoute.setToolTipText("Generate the most efficient possible route between the selected points");
        makeAStarRoute.addActionListener(e -> {
            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                JFrame frame = new JFrame("Route");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setMinimumSize(new Dimension(600, 400));

                AStarRouteDisplay route = new AStarRouteDisplay(graph, backgroundImagePath,
                        graph.makeAStarRoute(new EdgeAttributeManager(), startPoint, endPoint));
                frame.add(addToScrollPane(route));

                frame.setLocationRelativeTo(null);
                frame.repaint();
                frame.setVisible(true);

                clearState();
            } else {
                JOptionPane.showMessageDialog(this,
                        "You must select two points in order to generate a path.",
                        "Routing Error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        //Add elements to the side panel
        sidePanel.add(startInfo);
        sidePanel.add(endPointInfo);
        sidePanel.add(makeAStarRoute);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(clearButton);

        //Build the map view scrollable stuff
        clearState();
        JScrollPane mapScrollPane = addToScrollPane(mapView);

        //Set layout and add
        setLayout(new BorderLayout());
        add(mapScrollPane);
        add(sidePanel, BorderLayout.EAST);
    }

    /**
     * Add the listeners to all the nodes in the map.
     */
    private void addListenersToMapNodes() {
        for (LocationButton button: mapView.getLocationButtonList()) {
            button.addActionListener(e -> {
                Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

                if (startPoint == null) {
                    startPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.GREEN);
                    startInfo.setText("Start Point: ("
                            + clickedLocation.getPosition().x + ", "
                            + clickedLocation.getPosition().y + ")");

                    clearButton.setEnabled(true);
                } else if (endPoint == null) {
                    endPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.RED);
                    endPointInfo.setText("Destination Point: ("
                            + clickedLocation.getPosition().x + ", "
                            + clickedLocation.getPosition().y + ")");

                    clearButton.setEnabled(true);
                    makeAStarRoute.setEnabled(true);
                }
            });
        }
    }

    /**
     * Add a passed panel to a new JScrollPane, return it.
     *
     * @param panel The panel that will be added
     * @return The JScrollPane containing the panel
     */
    private JScrollPane addToScrollPane(JPanel panel) {
        //Make the scroll pane, set up click and drag
        JScrollPane resultScrollPane = new JScrollPane(panel);
        resultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        resultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            int mouseStartX = 0;
            int mouseStartY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                JViewport viewPort = resultScrollPane.getViewport();
                Point vpp = viewPort.getViewPosition();
                vpp.translate(mouseStartX - e.getXOnScreen(), mouseStartY - e.getYOnScreen());
                panel.scrollRectToVisible(new Rectangle(vpp, viewPort.getSize()));

                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                panel.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                resultScrollPane.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resultScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        };
        resultScrollPane.getViewport().addMouseListener(mouseAdapter);
        resultScrollPane.getViewport().addMouseMotionListener(mouseAdapter);
        resultScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return resultScrollPane;
    }

    /**
     * Clear the state of the App, returning it to the default state.
     */
    private void clearState() {
        startPoint = null;
        endPoint = null;

        mapView.redrawButtons();
        addListenersToMapNodes();

        startInfo.setText("Start Point: Not selected");
        endPointInfo.setText("Destination Point: Not selected");

        makeAStarRoute.setEnabled(false);
        clearButton.setEnabled(false);
    }

}
