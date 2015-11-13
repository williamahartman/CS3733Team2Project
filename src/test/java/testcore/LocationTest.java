package testcore;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * This class holds test cases for edge. Make one test method per actual method.
 */
public class LocationTest {
    @Test
    public void namesIncludeTests() {
        Location test1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location test2 = new Location(new Point2D.Double(0, 0), 0, new String[] {"A"});
        Location test3 = new Location(new Point2D.Double(0, 0), 0, new String[] {"A1"});
        Location test4 = new Location(new Point2D.Double(0, 0), 0, new String[] {"A", "B"});
        Location test5 = new Location(new Point2D.Double(0, 0), 0, new String[] {"A1", "B"});

        assertEquals("[] for A", false, test1.namesInclude("A"));
        assertEquals("[A] for A", true, test2.namesInclude("A"));
        assertEquals("[A] for B", false, test2.namesInclude("B"));
        assertEquals("[A] for A1", false, test2.namesInclude("A1"));
        assertEquals("[A1] for A", true, test3.namesInclude("A"));
        assertEquals("[A B] for A", true, test4.namesInclude("A"));
        assertEquals("[A B] for B", true, test4.namesInclude("B"));
        assertEquals("[A B] for C", false, test4.namesInclude("C"));
        assertEquals("[A B] for A1", false, test4.namesInclude("A1"));
        assertEquals("[A1 B] for A", true, test5.namesInclude("A"));
        assertEquals("[A1 B] for A1", true, test5.namesInclude("A1"));
    }

    @Test
    public void makeAdjacentToTests() {
        //Testing with no attributes
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        Edge shared1 = loc1.getEdges().get(0);

        assertEquals("Loc1 and Loc2 have same edge", loc1.getEdges().get(0),
                loc2.getEdges().get(0));
        assertEquals("Loc1 and Loc2 are included in that edge", true,
                (shared1.getNode1() == loc1 && shared1.getNode2() == loc2) ||
                        (shared1.getNode1() == loc2 && shared1.getNode2() == loc1));

        //Testing with EdgeAttributes
        Location loc3 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        loc4.makeAdjacentTo(loc3, Arrays.asList(EdgeAttribute.INDOORS, EdgeAttribute.EDGE_REMOVED));
        Edge shared2 = loc3.getEdges().get(0);

        assertEquals("Loc3 and Loc4 have an edge with INDOORS and EDGE_REMOVED", true,
                shared2.hasAttribute(EdgeAttribute.INDOORS) &&
                        shared2.hasAttribute(EdgeAttribute.EDGE_REMOVED));
    }

    @Test
    public void removeEdgeTests() {
        Location loc1 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        Edge shared = loc1.getEdges().get(0);

        loc1.removeEdge(shared);
        assertEquals("Neither Location has the edge stored", false,
                loc1.getEdges().contains(shared) && loc2.getEdges().contains(shared));
    }

    @Test
    public void saveToDiskTests() {
        //Hah I dunno...
    }
}
