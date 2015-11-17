package ui;

import core.Location;
import core.LocationGraph;

import java.awt.*;
import java.util.List;

/**
 * This class displays everything a normal ViewMap object displays, as well as a route.
 * This is route is indicated by a list of locations in the order they are visited.
 */
public class AStarRouteDisplay extends MapView {
    List<Location> route;

    /**
     * Constructor.
     *
     * @param graph The LocationGraph whose edges will be displayed
     * @param mapBackgroundImagePath The path to the image that will be used as the background
     */
    public AStarRouteDisplay(LocationGraph graph, String mapBackgroundImagePath, List<Location> route) {
        super(graph, mapBackgroundImagePath);
        this.route = route;
    }


    /**
     * Draw the edges contained in this AStarRoute.
     *
     * @param g The graphics object used to draw the edges and the background
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.BLUE);


        Dimension imageRes = getImageResolution();

        int previousX = (int) (route.get(0).getPosition().x * imageRes.getWidth());
        int previousY = (int) (route.get(0).getPosition().y * imageRes.getHeight());
        for (int i = 1; i < route.size(); i++) {
            int currentX = (int) (route.get(i).getPosition().x * imageRes.getWidth());
            int currentY = (int) (route.get(i).getPosition().y * imageRes.getHeight());

            g2d.drawLine(previousX, previousY, currentX, currentY);

            previousX = currentX;
            previousY = currentY;
        }
    }
}
