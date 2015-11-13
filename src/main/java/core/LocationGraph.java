package core;

import java.util.*;

/**
 * A LocationGraph stores a graph of locations and edges. This will hold onto all the data used in
 * the mapping application.
 *
 * This class also handles searching for data associated with Locations, filtering based on
 * EdgeAttributes, and performing the routing algorithm.
 */
public class LocationGraph {
    private HashSet<Location> locationList;

    /**
     * Default constructor.
     */
    public LocationGraph() {
        locationList = new HashSet<>();
    }

    /**
     * Constructor for loading off of a list. This is intended for loading off of a disk
     *
     * @param locationList The list of Locations that will be loaded
     */
    public LocationGraph(Collection<Location> locationList) {
        this.locationList = new HashSet<>();
        this.locationList.addAll(locationList);
    }

    /**
     * Returns a list of locations in order. This list describes the optimal route between the
     * two passed locations.
     *
     * @param attributeManager The attribute manager used to calculate the cost of moving
     *                         across an edge
     * @param start The starting Location
     * @param destination The destination Location
     * @return The list of Locations that comprise an optimal route
     */
    public List<Location> makeAStarRoute(EdgeAttributeManager attributeManager,
                                         Location start, Location destination) {
        ArrayList<Location> result = new ArrayList<>();
        result.add(start);
        return result;
    }

    /**
     * Return a list of locations whose names contain or are equal to the passed String.
     *
     * @param searchString The String that will be searched for.
     * @return The list of Locations whose names contain the search string
     */
    public List<Location> searchLocationByName(String searchString) {
        ArrayList<Location> result = new ArrayList<>();
        locationList.stream().filter(loc -> loc.namesInclude(searchString)).forEach(result::add);

        return result;
    }

    /**
     * Apply the EDGE_REMOVED EdgeAttribute to all edges that are associated with the passed
     * EdgeAttribute.
     *
     * @param searchAttribute The EdgeAttribute whose associated will have EDGE_REMOVED applied.
     */
    public void filterOutAttribute(final EdgeAttribute searchAttribute) {
        //TODO Lets discuss how this should work
        List<Edge> edgeList = getAllEdges();
        edgeList.stream().filter(e -> e.hasAttribute(searchAttribute))
                         .forEach(e -> e.addAttribute(EdgeAttribute.EDGE_REMOVED));
    }

    /**
     * Add a new location to the graph.
     *
     * @param newLocation The new Location that will be added to the graph
     * @param adjacentEdgesWithAttributes An association that represents new edges attached to
     *                                    the new Location. This represented edge extends from
     *                                    newLocation to the key of the Map data structure, and
     *                                    is associated with the attributes contained in the value
     *                                    of the Map data structure.
     */
    public void addLocation(Location newLocation,
                            Map<Location, List<EdgeAttribute>> adjacentEdgesWithAttributes) {
        /*
        Iterate through the passed map, making Edges between the new location and all adjacent
        locations. The new Edges have the attributes associated with the adjacent points through
        the Map data structure.
         */
        for (Location currentAdjacentLocation: adjacentEdgesWithAttributes.keySet()) {
            List<EdgeAttribute> attributes = adjacentEdgesWithAttributes
                    .get(currentAdjacentLocation);
            newLocation.makeAdjacentTo(currentAdjacentLocation, attributes);
        }

        //Add the new point (now with its associate edges) to the list of a points in the graph
        locationList.add(newLocation);
    }

    /**
     * Magically save everything somehow.
     */
    public void saveToDisk() {
        System.out.println("(LocationGraph.Java line 99) LocationGraph: " + this +
                " would be saved to disk if this were implemented!");
        //todo implement this!

        /*
        Also, as a note to whoever sees this first we need to think about whether the EDGE_REMOVED
        EdgeAttribute should get saved. My gut reaction is no, but I'm not sure what the best
        way to implement this is.
         */
    }

    public List<Location> getAllLocations() {
        return new ArrayList<>(locationList);
    }

    public List<Edge> getAllEdges() {
        /*
        We use a Set to prevent adding an edge multiple times. We just iterate though Locations and
        their edges to build the list. This is not great since our graph is pretty sparse.

        We maybe right a faster version of this sometime.
         */
        HashSet<Edge> edgeSet = new HashSet<>();
        getAllLocations().forEach(loc -> loc.getEdges().forEach(edgeSet::add));

        return new ArrayList<>(edgeSet);
    }
}
