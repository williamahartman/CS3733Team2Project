package ui;

import java.awt.*;

/**
 * This class holds style values for a MapView
 */
public class MapViewSyle {
    private boolean drawAllEdges = true;
    private boolean drawAllPoints = true;
    private boolean drawNamedPoints = true;
    private boolean drawRoutes = true;

    private Color edgeColor;
    private Color routeColor;
    private Color locationColor;
    private Color routeLocationColor;

    public MapViewSyle(boolean drawAllEdges, boolean drawAllPoints, boolean drawNamedPoints,
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

    public Color getEdgeColor() {
        return edgeColor;
    }

    public Color getRouteColor() {
        return routeColor;
    }

    public Color getLocationColor() {
        return locationColor;
    }

    public Color getRouteLocationColor() {
        return routeLocationColor;
    }
}
