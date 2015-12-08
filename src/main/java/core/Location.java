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
     * @param position The position of the Location on the map (as a fraction of the width and
     *                 height of the map)
     * @param floorNumber The floor the Location will be placed on. Should be 0 for outdoor points.
     * @param nameList The list of searchable names for the Location
     * @param edgeList The list of Edges that include the Location as one of their nodes.
     */
    public Location(Point2D.Double position, int floorNumber, String[] nameList,
                    List<Edge> edgeList) {
        this.position = position;
        this.floorNumber = floorNumber;
        this.nameList = nameList;
        this.edgeList = edgeList;
    }

    /**
     * Constructor.
     *
     * @param position The position of the Location on the map (as a fraction of the width and
     *                 height of the map)
     * @param floorNumber The floor the Location will be placed on. Should be 0 for outdoor points.
     * @param nameList The list of searchable names for the Location
     */
    public Location(Point2D.Double position, int floorNumber, String[] nameList) {
        this(position, floorNumber, nameList, new ArrayList<>());
    }

    /**
     * Returns whether or not any of the associated names for the location INCLUDE the search
     * string (as a substring or an equivalent String). Case insensitive.
     *
     * @param searchName The string that is searched for
     * @return Whether or not the searched name is included in the associated names.
     */
    public boolean namesInclude(String searchName) {
        for (String s: nameList) {
            if (s.toLowerCase().contains(searchName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Make the current Location adjacent to the passed location on the graph. This method will
     * build an edge between the two points.
     *
     * @param nextLocation The Location the current Location will become adjacent to
     * @param edgeAttributes The list of EdgeAttributes that will be applied to the new edge
     */

    public Edge makeAdjacentTo(Location nextLocation, List<EdgeAttribute> edgeAttributes) {
        //Don't add an edge if the current Location is passed.
        if (nextLocation == this) {
            return null;
        }

        Edge connectionToNeighbor = getConnectingEdgeFromNeighbor(nextLocation);

        if (connectionToNeighbor == null) {
            Edge e = new Edge(this, nextLocation, edgeAttributes);
            edgeList.add(e);

            //Make the connection two way
            nextLocation.makeAdjacentTo(this, edgeAttributes);
            return e;
        } else {
            //If there is an existing one way connection, take that edge and make it two way
            edgeList.add(connectionToNeighbor);
            return connectionToNeighbor;
        }
    }
    /**
     * Removes the edge from the current Location and the other Location associated with that edge.
     *
     * @param toRemove The edge that will be removed from the graph
     */
    public void removeEdge(Edge toRemove) {
        edgeList.remove(toRemove);
        if (neighborHasEdge(toRemove)) {
            getOtherLoc(toRemove).removeEdge(toRemove);
        }
    }

    /**
     * Returns the node in the passed Location that is not the current object.
     *
     * @param e The other edge
     * @return The node that is not the current node.
     */
    private Location getOtherLoc(Edge e) {
        return (this != e.getNode1() ? e.getNode1() : e.getNode2());
    }

    /**
     * Returns whether or not the neighbor contained in the passed Edge has the passed Edge stored.
     *
     * @param e The edge to search for
     * @return whether or not the neighbor contained in the passed Edge has the passed Edge stored.
     */
    private boolean neighborHasEdge(Edge e) {
        return getOtherLoc(e).getEdges().contains(e);
    }

    /**
     * Returns the Edge contained by the passed Location that connects it to the current location.
     * If it doesn't exist, return null
     *
     * @param otherLoc The Location to search for a connecting edge in
     * @return The connecting edge or null
     */
    public Edge getConnectingEdgeFromNeighbor(Location otherLoc) {
        for (Edge e: otherLoc.getEdges()) {
            if (e.getNode1() == this || e.getNode2() == this) {
                return e;
            }
        }
        return null;
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
    public String[] getNameList(){
        return nameList;
    }
    public void setFloorNumber(int number){
        floorNumber = number;
    }
    public void setNameList(String[] s){
        nameList = s;
    }
    public Location createStairUp(int floor, List<EdgeAttribute> ea)
    {
        Location above = new Location(this.getPosition(), floor, new String[0]);
        this.makeAdjacentTo(above, ea);
        return above;
    }

}
