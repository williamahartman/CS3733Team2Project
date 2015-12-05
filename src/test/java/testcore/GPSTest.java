package testcore;

import core.EdgeAttributeManager;
import ui.Instruction;
import core.Location;
import core.LocationGraph;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by ziyanding on 11/19/15.
 */
public class GPSTest {
    @Test
    public void test1() {
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
        List<Location> path = graph.makeAStarRoute(new EdgeAttributeManager(), loc0, loc4);
        //Instruction i = new Instruction();
        //i.stepByStepInstruction(path);
        //System.out.println(i.getInstruction());
    }
    @Test
    public void test2(){
        List<Location> listLoc = new ArrayList<>();
        Location loc0 = new Location(new Point2D.Double(1, 1), 0, new String[0]);
        Location loc1 = new Location(new Point2D.Double(2, 2), 0, new String[0]);
        Location loc2 = new Location(new Point2D.Double(3, 1), 0, new String[0]);
        Location loc3 = new Location(new Point2D.Double(3, 5), 0, new String[0]);
        listLoc.add(loc0);
        //Instruction i = new Instruction();
        //i.stepByStepInstruction(listLoc, 1);
        //System.out.println(i.getInstruction());
        listLoc.add(loc1);
        listLoc.add(loc2);
        listLoc.add(loc3);
        Instruction i1 = new Instruction();
        //i1.stepByStepInstruction(listLoc, 1);
        //System.out.println(i1.getInstruction());
        Location loc4 = new Location(new Point2D.Double(4, 5), 0, new String[0]);
        Location loc5 = new Location(new Point2D.Double(5, 5), 0, new String[0]);
        Location loc6 = new Location(new Point2D.Double(6, 5), 0, new String[0]);
        Location loc7 = new Location(new Point2D.Double(6, 4), 0, new String[0]);
        listLoc.add(loc4);
        listLoc.add(loc5);
        listLoc.add(loc6);
        listLoc.add(loc7);
        //Instruction i2 = new Instruction();
        //i2.stepByStepInstruction(listLoc, 1);
        //System.out.println(i2.getInstruction());

    }

}
