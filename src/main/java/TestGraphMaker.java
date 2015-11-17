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
        Location loc0 = new Location(new Point2D.Double(0.0, 0.0), 0, new String[0]);
        Location loc1 = new Location(new Point2D.Double(0.1, 0.0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0.2, 0.0), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(0.3, 0.0), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(0.6, 0.6), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(0.9, 0.6), 0, new String[0]);
        Location loc6 = new Location(new Point2D.Double(0.5, 0.5), 0, new String[0]);
        Location loc7 = new Location(new Point2D.Double(0.4, 0.1), 0, new String[0]);

        graph.addLocation(loc0, new HashMap<>());
        graph.addLocation(loc1, new HashMap<>());
        graph.addLocation(loc2, new HashMap<>());
        graph.addLocation(loc3, new HashMap<>());
        graph.addLocation(loc4, new HashMap<>());
        graph.addLocation(loc5, new HashMap<>());
        graph.addLocation(loc6, new HashMap<>());
        graph.addLocation(loc7, new HashMap<>());

          loc0.makeAdjacentTo(loc1, new ArrayList<>());
          loc1.makeAdjacentTo(loc2, new ArrayList<>());

          loc2.makeAdjacentTo(loc3, new ArrayList<>());



        return graph;
    }
}
