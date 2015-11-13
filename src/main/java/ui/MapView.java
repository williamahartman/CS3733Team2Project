package ui;

import core.Edge;
import core.EdgeAttribute;
import core.LocationGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This is a panel that displays edges from a map. An image background is displayed bellow
 * these edges.
 */
public class MapView extends JPanel{
    private java.util.List<Edge> graphEdgeList;
    private BufferedImage mapBackground;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param mapBackgroundImagePath The path to the image that will be used as the background
     */
    public MapView(LocationGraph graph, String mapBackgroundImagePath) {
        super(true);
        resetEdges(graph);
        try {
            mapBackground = ImageIO.read(new File(mapBackgroundImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the map to display the edges of the passed graph.
     *
     * @param graph The graph whose edges will be displayed
     */
    public final void resetEdges(LocationGraph graph) {
        this.graphEdgeList = graph.getAllEdges();
    }

    /**
     * Draw the edges contained in this MapView, as well as the background.
     *
     * @param g The graphics object used to draw the edges and the background
     */
    @Override
    public void paint(Graphics g) {
        //Draw background if loaded
        if (mapBackground != null) {
            g.drawImage(mapBackground, 0, 0, null);
        }

        for (Edge e: graphEdgeList) {
            if (e.hasAttribute(EdgeAttribute.EDGE_REMOVED)) {
                g.setColor(Color.red);
            } else if (e.hasAttribute(EdgeAttribute.INDOORS)) {
                g.setColor(Color.blue);
            } else if (e.hasAttribute(EdgeAttribute.OUTDOORS)) {
                g.setColor(Color.green);
            }  else {
                g.setColor(Color.black);
            }

            Point2D.Double loc1 = e.getNode1().getPosition();
            Point2D.Double loc2 = e.getNode2().getPosition();
            g.drawLine((int) loc1.x, (int) loc1.y, (int) loc2.x, (int) loc2.y);
        }
    }
}
