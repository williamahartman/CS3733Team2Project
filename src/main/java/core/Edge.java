package core;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an Edge in a LocationGraph. It is associated with two Locations
 * and a list of EdgeAttributes.
 */
public class Edge {
    private Location node1;
    private Location node2;
    private List<EdgeAttribute> associatedAttributes;

    /**
     * Constructor.
     *
     * @param node1 The first Location associated with the Edge
     * @param node2 The second Location associated with the Edge
     * @param associatedAttributes The list of EdgeAttributes associated with the Edge
     */
    public Edge(Location node1, Location node2, List<EdgeAttribute> associatedAttributes) {
        this.node1 = node1;
        this.node2 = node2;
        this.associatedAttributes = new ArrayList<>(associatedAttributes);
    }

    /**
     * Calculate the "cost" of moving across this edge, using the modifier values from the
     * passed EdgeAttributeManager.
     *
     * @param attributeManager The EdgeAttributeManager that stores modifiers for the graph
     * @return The "cost" of moving across the edge
     */
    public double getCost(EdgeAttributeManager attributeManager) {
        //Initially set the cost to the distance between the points
        double cost = node1.getPosition().distance(node2.getPosition());

        //Iterate over the associated EdgeAttributes, multiplying the cost by their associated costs
        for (EdgeAttribute e: associatedAttributes) {
            cost *= attributeManager.getModifierFromAttribute(e);
        }

        return cost;
    }

    /**
     * Return whether or not the Edge is associated with the passed EdgeAttribute type.
     *
     * @param searchAttribute The EdgeAttribute that is searched for
     * @return Whether or not the Edge is associated with the passed EdgeAttribute type.
     */
    public boolean hasAttribute(EdgeAttribute searchAttribute) {
        return associatedAttributes.contains(searchAttribute);
    }

    /**
     * Makes the current Edge become associated with a passed EdgeAttribute.
     *
     * @param attribute The EdgeAttribute that will be associated with this Edge
     */
    public void addAttribute(EdgeAttribute attribute) {
        associatedAttributes.add(attribute);
    }

    /**
     * Makes the current Edge no longer associated with a passed EdgeAttribute.
     *
     * @param attribute The EdgeAttribute that will be disassociated with this Edge
     */
    public void removeAttribute(EdgeAttribute attribute) { associatedAttributes.remove(attribute); }

    /**
     * Return the first location associated with this edge.
     *
     * @return The first location associated with this edge
     */
    public Location getNode1() {
        return node1;
    }

    /**
     * Return the second location associated with this edge.
     *
     * @return The second location associated with this edge
     */
    public Location getNode2() {
        return node2;
    }

    /**
     * Returns the full list of attributes associated with the edge.
     *
     * @return The list of attributes
     */
    public List<EdgeAttribute> getAttributes() { return associatedAttributes; }

}
