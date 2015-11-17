package ui;

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
    private MapView mapView;

    private Location startPoint;
    private Location endPoint;

    private JLabel startInfo;
    private JLabel endPointInfo;

    /**
     * Constructor.
     *
     * @param graph The graph that will be shown
     * @param backgroundImagePath The path to the image that will be used as a background
     */
    public MainAppUI(LocationGraph graph, String backgroundImagePath) {
        this.mapView = new MapView(graph, backgroundImagePath);

        startPoint = null;
        endPoint = null;
    }

    private void addListenersToMapNodes() {
        for (LocationButton button: mapView.getLocationButtonList()) {
            button.addActionListener(e -> {
                Location clickedLocation = ((LocationButton) e.getSource()).getAssociatedLocation();

                if (startPoint == null) {
                    startPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.GREEN);
                    startInfo.setText("Starting from: " + clickedLocation.getPosition());
                } else if (endPoint == null) {
                    endPoint = clickedLocation;
                    ((JButton) e.getSource()).setBackground(Color.RED);
                    endPointInfo.setText("Going to: " + clickedLocation.getPosition());
                }
            });
        }
    }

    private void clearState() {
        startPoint = null;
        endPoint = null;

        mapView.redrawButtons();
        addListenersToMapNodes();

        startInfo.setText("Click a Node");
        endPointInfo.setText("Click a Node");
    }

    /**
     * Populate the JFrame with panels used for the applcation.
     */
    public void setUpMainApp() {
        //Initialize Panels and buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        startInfo = new JLabel();
        endPointInfo = new JLabel();

        JButton clearButton = new JButton("Clear start point and destination");
        clearButton.addActionListener(e -> clearState());

        JButton makeAStarRoute = new JButton("Find the shortest route");
        makeAStarRoute.addActionListener(e -> {
            if (startPoint != null && endPoint != null && startPoint != endPoint) {
                System.out.println("Make route between " + startPoint + " and " + endPoint);
                clearState();
            }
        });

        sidePanel.add(startInfo);
        sidePanel.add(endPointInfo);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(clearButton);
        sidePanel.add(makeAStarRoute);

        /*
        Setup for the mapView
         */
        clearState();

        //Make the scroll pane, set up click and drag
        JScrollPane mapScrollPane = new JScrollPane(mapView);
        mapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            int mouseStartX = 0;
            int mouseStartY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                JViewport viewPort = mapScrollPane.getViewport();
                Point vpp = viewPort.getViewPosition();
                vpp.translate(mouseStartX - e.getXOnScreen(), mouseStartY - e.getYOnScreen());
                mapView.scrollRectToVisible(new Rectangle(vpp, viewPort.getSize()));

                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                mapView.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                mapScrollPane.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mapScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        };
        mapScrollPane.getViewport().addMouseListener(mouseAdapter);
        mapScrollPane.getViewport().addMouseMotionListener(mouseAdapter);
        mapScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setLayout(new BorderLayout());
        add(mapScrollPane);
        add(sidePanel, BorderLayout.EAST);
    }
}
