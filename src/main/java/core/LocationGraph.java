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
    public List<Location> makeAStarRoute(EdgeAttributeManager attributeManager, Location start, Location destination)
    {
        ArrayList<Location> checkedLocations = new ArrayList<>(); //The List of already checked Locations
        ArrayList<Location> uncheckedLocations = new ArrayList<>(); // The list of Locations yet to be checked
        Map<Location, Location> path = new HashMap<>(); // each Location refers to its previous Location
        Map<Location, ArrayList<Double>> distanceValues = new HashMap<>(); //each Locations stored G,H,F values
        List<Location> finalPath = new ArrayList<>(); //what will be returned
        /**
         * Initialise the path at start Location
         */
        ArrayList<Double> initialValues = new ArrayList<Double>(); // The List of G,H,F for start
        uncheckedLocations.add(start); //initialising uncheckedLocations
        initialValues.add(0.0); //initial G Value
        initialValues.add(start.getPosition().distance(destination.getPosition())); //initial H Value
        initialValues.add((initialValues.get(0) + initialValues.get(1))); //initial F Value
        distanceValues.put(start, initialValues); //start and its corresponding G,H,F values
        while (!uncheckedLocations.isEmpty())
        {
            /**
             * Determining which location to check
             */
            Location current = uncheckedLocations.get(0); // remember Location with smallest F
            double smallestF = distanceValues.get(current).get(2); // remember smallest F Value
            for (Location loc:uncheckedLocations)
            {
                double checkF = distanceValues.get(loc).get(2);
                if (checkF < smallestF)
                {
                    smallestF = checkF;
                    current = loc;
                }
            }
            /**
             *Determining final path
             */
            if (current.equals(destination)) // if you have reached your goal
            {
                Location recurse = current;
                while (recurse != null) //while path has a predecessor, keep recursively adding the previous locations
                {
                    finalPath.add(recurse);
                    recurse = path.get(recurse);
                }
                Collections.reverse(finalPath);
                return finalPath; //returns final path from start to destination
            }
            uncheckedLocations.remove(current); //this location will now be checked so remove it from uncheckedLocations
            checkedLocations.add(current); // this location will now be checked so add it to checkedLocations
            /**
             * Getting all Locations that are adjacent to current
             */

            List<Edge> eList = new ArrayList<>(current.getEdges()); //a list of all edges that current is a part of
            ArrayList<Location> neighbors = new ArrayList<Location>();
            for (Edge e:eList)
            {
                Location loc1 = e.getNode1(); //a possible location
                Location loc2 = e.getNode2(); // a possible location
                if (!loc1.equals(current)) // if the location is not equal to current then add to neighbors
                {
                    neighbors.add(loc1);
                }
                if (!loc2.equals(current)) // if the location is not equal to current then add to neighbors
                {
                    neighbors.add(loc2);
                }

            }
            //System.out.println(neighbors.get(0));
            /**
             * Determining which neighbors to add to uncheckedLocations
             */
            for (Location loc: neighbors)
            {
               // System.out.println(loc);

                if (checkedLocations.contains(loc)) // this location has already been checked
                {
                    //ignore this neighbor
                }
                else
                {
                    ArrayList<Double> locValues = new ArrayList<Double>(); // The List of G,H,F for L
                    double gL = distanceValues.get(current).get(0) +
                            current.getConnectingEdgeFromNeighbor(loc).getCost(attributeManager);
                    // ^initial G Value
                    double hL = loc.getPosition().distance(destination.getPosition()); // H Value of L
                    locValues.add(gL); locValues.add(hL);  // creating the list of values for L
                    double fL = locValues.get(0) + locValues.get(1); //F Value of L
                    locValues.add(fL);

                    if (uncheckedLocations.contains(loc)) //if L is already in uncheckedLocations
                    {
                        if (distanceValues.get(loc).get(2) < fL) // if instance of L already has a lower F
                        {
                            //ignore this neighbor
                        }
                        else
                        {
                            distanceValues.put(loc, locValues); //L and its corresponding G,H,F values
                            path.put(loc, current);
                        }
                    }
                    else
                    {
                        uncheckedLocations.add(loc);
                        distanceValues.put(loc, locValues); //L and its corresponding G,H,F values
                        path.put(loc, current);
                    }
                }
            }
        }
        return finalPath; //a list of locations from start - destination
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
     * Return a list of locations whose are on a certain floor.
     *
     * @param floorNum The int of the floor number
     * @return The list of Locations that are on the correct floor
     */
    public List<Location> locationByFloorNumber (int floorNum)
    {
        List<Location> floor = new ArrayList<>();
        for (Location loc:this.getAllLocations())
        {
            if (loc.getFloorNumber() == floorNum)
            {
                floor.add(loc);
            }
        }
        return floor;
    }
    /**
     * Return a list of Edges whose are on a certain floor.
     *
     * @param floorNum The int of the floor number
     * @return The list of Edges that are on the correct floor
     */
    public List<Edge> edgeByFloorNumber (int floorNum)
    {
        List<Edge> floorEdge = new ArrayList<>();
        List<Location> floorLoc = locationByFloorNumber(floorNum);
        for (Location loc:floorLoc)
        {
            for (Edge ed: loc.getEdges())
            {
                if (!floorEdge.contains(ed))
                {
                    if (ed.getNode1().getFloorNumber() == ed.getNode2().getFloorNumber())
                    {
                        floorEdge.add(ed);
                    }
                }
            }
        }
        return floorEdge;
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
     * Removes the passed Location for the LocationGraph, removing all associated Edges as well.
     *
     * @param loc The Location to remove.
     */
    public void removeLocation(Location loc){
        locationList.remove(loc);
        List<Edge> edgeList = loc.getEdges();
        int n = edgeList.size();
        for (int i = 0; i < n; i++) {
            loc.removeEdge(edgeList.get(0));
        }
    }
    /**
     * Return the list of Locations in the LocationGraph. The order of the Locations in this
     * list may change at anytime!
     *
     * @return The list of Locations in the LocationGraph
     */
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
