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
public class EdgeTest {
    public void getCostTests() {
        //TODO Write some!
    }

    @Test
    public void hasAttributeTests() {
        Location empty = new Location(new Point2D.Double(0, 0), 0, new String[0]);

        Edge testEdge1 = new Edge(empty, empty, new ArrayList<>());
        Edge testEdge2 = new Edge(empty, empty, Arrays.asList(EdgeAttribute.INDOORS));
        Edge testEdge3 = new Edge(empty, empty, Arrays.asList(EdgeAttribute.OUTDOORS,
                EdgeAttribute.INDOORS, EdgeAttribute.EDGE_REMOVED));

        assertEquals("Edge1 for EDGE_REMOVED", false,
                testEdge1.hasAttribute(EdgeAttribute.EDGE_REMOVED));
        assertEquals("Edge2 for EDGE_REMOVED", false,
                testEdge2.hasAttribute(EdgeAttribute.EDGE_REMOVED));
        assertEquals("Edge2 for INDOORS", true,
                testEdge2.hasAttribute(EdgeAttribute.INDOORS));
        assertEquals("Edge3 for HANDICAP_ACCESSIBLE", false,
                testEdge3.hasAttribute(EdgeAttribute.HANDICAP_ACCESSIBLE));
        assertEquals("Edge3 for OUTDOORS", true,
                testEdge3.hasAttribute(EdgeAttribute.OUTDOORS));
        assertEquals("Edge3 for INDOORS", true,
                testEdge3.hasAttribute(EdgeAttribute.INDOORS));
        assertEquals("Edge3 for EDGE_REMOVED", true,
                testEdge3.hasAttribute(EdgeAttribute.EDGE_REMOVED));
    }

    @Test
    public void addAttributeTests() {
        Location empty = new Location(new Point2D.Double(0, 0), 0, new String[0]);

        Edge testEdge1 = new Edge(empty, empty, new ArrayList<>());
        testEdge1.addAttribute(EdgeAttribute.EDGE_REMOVED);

        assertEquals("Edge1 for EDGE_REMOVED", true,
                testEdge1.hasAttribute(EdgeAttribute.EDGE_REMOVED));
    }

    @Test
    public void saveToDiskTests() {
        //Hah I dunno...
    }
}
