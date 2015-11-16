import static org.junit.Assert.*;

import core.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a little example of a test case. Real test cases should be less
 * stupid and more organized.
 */
public class TestPlaceholder {

    /**
     * This is a test that will pass.
     */
    @Test
    public void testCase1() {
        String teamName = AppLauncher.getTeamName();
        assertEquals("AZTEC WASH!", teamName);
    }


    @Test
    public void testAstarInOneLine() {
        LocationGraph graph = new LocationGraph();
        Location loc0 = new Location(new Point2D.Double(0.0, 0.0), 0, new String[0]);
        Location loc1 = new Location(new Point2D.Double(0.1, 0.0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(0.2, 0.0), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(0.3, 0.0), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(0.4, 0.0), 0, new String[0]);
        graph.addLocation(loc0, new HashMap<>());
        graph.addLocation(loc1, new HashMap<>());
        graph.addLocation(loc2, new HashMap<>());
        graph.addLocation(loc3, new HashMap<>());
        graph.addLocation(loc4, new HashMap<>());

        loc0.makeAdjacentTo(loc1, new ArrayList<>());
        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        loc2.makeAdjacentTo(loc3, new ArrayList<>());
        loc3.makeAdjacentTo(loc4, new ArrayList<>());
        //Test A* algorithm
        List<Location> test = new LinkedList<>();
        List<Location> path = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc1);
        test.add(loc1);
        test.add(loc0);
        assertEquals(test, path);
        //Test calculateDistance
        double d1 = graph.calculateDistance(loc0, loc1);
        assertEquals(0.1, d1, 0.0);
        //Test A* algorithm
        List<Location> test2 = new LinkedList<>();
        List<Location> path2 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc2);
        test2.add(loc2);
        test2.add(loc1);
        test2.add(loc0);
        assertEquals(test2, path2);
        //Test calculateDistance
        double d2 = graph.calculateDistance(loc0, loc2);
        assertEquals(0.2, d2, 0.0);
        //Test A* algorithm
        List<Location> test3 = new LinkedList<>();
        List<Location> path3 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc3);
        test3.add(loc3);
        test3.add(loc2);
        test3.add(loc1);
        test3.add(loc0);
        assertEquals(test3, path3);
        //Test calculateDistance
        double d3 = graph.calculateDistance(loc0, loc3);
        assertEquals(0.3, d3, 0.0);

        //Test A* algorithm
        List<Location> test4 = new LinkedList<>();
        List<Location> path4 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc4);
        test4.add(loc4);
        test4.add(loc3);
        test4.add(loc2);
        test4.add(loc1);
        test4.add(loc0);
        assertEquals(test4, path4);
        //Test calculateDistance
        double d4 = graph.calculateDistance(loc0, loc4);
        assertEquals(0.4, d4, 0.0);
    }

    @Test
    public void testAstarInExampleGraph() {
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(1, 3), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(2, 6), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(8, 7), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(6, 6), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(9, 6), 0, new String[0]);
        Location loc6 = new Location(new Point2D.Double(5, 5), 0, new String[0]);
        Location loc7 = new Location(new Point2D.Double(4, 1), 0, new String[0]);

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
        loc6.makeAdjacentTo(loc7, new ArrayList<>());
        //Test A* algorithm
        List<Location> test1 = new LinkedList<>();
        List<Location> path1 = graph.makeAStarRoute(new EdgeAttributeManager(), loc1, loc5);
        test1.add(loc5);
        test1.add(loc4);
        test1.add(loc6);
        test1.add(loc2);
        test1.add(loc1);
        assertEquals(test1, path1);
        //Test calculateDistance
        double d = graph.calculateDistance(loc1, loc5);
        assertEquals(10.74, d, 0.0);
    }

    @Test
    public void testAstar1() {
        LocationGraph graph = new LocationGraph();
        Location loc1 = new Location(new Point2D.Double(1, 1), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(1, 5), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(2, 3), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(3, 2), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(4, 6), 0, new String[0]);

        graph.addLocation(loc1, new HashMap<>());
        graph.addLocation(loc2, new HashMap<>());
        graph.addLocation(loc3, new HashMap<>());
        graph.addLocation(loc4, new HashMap<>());
        graph.addLocation(loc5, new HashMap<>());

        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        loc1.makeAdjacentTo(loc3, new ArrayList<>());
        loc1.makeAdjacentTo(loc4, new ArrayList<>());
        loc2.makeAdjacentTo(loc3, new ArrayList<>());
        loc2.makeAdjacentTo(loc5, new ArrayList<>());
        loc3.makeAdjacentTo(loc4, new ArrayList<>());
        loc3.makeAdjacentTo(loc5, new ArrayList<>());
        loc4.makeAdjacentTo(loc5, new ArrayList<>());

        //Test A* algorithm
        List<Location> test1 = new LinkedList<>();
        List<Location> path1 = graph.makeAStarRoute(new EdgeAttributeManager(), loc1, loc5);
        test1.add(loc5);
        test1.add(loc3);
        test1.add(loc1);
        assertEquals(test1, path1);
        //Test calculateDistance
        double d1 = graph.calculateDistance(loc1, loc5);
        assertEquals(5.84, d1, 0.0);

        //Test A* algorithm
        List<Location> test2 = new LinkedList<>();
        List<Location> path2 = graph.makeAStarRoute(new EdgeAttributeManager(), loc3, loc5);
        test2.add(loc5);
        test2.add(loc3);
        assertEquals(test2, path2);
        //Test calculateDistance
        double d2 = graph.calculateDistance(loc3, loc5);
        assertEquals(3.61, d2, 0.0);
    }



    @Test
    public void testTestCase1() throws Exception {

    }


//    @Test
//    public void testTestAstar() throws Exception {
//
//    }

//    //If you uncomment this, you can see what happens when a test fails
//
//    /**
//     * This is a test that will fail.
//     */
//    @Test
//    public void testCase2() {
//        String teamName = AppLauncher.getTeamName();
//        assertEquals("some other boring team", teamName);
//    }
}
