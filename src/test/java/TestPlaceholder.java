import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import core.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
    public void testAstar()
    {
       LocationGraph a = TestGraphMaker.makeTestGraph();
        List<Location> path = a.makeAStarRoute
                (new EdgeAttributeManager(), a.getAllLocations().get(0), a.getAllLocations().get(2));
        List<Location> test;
        test = new LinkedList<>();
        //test.add(a.getAllLocations().get(3));
        test.add(a.getAllLocations().get(2));
        test.add(a.getAllLocations().get(1));
        test.add(a.getAllLocations().get(0));
        assertEquals(test, path);

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
