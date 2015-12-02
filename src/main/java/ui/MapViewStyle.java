package ui;

import java.awt.*;

/**
 * This class holds style values for a MapView.
 */
public class MapViewStyle {
    private boolean drawAllEdges = true;
    private boolean drawAllPoints = true;
    private boolean drawNamedPoints = true;
    private boolean drawRoutes = true;

    private Color edgeColor;
    private Color floor1EdgeColor;
    private Color floor2EdgeColor;
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

    public Color getEdgeColor() {
        if (MainAppUI.floorNumber == 0){
            return edgeColor;
        } else if (MainAppUI.floorNumber == 1){
            return floor1EdgeColor;
        } else if (MainAppUI.floorNumber == 2){
            return floor2EdgeColor;
        } else { return edgeColor; }
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
        if (floorNumber == 0){
            this.edgeColor = edgeColor;
        } else if (floorNumber == 1){
            this.floor1EdgeColor = edgeColor;
        } else if (floorNumber == 2){
            this.floor2EdgeColor = edgeColor;
        }
    }

    public void setRouteLocationColor(Color routeLocationColor) { this.routeLocationColor = routeLocationColor; }
}
