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

    private float unnamedButtonSize;
    private float namedButtonSize;
    private float startOrEndButtonSize;
    private Color edgeColor;
    private Color routeColor;
    private Color locationColor;
    private Color routeLocationColor;
    private Color startPointColor;
    private Color endPointColor;
    private Color selectedPointColor;
    private Color previousSelectedColor;
    private Color edgeHighlightColor;

    public MapViewStyle(boolean drawAllEdges, boolean drawAllPoints, boolean drawNamedPoints,
                        boolean drawRoutes, float unnamedButtonSize, float namedButtonSize,
                        float startOrEndButtonSize, Color edgeColor, Color routeColor,
                        Color locationColor, Color routeLocationColor, Color startPointColor,
                        Color endPointColor, Color selectedPointColor, Color previousSelectedColor,
                        Color edgeHighlightColor) {
        this.drawAllEdges = drawAllEdges;
        this.drawAllPoints = drawAllPoints;
        this.drawNamedPoints = drawNamedPoints;
        this.drawRoutes = drawRoutes;
        this.unnamedButtonSize = unnamedButtonSize;
        this.namedButtonSize = namedButtonSize;
        this.startOrEndButtonSize = startOrEndButtonSize;
        this.edgeColor = edgeColor;
        this.routeColor = routeColor;
        this.locationColor = locationColor;
        this.routeLocationColor = routeLocationColor;
        this.startPointColor = startPointColor;
        this.endPointColor = endPointColor;
        this.selectedPointColor = selectedPointColor;
        this.previousSelectedColor = previousSelectedColor;
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

    public void setNamedButtonSize(float namedButtonSize) {
        this.namedButtonSize = namedButtonSize;
    }

    public void setUnnamedButtonSize(float unnamedButtonSize) {
        this.unnamedButtonSize = unnamedButtonSize;
    }

    public void setStartOrEndButtonSize(float startOrEndButtonSize) {
        this.startOrEndButtonSize = startOrEndButtonSize;
    }

    public Color getEdgeColor() {
        return edgeColor;
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

    public float getUnnamedButtonSize() {
        return unnamedButtonSize;
    }

    public float getNamedButtonSize() {
        return namedButtonSize;
    }

    public float getStartOrEndButtonSize() {
        return startOrEndButtonSize;
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

    public Color getStartPointColor() {
        return startPointColor;
    }

    public Color getEndPointColor() {
        return endPointColor;
    }

    public Color getSelectedPointColor() {
        return selectedPointColor;
    }

    public Color getPreviousSelectedColor() {
        return previousSelectedColor;
    }

    public Color getEdgeHighlightColor() {
        return edgeHighlightColor;
    }
}
