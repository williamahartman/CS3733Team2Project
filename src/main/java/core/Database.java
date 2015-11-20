package core;


import java.sql.*;
import java.util.*;
import java.awt.geom.Point2D;

/**
 * Created by Scott on 11/14/2015.
 * Manage Database connection and queries
 */
public class Database {
    private Connection con = null;

    /**
     * Constructor.
     * Connects to the database
     *
     */
    public Database() throws Exception {
        //create a file to find the absolute path of the project

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" +
                        "aztecwash.cly9e1vwzwlp.us-west-2.rds.amazonaws.com:3306",
                "aztecwash", "aztecwash");

        /*
        TABLES IN THE DATABASE:

        CREATE TABLE mydb.NODES(
            NODE_ID int not null AUTO_INCREMENT,
            POS_X double not null,
            POS_Y double not null,
            FLOOR_NUM int not null,
            PRIMARY KEY (NODE_ID))

        ALTER TABLE mydb.NODES
                ADD CONSTRAINT uniquePosition UNIQUE (POS_X, POS_Y)

        CREATE TABLE mydb.EDGES(
                EDGE_ID int not null AUTO_INCREMENT,
                NODE1_ID int not null,
                NODE2_ID int not null,
                PRIMARY KEY(EDGE_ID))
        ALTER TABLE mydb.EDGES
                ADD CONSTRAINT uniquePosition UNIQUE (NODE1_ID, NODE2_ID)

        CREATE TABLE mydb.NAMES(
                NODE_ID int not null,
                NAME varchar(30) not null)

        CREATE TABLE mydb.EDGE_ATTRIBUTES(
                EDGE_ID int not null,
                ATTRIBUTE varchar(30) not null)
        */
    }

    /*
     * Add a node to the database
     *
     * @param locToAdd The location/node that needs to be added to the database
     */
    public void addNode(Location locToAdd) {
        //// TODO: 11/15/2015 Add in names functionality
        //get the x and y of the node to add
        double x = locToAdd.getPosition().getX();
        double y = locToAdd.getPosition().getY();
        //get the floor number
        int floorNum = locToAdd.getFloorNumber();

        //query for inserting the node into the database
        String query = "INSERT INTO mydb.NODES (POS_X, POS_Y, FLOOR_NUM) VALUES " +
                "(" + x + "," + y + "," + floorNum + ")";
        String getID = "SELECT NODE_ID FROM mydb.NODES WHERE" +
                "POS_X = " + x + " AND POS_Y = " + y;
        try {
            Statement stmt = con.createStatement();
            //execute the query to add the node
            stmt.execute(query);

            //get the node id
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates");
            }
            int nodeId = rs.getInt("NODE_ID");

            //Add location NAMES
            addNames(locToAdd, nodeId);


        } catch (SQLException e) {
            System.out.print("Couldn't add location\n");
            e.getStackTrace();
        }
    }

    /*
     * Remove a node from the database
     *
     * @param locToRem The location/node that needs to be removed to the database
     */
    public void removeNode(Location locToRem) {
        //get the x and y of the location
        double x = locToRem.getPosition().getX();
        double y = locToRem.getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            //get the id of the node that is to be removed
            String getID = "SELECT NODE_ID FROM mydb.NODES WHERE POS_X = "  +
                    x + " and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates");
            }
            int nodeId = rs.getInt("NODE_ID");

            try {
                // Remove the edges that include this node
                String remEdge = "DELETE FROM mydb.EDGES WHERE NODE1_ID = "
                        + nodeId + " or NODE2_ID = " + nodeId;
                //execute query to remove edges
                stmt.execute(remEdge);

                String remNames = "DELETE FROM mydb.EDGE_ATTRIBUTES" +
                        "WHERE EDGE_ID = (SELECT EDGE_ID FROM EDGES WHERE NODE1_ID = " + nodeId +
                        "AND NODE2_ID = " + nodeId + ")";
                stmt.execute(remNames);
            } catch (SQLException e){
                //location not apart of any nodes
            }

            String removeNames = "DELETE FROM mydb.NAMES WHERE NODE_ID = "
                    + nodeId;
            stmt.execute(removeNames);
            //remove the node
            String remNode = "DELETE FROM mydb.NODES WHERE NODE_ID = "
                    + nodeId;
            //execute the query to remove nodes
            stmt.execute(remNode);

        } catch (SQLException e) {
            System.out.print("Couldn't remove location\n");
            e.getStackTrace();
        }

    }

    /*
     * Update a node in the database
     *
     * @param locToUpdate The location/node that needs to be updated in the database
     */
    public void updateNode(Location locToUpdate) {
        //// TODO: 11/15/2015 add name functionality

        double x = locToUpdate.getPosition().getX();
        double y = locToUpdate.getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            String getID = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x + "and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No entry with Node's location");
            }
            int nodeId = rs.getInt("NODE_ID");
            String update = "UPDATE mydb.NODES SET"
                    + "FLOOR_NUM = " + locToUpdate.getFloorNumber()
                    + "WHERE NODE_ID =" + nodeId;
            stmt.execute(update);
            String delete = "DELETE FROM mydb.NAMES WHERE" +
                    "NODE_ID =" + nodeId;
            update = "INSERT INTO mydb.NAMES ()"
                    + "FLOOR_NUM = " + locToUpdate.getFloorNumber()
                    + "WHERE NODE_ID =" + nodeId;
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }


    }

    /*
     * Add an edge to the database
     *
     * @param edgeToAdd The edge that needs to be added to the database
     */
    public void addEdge(Edge edgeToAdd) {
        //// TODO: 11/15/2015 add in attributes functionality
        //NOTE: can add edge with node ids 5,6 and another with 6,5
        //gets the x and y of both nodes in the edge
        double x1 = edgeToAdd.getNode1().getPosition().getX();
        double y1 = edgeToAdd.getNode1().getPosition().getY();
        double x2 = edgeToAdd.getNode2().getPosition().getX();
        double y2 = edgeToAdd.getNode2().getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            //query to get the id of the first node
            String getID1 = "SELECT NODE_ID FROM mydb.NODES WHERE POS_X = "  +
                    x1 + " and POS_Y =" + y1;
            //query to get the id of the second node
            String getID2 = "SELECT NODE_ID FROM mydb.NODES WHERE POS_X = "  +
                    x2 + " and POS_Y = " + y2;
            //get the result set of the query for the first nodeID
            ResultSet rs = stmt.executeQuery(getID1);
            //if rs is empty
            if (!rs.next()){
                throw new SQLException("No entry with Node1's location");
            }
            //store the nodeID
            int nodeId1 = rs.getInt("NODE_ID");
            //get the result set of the query for the second nodeID
            rs = stmt.executeQuery(getID2);
            //if rs is empty
            if (!rs.next()){
                throw new SQLException("No entry with Node2's location");
            }
            //store the node ID
            int nodeId2 = rs.getInt("NODE_ID");

            //query for inserting the edge into the database
            String query = "INSERT INTO mydb.EDGES (NODE1_ID, NODE2_ID) VALUES " +
                    "(" + nodeId1 + "," + nodeId2 + ")";
            //add the edge to the database
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }


    }

    /*
     * Remove an edge from the database
     *
     * @param edgeToRem The edge that needs to be removed to the database
     */
    public void removeEdge(Location loc1, Location loc2) {
        //gets the x and y of both nodes in the edge
        double x1 = loc1.getPosition().getX();
        double y1 = loc1.getPosition().getY();
        double x2 = loc2.getPosition().getX();
        double y2 = loc2.getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            //query to get the first nodeID
            String query = "SELECT NODE_ID FROM mydb.NODES WHERE " +
                    "(POS_X = " + x1 + " AND POS_Y =" + y1 + ") OR " +
                    "(POS_X = " + x2 + " and POS_Y =" + y2 + ")";
            //execute the query to get the first nodeID
            ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates(Node1)");
            }
            //store the first nodeID
            int nodeId1 = rs.getInt("NODE_ID");
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates(Node2)");
            }
            //store the second nodeID
            int nodeId2 = rs.getInt("NODE_ID");

            query = "SELECT EDGE_ID FROM mydb.EDGES WHERE " +
                    "NODE1_ID = " + nodeId1 + " AND NODE2_ID = " + nodeId2 + ") OR" +
                    " (NODE1_ID = " + nodeId2 + " AND NODE2_ID = " + nodeId1 + ")";
            //query to get the Edge id
            rs = stmt.executeQuery(query);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates(Node2)");
            }
            //store the second nodeID
            int edgeId = rs.getInt("EDGE_ID");
            String remAttr = "DELETE FROM mydb.EDGE_ATTRIBUTES WHERE EDGE_ID = " + edgeId;
            stmt.execute(remAttr);
            String remEdge = "DELETE FROM mydb.EDGES WHERE EDGE_ID = " + edgeId;
            //remove the edge from the database
            stmt.execute(remEdge);
        } catch (SQLException e) {
            e.getStackTrace();
        }

    }

    /*
     * Update an edge in the database
     *
     * @param edgeToUpdate The edge that needs to be updated in the database
     */
    public void updateEdge(Edge edgeToUpdate) {
        //// TODO: 11/15/2015 Add in update functionality

    }
    /*
     * Create a location graph based on node and edge information in the database
     *
     *
     */
    public LocationGraph createGraph() {
        // Location graph for this map
        LocationGraph graph = new LocationGraph();

        try {
            // Hash map to store location and its corresponding database node id
            Statement sta = con.createStatement();
            int locCount;
            String query = "SELECT COUNT(NODE_ID) AS NUM FROM mydb.NODES";
            ResultSet res = sta.executeQuery(query);
            if (!res.next()){
                throw new SQLException("No Nodes in table \n");
            }
            locCount = res.getInt("NUM");
            HashMap<Integer, Location> hm = new HashMap<Integer, Location>(locCount);


            // Create locations
            // Get all nodes stored in the database
            res = sta.executeQuery(
                    "SELECT * FROM mydb.NODES");
            while (res.next()) {
                Location loc = new Location (
                        new Point2D.Double(res.getDouble("POS_X"),
                                res.getDouble("POS_Y")),
                        res.getInt("FLOOR_NUM"), new String[0]);

                // Add locations and node id to a hash map
                hm.put(res.getInt("NODE_ID"), loc);
                graph.addLocation(loc, new HashMap<>());
            }

            // Create edges
            query = "SELECT * FROM mydb.EDGES";
            res = sta.executeQuery(query);
            while (res.next()) {
                Location loc1 = hm.get(res.getInt("NODE1_ID"));
                Location loc2 = hm.get(res.getInt("NODE2_ID"));
                loc1.makeAdjacentTo(loc2, new ArrayList<>());
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }

        // Return location graph
        return graph;
    }
    /*
    * Update the database with all new locations/edges
    *
    * @param locGraph The updated location graph
    */
    public void updateDB(LocationGraph locGraph){
        try {
            Statement sta = con.createStatement();
            // Get all locations and store in database
            List<Location> allLocations = locGraph.getAllLocations();
            for (int i = 0; i < allLocations.size(); i++) {
                double x1 = allLocations.get(i).getPosition().getX();
                double y1 = allLocations.get(i).getPosition().getY();
                String getID = "SELECT NODE_ID FROM mydb.NODES WHERE POS_X = "  +
                        x1 + " and POS_Y =" + y1;
                //execute the query to get the first nodeID
                ResultSet rs = sta.executeQuery(getID);
                if (!rs.next()){
                    addNode(allLocations.get(i));
                }

            }

            // Get all edges and store in database
            List<Edge> allEdges = locGraph.getAllEdges();
            for (int i = 0; i < allEdges.size(); i++) {
                //todo: make this not throw all kinds of exceptions.
                addEdge(allEdges.get(i));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.print("Error Closing Connection: \n");
            e.getStackTrace();
        }
    }
    private void addNames(Location loc, int nodeId){
        try {
            Statement stmt = con.createStatement();
            String[] nameList = loc.getNameList();
            for (int i = 0; i < nameList.length; i++) {
                String query = "INSERT INTO mydb.NAMES (NODE_ID, NAME) VALUES " +
                        "(" + nodeId + "," + nameList[i] + ")";
                stmt.execute(query);
            }


        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
