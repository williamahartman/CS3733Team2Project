package testcore;

import core.EdgeAttributeManager;
import core.Instruction;
import core.Location;
import core.LocationGraph;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the makeAStarRoute() and calculateDistance() in LocationGraph.java.
 */
public class AStarTest {

    @Test
    public void testAstar1() {
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
        test.add(loc0);
        test.add(loc1);
        assertEquals(test, path);
        //Test calculateDistance
        double d1 = graph.calculateDistance(path);
        assertEquals(0.1, d1, 0.0);
        //Test A* algorithm
        List<Location> test2 = new LinkedList<>();
        List<Location> path2 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc2);
        test2.add(loc0);
        test2.add(loc1);
        test2.add(loc2);
        assertEquals(test2, path2);
        //Test calculateDistance
        double d2 = graph.calculateDistance(path2);
        assertEquals(0.2, d2, 0.0);
        //Test A* algorithm
        List<Location> test3 = new LinkedList<>();
        List<Location> path3 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc3);
        test3.add(loc0);
        test3.add(loc1);
        test3.add(loc2);
        test3.add(loc3);
        assertEquals(test3, path3);
        //Test calculateDistance
        double d3 = graph.calculateDistance(path3);
        assertEquals(0.3, d3, 0.0);

        //Test A* algorithm
        List<Location> test4 = new LinkedList<>();
        List<Location> path4 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc4);
        test4.add(loc0);
        test4.add(loc1);
        test4.add(loc2);
        test4.add(loc3);
        test4.add(loc4);
        assertEquals(test4, path4);
        //Test calculateDistance
        double d4 = graph.calculateDistance(path4);
        assertEquals(0.4, d4, 0.0);
    }

    @Test
    public void testAstar2() {
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
        test1.add(loc1);
        test1.add(loc2);
        test1.add(loc6);
        test1.add(loc4);
        test1.add(loc5);
        assertEquals(test1, path1);
        //Test calculateDistance
        double d = graph.calculateDistance(path1);
        assertEquals(10.74, d, 0.0);

        Instruction i = new Instruction();
       // i.stepByStepInstruction(path1);
      //  System.out.println(i.getInstruction());
    }

    @Test
    public void testAstar3() {
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
        test1.add(loc1);
        test1.add(loc3);
        test1.add(loc5);
        assertEquals(test1, path1);
        //Test calculateDistance
        double d1 = graph.calculateDistance(path1);
        assertEquals(5.84, d1, 0.0);

        //Test A* algorithm
        List<Location> test2 = new LinkedList<>();
        List<Location> path2 = graph.makeAStarRoute(new EdgeAttributeManager(), loc3, loc5);
        test2.add(loc3);
        test2.add(loc5);
        assertEquals(test2, path2);
        //Test calculateDistance
        double d2 = graph.calculateDistance(path2);
        assertEquals(3.61, d2, 0.0);
        Instruction i = new Instruction();
        //i.stepByStepInstruction(path1);
       // System.out.println(i.getInstruction());
    }
    @Test
    public void testAstar4()
    {
        LocationGraph graph = new LocationGraph();
        Location loc0 = new Location(new Point2D.Double(0, 0), 0, new String[0]);
        Location loc1 = new Location(new Point2D.Double(3, 0), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(6, 2), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(8, 0), 0, new String[0]);
        Location loc4 = new Location(new Point2D.Double(8, 4), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(6, 5), 0, new String[0]);
        Location loc6 = new Location(new Point2D.Double(1, 2), 0, new String[0]);
        Location loc7 = new Location(new Point2D.Double(3, 3), 0, new String[0]);
        Location loc8 = new Location(new Point2D.Double(1, 6), 0, new String[0]);
        Location loc9 = new Location(new Point2D.Double(3, 7), 0, new String[0]);
        Location loc10 = new Location(new Point2D.Double(4, 5), 0, new String[0]);
        Location loc11 = new Location(new Point2D.Double(5, 6), 0, new String[0]);
        Location loc12 = new Location(new Point2D.Double(5, 9), 0, new String[0]);
        Location loc13 = new Location(new Point2D.Double(6, 7), 0, new String[0]);
        Location loc14 = new Location(new Point2D.Double(8, 5), 0, new String[0]);
        Location loc15 = new Location(new Point2D.Double(8, 8), 0, new String[0]);
        Location loc16 = new Location(new Point2D.Double(10, 6), 0, new String[0]);
        Location loc17 = new Location(new Point2D.Double(8, 5), 0, new String[0]);
        Location loc18 = new Location(new Point2D.Double(10, 0), 0, new String[0]);
        Location loc19 = new Location(new Point2D.Double(8, 7), 0, new String[0]);
        Location loc20 = new Location(new Point2D.Double(6, 5), 0, new String[0]);

        graph.addLocation(loc0, new HashMap<>());
        graph.addLocation(loc1, new HashMap<>());
        graph.addLocation(loc2, new HashMap<>());
        graph.addLocation(loc3, new HashMap<>());
        graph.addLocation(loc4, new HashMap<>());
        graph.addLocation(loc5, new HashMap<>());
        graph.addLocation(loc6, new HashMap<>());
        graph.addLocation(loc7, new HashMap<>());
        graph.addLocation(loc8, new HashMap<>());
        graph.addLocation(loc9, new HashMap<>());
        graph.addLocation(loc10, new HashMap<>());
        graph.addLocation(loc11, new HashMap<>());
        graph.addLocation(loc12, new HashMap<>());
        graph.addLocation(loc13, new HashMap<>());
        graph.addLocation(loc14, new HashMap<>());
        graph.addLocation(loc15, new HashMap<>());
        graph.addLocation(loc16, new HashMap<>());
        graph.addLocation(loc17, new HashMap<>());
        graph.addLocation(loc18, new HashMap<>());
        graph.addLocation(loc19, new HashMap<>());
        graph.addLocation(loc20, new HashMap<>());

        loc0.makeAdjacentTo(loc6, new ArrayList<>());
        loc1.makeAdjacentTo(loc2, new ArrayList<>());
        loc1.makeAdjacentTo(loc6, new ArrayList<>());
        loc1.makeAdjacentTo(loc7, new ArrayList<>());
        loc2.makeAdjacentTo(loc3, new ArrayList<>());
        loc2.makeAdjacentTo(loc4, new ArrayList<>());
        loc2.makeAdjacentTo(loc5, new ArrayList<>());
        loc2.makeAdjacentTo(loc7, new ArrayList<>());
        loc2.makeAdjacentTo(loc10, new ArrayList<>());
        loc3.makeAdjacentTo(loc4, new ArrayList<>());
        loc4.makeAdjacentTo(loc5, new ArrayList<>());
        loc5.makeAdjacentTo(loc10, new ArrayList<>());
        loc6.makeAdjacentTo(loc7, new ArrayList<>());
        loc6.makeAdjacentTo(loc8, new ArrayList<>());
        loc7.makeAdjacentTo(loc10, new ArrayList<>());
        loc8.makeAdjacentTo(loc9, new ArrayList<>());
        loc8.makeAdjacentTo(loc10, new ArrayList<>());
        loc9.makeAdjacentTo(loc10, new ArrayList<>());
        loc9.makeAdjacentTo(loc12, new ArrayList<>());
        loc11.makeAdjacentTo(loc12, new ArrayList<>());
        loc11.makeAdjacentTo(loc13, new ArrayList<>());
        loc11.makeAdjacentTo(loc14, new ArrayList<>());
        loc11.makeAdjacentTo(loc20, new ArrayList<>());
        loc12.makeAdjacentTo(loc13, new ArrayList<>());
        loc12.makeAdjacentTo(loc15, new ArrayList<>());
        loc13.makeAdjacentTo(loc14, new ArrayList<>());
        loc13.makeAdjacentTo(loc15, new ArrayList<>());
        loc13.makeAdjacentTo(loc19, new ArrayList<>());
        loc14.makeAdjacentTo(loc19, new ArrayList<>());
        loc14.makeAdjacentTo(loc20, new ArrayList<>());
        loc15.makeAdjacentTo(loc16, new ArrayList<>());
        loc15.makeAdjacentTo(loc17, new ArrayList<>());
        loc15.makeAdjacentTo(loc19, new ArrayList<>());
        loc16.makeAdjacentTo(loc17, new ArrayList<>());
        loc17.makeAdjacentTo(loc18, new ArrayList<>());
        loc19.makeAdjacentTo(loc20, new ArrayList<>());

        //Test A* algorithm
        List<Location> test1 = new LinkedList<>();
        List<Location> path1 = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc18);
        test1.add(loc0);
        test1.add(loc6);
        test1.add(loc8);
        test1.add(loc9);
        test1.add(loc12);
        test1.add(loc15);
        test1.add(loc17);
        test1.add(loc18);
        assertEquals(test1, path1);
        //Test calculateDistance
        double d1 = graph.calculateDistance(path1);
        assertEquals(22.85, d1, 0.0);
        Instruction i = new Instruction();
        i.stepByStepInstruction(path1);
        System.out.println(i.getInstruction());
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
