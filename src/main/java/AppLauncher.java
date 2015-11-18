import core.Database;
import core.Location;
import core.Edge;
import core.LocationGraph;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

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
        /*
        JFrame frame = new JFrame(getTeamName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Make a panel for the frame, and give it a basic layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //Add stuff to the panel
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel(getTeamName());
            mainPanel.add(label);
        }

        //Add stuff to the frame
        frame.getContentPane().add(mainPanel);
        frame.pack();

        //Show the frame
        frame.setVisible(true);
        */
        try {
            LocationGraph graph = new LocationGraph();
            Database test = new Database();
            Database test1 = new Database();
            test1.closeConnection();

            Location loc1 = new Location(new Point2D.Double(0.1, 0.3), 0, new String[0]);
            Location loc2 = new Location(new Point2D.Double(0.2, 0.3), 0, new String[0]);
            Location loc3 = new Location(new Point2D.Double(0.3, 0.3), 0, new String[0]);
            graph.addLocation(loc1, new HashMap<>());
            graph.addLocation(loc2, new HashMap<>());
            graph.addLocation(loc3, new HashMap<>());
            test.addNode(loc1);
            test.addNode(loc2);
            Edge edge1 = new Edge(loc1, loc2, new ArrayList<>());
            test.addEdge(edge1);
            test.removeEdge(edge1);
            test.removeNode(loc1);
            test.removeNode(loc2);
        } catch (Exception e) {
            System.out.print("\n Exception::\n" + e);
        }
    }

}
