package database;

import core.Edge;
import core.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of what elements were added or removed from a location.
 * These changes can then be applied to the database later.
 */

public class DatabaseList {
    List<Location> addLocList = new ArrayList<>();
    List<Location> removeLocList = new ArrayList<>();
    List<Location> updateLocList = new ArrayList<>();
    List<Edge> addEdgeList = new ArrayList<>();
    List<Edge> removeEdgeList = new ArrayList<>();
    List<Edge> updateEdgeList = new ArrayList<>();

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
        if (!addEdgeList.contains(eRem)) {
            removeEdgeList.add(eRem);
        } else {
            addEdgeList.remove(eRem);
        }
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
