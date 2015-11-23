import ui.MainAppUI;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * This is a baby easy example of a swing app. This can turn into the real
 * launcher for our app probably.
 */

public class AppLauncher{

    public static void main(String[] args) {
        try {
            Database graphData = new Database();

            /*
            DatabaseList dbList = new DatabaseList();
            Location loc1 = new Location(new Point2D.Double(0.1, 0.3), 0, new String[0]);
            Location loc2 = new Location(new Point2D.Double(0.2, 0.6), 0, new String[0]);
            Location loc3 = new Location(new Point2D.Double(0.8, 0.7), 0, new String[0]);
            Location loc4 = new Location(new Point2D.Double(0.6, 0.6), 0, new String[0]);
            Location loc5 = new Location(new Point2D.Double(0.9, 0.6), 0, new String[0]);
            String[] names = {"HL2", "AK116"};
            Location loc6 = new Location(new Point2D.Double(0.5, 0.5), 0, names);
            String[] name = {"WB123", "CC23"};
            Location loc7 = new Location(new Point2D.Double(0.4, 0.1), 0, name);

            loc1.makeAdjacentTo(loc2, new ArrayList<>());
            loc1.makeAdjacentTo(loc7, new ArrayList<>());
            loc2.makeAdjacentTo(loc6, new ArrayList<>());
            loc3.makeAdjacentTo(loc4, new ArrayList<>());
            loc4.makeAdjacentTo(loc6, new ArrayList<>());
            loc4.makeAdjacentTo(loc5, new ArrayList<>());
            loc6.makeAdjacentTo(loc7, new ArrayList<>());

            List<Edge> edge = new ArrayList<>(loc1.getEdges());
            List<Edge> edge1 = new ArrayList<>(loc2.getEdges());
            List<Edge> edge2 = new ArrayList<>(loc3.getEdges());
            List<Edge> edge3 = new ArrayList<>(loc4.getEdges());
            List<Edge> edge4 = new ArrayList<>(loc5.getEdges());
            List<Edge> edge5 = new ArrayList<>(loc6.getEdges());
            List<Edge> edge6 = new ArrayList<>(loc7.getEdges());
            dbList.addedLocation(loc1);
            dbList.addedLocation(loc2);
            dbList.addedLocation(loc3);
            dbList.addedLocation(loc4);
            dbList.addedLocation(loc5);
            dbList.addedLocation(loc6);
            dbList.addedLocation(loc7);
            dbList.removedLocation(loc7);
            Iterator<Edge> iter = edge.iterator();
            while (iter.hasNext()) {
                dbList.addedEdge(iter.next());

            }
            Iterator<Edge> iter1 = edge1.iterator();
            while (iter1.hasNext()) {
                dbList.addedEdge(iter1.next());

            }
            Iterator<Edge> iter2 = edge2.iterator();
            while (iter2.hasNext()) {
                dbList.addedEdge(iter2.next());

            }
            Iterator<Edge> iter3 = edge3.iterator();
            while (iter3.hasNext()) {
                dbList.addedEdge(iter3.next());

            }
            Iterator<Edge> iter4 = edge4.iterator();
            while (iter4.hasNext()) {
                dbList.addedEdge(iter4.next());

            }
            Iterator<Edge> iter5 = edge5.iterator();
            while (iter5.hasNext()) {
                dbList.addedEdge(iter5.next());

            }
            Iterator<Edge> iter6 = edge6.iterator();
            while (iter6.hasNext()) {
                dbList.addedEdge(iter6.next());

            }

            graphData.updateDB(dbList.getAddLocList(), dbList.getRemoveLocList(), dbList.getUpdateLocList(),
                    dbList.getAddEdgeList(), dbList.getRemoveEdgeList(), dbList.getUpdateEdgeList());
*/

            //Make a frame
            MainAppUI app = new MainAppUI(graphData.createGraph(),
                    "campusmap.png");
            graphData.closeConnection();

            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));

            app.setUpMainApp();

            app.repaint();

            //Show the frame
            app.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the online database (be on the internet!)",
                    "Database error!",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
            System.exit(-1);
        }
    }

}
