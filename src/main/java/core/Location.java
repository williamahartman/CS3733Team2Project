package core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Location for a LocationGraph. These are the nodes in the graph
 * that represents the map.
 *
 * The most important piece of data is the list of associated Edges. All other class data is
 * intended for searching for and displaying information about Locations.
 */
public class Location {
    private Point2D.Double position;
    private int floorNumber;
    private String[] nameList;
    private List<Edge> edgeList;

    /**
     * Constructor. This is intended for loading a Location from a file.
     *
     * @param position The position of the Location on the map (as a fraction of the width and height of the map)
     * @param floorNumber The floor the Location will be placed on. Should be 0 for outdoor points.
     * @param nameList The list of searchable names for the Location
     * @param edgeList The list of Edges that include the Location as one of their nodes.
     */
    public Location(Point2D.Double position, int floorNumber, String[] nameList, List<Edge> edgeList) {
        this.position = position;
        this.floorNumber = floorNumber;
        this.nameList = nameList;
        this.edgeList = edgeList;
    }

    /**
     * Constructor.
     *
     * @param position The position of the Location on the map (as a fraction of the width and height of the map)
     * @param floorNumber The floor the Location will be placed on. Should be 0 for outdoor points.
     * @param nameList The list of searchable names for the Location
     */
    public Location(Point2D.Double position, int floorNumber, String[] nameList) {
        this(position, floorNumber, nameList, new ArrayList<Edge>());
    }

    /**
     * Returns whether or not any of the associated names for the location INCLUDE the search string.
     * (as a substring or an equivalent String).
     *
     * @param searchName The string that is searched for
     * @return Whether or not the searched name is included in the associated names.
     */
    public boolean namesInclude(String searchName) {
        for(String s: nameList) {
            if(s.contains(searchName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Make the current Location adjacent to the passed location on the graph. This method will
     * build an edge between the two points.
     *
     * @param nextLocation The Location the current Location will become adjecent to
     * @param edgeAttributes The list of EdgeAttributes that will be applied to the new edge
     */
    public void makeAdjacentTo(Location nextLocation, EdgeAttribute[] edgeAttributes) {
        System.out.println("(Location.Java line 66) Location: " + nextLocation + " would be added if this were" +
                " implemented!");
        //todo implement this!
    }

    /**
     * Removes the edge from the current Location and the other Location associated with that edge.
     *
     * @param toRemove The edge that will be removed from the graph
     */
    public void removeEdge(Edge toRemove) {
        System.out.println("(Location.Java line 76) Edge: " + toRemove + " would be removed if this were implemented!");
        //todo implement this!
    }

    /**
     * Magically save everything somehow.
     */
    public void saveToDisk() {
        System.out.println("(Location.Java line 89) Location: " + this + " would be saved to disk if this were" +
                " implemented!");
        //todo implement this!
    }

    public List<Edge> getEdges() {
        return edgeList;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Point2D.Double getPosition() {
        return position;
    }
}
