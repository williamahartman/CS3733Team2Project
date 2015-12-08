package database;

import core.Edge;
import core.Location;

import java.sql.*;
import java.util.*;
import java.awt.geom.Point2D;

/**
 * Created by hollyn on 11/21/15.
 */

public class DatabaseList {
    List<Location> addLocList = new ArrayList<>();
    List<Location> removeLocList = new ArrayList<>();
    List<Location> updateLocList = new ArrayList<>();
    List<Edge> addEdgeList = new ArrayList<>();
    List<Edge> removeEdgeList = new ArrayList<>();
    List<Edge> updateEdgeList = new ArrayList<>();

    public DatabaseList() {

    }

    public void addedLocation(Location locAdd) {
        addLocList.add(locAdd);
    }

    public void removedLocation(Location locRem) {
        removeLocList.add(locRem);
    }

    public void updatedLocation(Location locUpdate) {
        updateLocList.add(locUpdate);
    }

    public void addedEdge(Edge eAdd) {
        addEdgeList.add(eAdd);
    }

    public void removedEdge(Edge eRem) {
        removeEdgeList.add(eRem);
    }

    public void updatedEdge(Edge eUpdate) {
        updateEdgeList.add(eUpdate);
    }

    public List<Location> getAddLocList() { return addLocList; }
    public List<Location> getRemoveLocList() { return removeLocList; }
    public List<Location> getUpdateLocList() { return updateLocList; }
    public List<Edge> getAddEdgeList() { return addEdgeList; }
    public List<Edge> getRemoveEdgeList() { return removeEdgeList; }
    public List<Edge> getUpdateEdgeList() { return updateEdgeList; }
}
