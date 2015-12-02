package ui;

import java.awt.*;
import java.util.HashMap;

/**
 * This class holds style values for a MapView.
 */
public class MapViewStyle {
    private boolean drawAllEdges = true;
    private boolean drawAllPoints = true;
    private boolean drawNamedPoints = true;
    private boolean drawRoutes = true;

    private Color edgeColor;
    private HashMap<Integer, Color> floorEdgeColors;
    private Color routeColor;
    private Color locationColor;
    private Color routeLocationColor;

    public MapViewStyle(boolean drawAllEdges, boolean drawAllPoints, boolean drawNamedPoints,
                        boolean drawRoutes, Color edgeColor, Color routeColor, Color locationColor,
                        Color routeLocationColor) {
        this.drawAllEdges = drawAllEdges;
        this.drawAllPoints = drawAllPoints;
        this.drawNamedPoints = drawNamedPoints;
        this.drawRoutes = drawRoutes;
        this.edgeColor = edgeColor;
        this.routeColor = routeColor;
        this.locationColor = locationColor;
        this.routeLocationColor = routeLocationColor;

        floorEdgeColors = new HashMap<>();
    }

    public boolean isDrawAllEdges() {
        return drawAllEdges;
    }

    public boolean isDrawAllPoints() {
        return drawAllPoints;
    }

    public boolean isDrawNamedPoints() {
        return drawNamedPoints;
    }

    public boolean isDrawRoutes() {
        return drawRoutes;
    }

    public void setDrawAllEdges(boolean drawAllEdges) {
        this.drawAllEdges = drawAllEdges;
    }

    public void setDrawAllPoints(boolean drawAllPoints) {
        this.drawAllPoints = drawAllPoints;
    }

    public void setDrawNamedPoints(boolean drawNamedPoints) {
        this.drawNamedPoints = drawNamedPoints;
    }

    public Color getEdgeColor(int floorNumber) {
        if (floorEdgeColors.containsKey(floorNumber)) {
            return floorEdgeColors.get(floorNumber);
        }

        return edgeColor;
    }

    public Color getRouteColor() { return routeColor; }

    public Color getLocationColor() {
        return locationColor;
    }

    public Color getRouteLocationColor() {
        return routeLocationColor;
    }

    public void setRouteColor(Color routeColor) { this.routeColor = routeColor; }

    public void setLocationColor(Color locationColor) {
        this.locationColor = locationColor;
    }

    public void setEdgeColor(Color edgeColor, int floorNumber) {
        if (floorEdgeColors.containsKey(floorNumber)) {
            floorEdgeColors.replace(floorNumber, edgeColor);
        } else {
            floorEdgeColors.put(floorNumber, edgeColor);
        }
    }

    public void setRouteLocationColor(Color routeLocationColor) { this.routeLocationColor = routeLocationColor; }
}
