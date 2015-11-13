package testcore;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class holds test cases for edge. Make one test method per actual method.
 */
public class LocationGraphTest {
    @Test
    public void addLocationTests() {
        //Add a first point
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        graph.addLocation(loc1, new HashMap<>());

        assertEquals("loc1 is on graph", true, graph.getAllLocations().contains(loc1));

        /*
         Add another point connected to loc1, check that the edge is there, check that the
         attributes are there
         */
        Location loc2 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        HashMap<Location, List<EdgeAttribute>> connections = new HashMap<>();
        connections.put(loc1, Arrays.asList(EdgeAttribute.INDOORS, EdgeAttribute.OUTDOORS));
        graph.addLocation(loc2, connections);

        assertEquals("loc1 and loc2 are on graph", true,
                graph.getAllLocations().contains(loc1) && graph.getAllLocations().contains(loc2));
        assertEquals("There is an edge between loc1 and loc2", loc1.getEdges(), loc2.getEdges());
        Edge shared = loc1.getEdges().get(0);
        assertEquals("The edge between loc1 and loc2 has the right attributes", true,
                shared.hasAttribute(EdgeAttribute.INDOORS) &&
                        shared.hasAttribute(EdgeAttribute.OUTDOORS));
    }

    @Test
    public void getAllEdgesTests() {
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0, 0), 0, new String[0]);

        graph.addLocation(loc1, new HashMap<>());

        HashMap<Location, List<EdgeAttribute>> connections = new HashMap<>();
        connections.put(loc1, new ArrayList<>());
        graph.addLocation(loc2, connections);

        //Test that a single edge returns, and that it is the right edge
        assertEquals("Returns the shared edge", loc1.getEdges(), graph.getAllEdges());

        //Add another edge, acyclic
        Location loc3 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        HashMap<Location, List<EdgeAttribute>> connections2 = new HashMap<>();
        connections2.put(loc2, new ArrayList<>());
        graph.addLocation(loc3, connections2);

        List<Edge> result = graph.getAllEdges();
        assertEquals("Edges returned are correct for 3 member acyclic graph", true,
                (result.size() == 2) &&
                        result.contains(loc1.getEdges().get(0)) &&
                        result.contains(loc3.getEdges().get(0)));

        //Add another edge, acyclic
        Location loc4 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        HashMap<Location, List<EdgeAttribute>> connections3 = new HashMap<>();
        connections3.put(loc3, new ArrayList<>());
        connections3.put(loc1, new ArrayList<>());
        graph.addLocation(loc4, connections3);

        assertEquals("Correct number of edges returned for 4 member cyclic graph", 4,
                graph.getAllEdges().size());
    }

    @Test
    public void searchLocationByNameTests() {
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[] {"A"});

        //In single item graph search for present and absent strings
        graph.addLocation(loc1, new HashMap<>());
        assertEquals("Search for A (1 item)", new ArrayList<>(Arrays.asList(loc1)),
                graph.searchLocationByName("A"));
        assertEquals("Search for A (1 item)", new ArrayList<>(),
                graph.searchLocationByName("X"));

        Location loc2 = new Location(new Point2D.Double(0, 0), 0, new String[] {"ATest", "B"});
        HashMap<Location, List<EdgeAttribute>> connections = new HashMap<>();
        connections.put(loc1, new ArrayList<>());

        //In a multi-item graph, search for present and absent strings
        graph.addLocation(loc2, connections);
        assertEquals("Search for A (2 item)", true,
                graph.searchLocationByName("A").contains(loc1) &&
                        graph.searchLocationByName("A").contains(loc2));
        assertEquals("Search for X (2 item)", new ArrayList<>(),
                graph.searchLocationByName("X"));
    }

    @Test
    public void filterOutAttributeTests() {
        //TODO write tests for this, or change how it works
    }

    @Test
    public void makeAStarRouteTests() {
        //Hah I dunno...
    }

    @Test
    public void saveToDiskTests() {
        //Hah I dunno...
    }
}
