import core.Location;
import core.LocationGraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class returns a map you can use to test stuff.
 */
public class TestGraphMaker {

    /**
     * Returns a hardcoded test map.
     *
     * @return a hardcoded test map
     */
    public static LocationGraph makeTestGraph() {
        //Make a test graph
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(0.1, 0.3), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0.2, 0.6), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(0.8, 0.7), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(0.6, 0.6), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(0.9, 0.6), 0, new String[0]);
        Location loc6 = new Location(new Point2D.Double(0.5, 0.5), 0, new String[0]);
        Location loc7 = new Location(new Point2D.Double(0.4, 0.1), 0, new String[0]);

        graph.addLocation(loc1, new HashMap<>());
        graph.addLocation(loc2, new HashMap<>());
        graph.addLocation(loc3, new HashMap<>());
        graph.addLocation(loc4, new HashMap<>());
        graph.addLocation(loc5, new HashMap<>());
        graph.addLocation(loc6, new HashMap<>());
        graph.addLocation(loc7, new HashMap<>());

        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        loc1.makeAdjacentTo(loc7, new ArrayList<>());
        loc2.makeAdjacentTo(loc6, new ArrayList<>());
        loc3.makeAdjacentTo(loc4, new ArrayList<>());
        loc4.makeAdjacentTo(loc6, new ArrayList<>());
        loc4.makeAdjacentTo(loc5, new ArrayList<>());
        loc5.makeAdjacentTo(loc4, new ArrayList<>());
        loc6.makeAdjacentTo(loc7, new ArrayList<>());
        loc7.makeAdjacentTo(loc1, new ArrayList<>());

        return graph;
    }
}
