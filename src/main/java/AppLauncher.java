import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;
import ui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a baby easy example of a swing app. This can turn into the real
 * launcher for our app probably.
 */
public class AppLauncher{
    /**
     * This method just always returns the name of our team.
     * We test this method with JUnit in src/test/java/TestPlaceholder.java
     *
     * @return The name of our amazing team!
     */
    public static String getTeamName() {
        return "AZTEC WASH!";
    }

    public static void main(String[] args) {
        //Make a frame
        JFrame frame = new JFrame(getTeamName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));

        //Make a panel for the frame, and give it a basic layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //Add stuff to the panel
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel(getTeamName());
            mainPanel.add(label);
        }

        //Make a test graph
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(10, 10), 0, new String[0]);

        graph.addLocation(loc1, new HashMap<>());

        HashMap<Location, List<EdgeAttribute>> connections = new HashMap<>();
        connections.put(loc1, new ArrayList<>());
        graph.addLocation(loc2, connections);

        Location loc3 = new Location(new Point2D.Double(10, 30), 0, new String[0]);
        HashMap<Location, List<EdgeAttribute>> connections2 = new HashMap<>();
        connections2.put(loc2, new ArrayList<>());
        graph.addLocation(loc3, connections2);

        Location loc4 = new Location(new Point2D.Double(5, 60), 0, new String[0]);
        HashMap<Location, List<EdgeAttribute>> connections3 = new HashMap<>();
        connections3.put(loc3, new ArrayList<>());
        connections3.put(loc1, new ArrayList<>());
        graph.addLocation(loc4, connections3);

        //Make the map
        JPanel mapView = new MapView(graph, "src/main/resources/campusmap.png");

        JScrollPane mapScrollPane = new JScrollPane(mapView);
        mapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(mapScrollPane);

        frame.repaint();
        frame.pack();

        //Show the frame
        frame.setVisible(true);
    }
}
