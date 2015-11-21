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
     * Connects to the database as a read-only user
     *
     */
    public Database() throws Exception {
        //create a file to find the absolute path of the project

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" +
                        "aztecwash.cly9e1vwzwlp.us-west-2.rds.amazonaws.com:3306",
                "aztecwash", "aztecwash");

        //ACTUAL READ ONLY USER IS USERNAME: user PASSWORD: guest

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
      * Attepmts to connect to the database with the given username and password
      *
      * @param username username of the database user
      *
      * @param password password for the user
      *
     */
    public Database(String username, String password) throws Exception {
        //create a file to find the absolute path of the project

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" +
                        "aztecwash.cly9e1vwzwlp.us-west-2.rds.amazonaws.com:3306",
                username, password);
    }

    /*
     * Add a node to the database
     *
     * @param locToAdd The location/node that needs to be added to the database
     */
    public void addNode(Location locToAdd) {
        double x = locToAdd.getPosition().getX(); //get the x of the node to add
        double y = locToAdd.getPosition().getY(); //get the y of the node to add
        int floorNum = locToAdd.getFloorNumber(); //get the floor number

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

            //add location names
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

        double x = locToRem.getPosition().getX(); //get the x of node to remove
        double y = locToRem.getPosition().getY(); //get the y of node to remove

        try {
            Statement stmt = con.createStatement();

            //get the id of the node to remove
            String getID = "SELECT NODE_ID FROM mydb.NODES WHERE POS_X = "  +
                    x + " and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates");
            }
            int nodeId = rs.getInt("NODE_ID");

            try {
                //remove the edges that link to this node
                String remEdge = "DELETE FROM mydb.EDGES WHERE NODE1_ID = "
                        + nodeId + " or NODE2_ID = " + nodeId;
                stmt.execute(remEdge); //execute query to remove edges

                String remNames = "DELETE FROM mydb.EDGE_ATTRIBUTES" +
                        "WHERE EDGE_ID = (SELECT EDGE_ID FROM EDGES WHERE NODE1_ID = " + nodeId +
                        "AND NODE2_ID = " + nodeId + ")";
                stmt.execute(remNames); //execute query to remove edge attributes

            } catch (SQLException e){
                //location not apart of any nodes
            }

            String removeNames = "DELETE FROM mydb.NAMES WHERE NODE_ID = "
                    + nodeId;
            stmt.execute(removeNames); //execute query to remove names

            String remNode = "DELETE FROM mydb.NODES WHERE NODE_ID = "
                    + nodeId;
            stmt.execute(remNode); //execute the query to remove nodes

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
        double x = locToUpdate.getPosition().getX(); //get the x of node to update
        double y = locToUpdate.getPosition().getY(); //get the y of node to update

        try {
            Statement stmt = con.createStatement();

            //get id based on x and y position
            String getID = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x + "and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No entry with Node's location");
            }
            int nodeId = rs.getInt("NODE_ID");

            //update floor number
            String updateFloor = "UPDATE mydb.NODES SET"
                    + "FLOOR_NUM = " + locToUpdate.getFloorNumber()
                    + "WHERE NODE_ID =" + nodeId;
            stmt.execute(updateFloor);

            // update names
            String delete = "DELETE FROM mydb.NAMES WHERE" +
                    "NODE_ID =" + nodeId;
            stmt.execute(delete); //delete any entries in NAMES where id is found
            //add all names to database
            addNames(locToUpdate, nodeId);

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
            //store first nodeID
            int nodeId1 = rs.getInt("NODE_ID");

            //get the result set of the query for the second nodeID
            rs = stmt.executeQuery(getID2);
            //if rs is empty
            if (!rs.next()){
                throw new SQLException("No entry with Node2's location");
            }
            //store second nodeID
            int nodeId2 = rs.getInt("NODE_ID");

            //query for inserting the edge into the database
            String insertEdge = "INSERT INTO mydb.EDGES (NODE1_ID, NODE2_ID) VALUES " +
                    "(" + nodeId1 + "," + nodeId2 + ")";
            stmt.execute(insertEdge); //add the edge to the database

            //query for getting edgeId from database
            String getEdgeId = "SELECT EDGE_ID FROM mydb.EDGES WHERE " +
                    "NODE1_ID = " + nodeId1 + "AND NODE2_ID = " + nodeId2 + ")";
            //get the result set of the query for the edgeId
            ResultSet rsE = stmt.executeQuery(getEdgeId);
            if (!rsE.next()){ //if rsE is empty
                throw new SQLException("No entry with Node1's location");
            }
            //store edgeId
            int edgeId = rsE.getInt("EDGE_ID");

            List<EdgeAttribute> attributes = edgeToAdd.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                String insertAttr = "INSERT INTO mydb.EDGE_ATTRIBUTES VALUES" +
                        "(" + edgeId + "," + attributes.get(i) + ")";
                stmt.execute(insertAttr);
            }

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
                    "(NODE1_ID = " + nodeId1 + " AND NODE2_ID = " + nodeId2 + ") OR" +
                    " (NODE1_ID = " + nodeId2 + " AND NODE2_ID = " + nodeId1 + ")";
            //query to get the Edge id
            rs = stmt.executeQuery(query);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates(Node2)");
            }
            //store first edgeId found
            int edgeId1 = rs.getInt("EDGE_ID");
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates(Node2)");
            }
            //store second edgeId found
            int edgeId2 = rs.getInt("EDGE_ID");

            String remAttr = "DELETE FROM mydb.EDGE_ATTRIBUTES WHERE (EDGE_ID = " + edgeId1
                    + "OR (EDGE_ID = " + edgeId2 + ")";
            stmt.execute(remAttr); //remove the attributes from the database

            String remEdge = "DELETE FROM mydb.EDGES WHERE (EDGE_ID = " + edgeId1
                    + "OR (EDGE_ID = " + edgeId2 + ")";
            stmt.execute(remEdge); //remove the edge from the database

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

        double node1X = edgeToUpdate.getNode1().getPosition().getX();
        double node1Y = edgeToUpdate.getNode1().getPosition().getY();
        double node2X = edgeToUpdate.getNode2().getPosition().getX();
        double node2Y = edgeToUpdate.getNode2().getPosition().getY();

        try {
            Statement stmt = con.createStatement();

            //query to get the first nodeID
            String query = "SELECT NODE_ID FROM mydb.NODES WHERE " +
                    "(POS_X = " + node1X + " AND POS_Y =" + node1Y + ") OR " +
                    "(POS_X = " + node2X + " and POS_Y =" + node2Y + ")";
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
                    "(NODE1_ID = " + nodeId1 + " AND NODE2_ID = " + nodeId2 + ") OR" +
                    " (NODE1_ID = " + nodeId2 + " AND NODE2_ID = " + nodeId1 + ")";
            //query to get the Edge id
            rs = stmt.executeQuery(query);
            if (!rs.next()){
                throw new SQLException("No edges with Node1 and Node2");
            }
            //store first edgeId found
            int edgeId1 = rs.getInt("EDGE_ID");

            int edgeId2;
            if (!rs.next()){
                //throw new SQLException("No second edge with Node1 and Node2");
                edgeId2 = 0; //set to zero so no exception is thrown
            } else {
                //store second edgeId found
                edgeId2 = rs.getInt("EDGE_ID");
            }

            if (edgeId2 != 0) {
                //removes all attributes for both edges
                String remAttrBoth = "DELETE FROM mydb.EDGE_ATTRIBUTES WHERE (EDGE_ID = " + edgeId1
                        + "OR (EDGE_ID = " + edgeId2 + ")";
                stmt.execute(remAttrBoth);

                List<EdgeAttribute> attributes = edgeToUpdate.getAttributes();
                for (int i = 0; i < attributes.size(); i++) {
                    String insertAttr1 = "INSERT INTO mydb.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId1 + "," + attributes.get(i).toString() + ")";
                    stmt.execute(insertAttr1);
                    String insertAttr2 = "INSERT INTO mydb.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId2 + "," + attributes.get(i).toString() + ")";
                    stmt.execute(insertAttr2);
                }
            } else {
                //removes all attributes for both edges
                String remAttr = "DELETE FROM mydb.EDGE_ATTRIBUTES WHERE EDGE_ID = " +
                        edgeId1 + ")";
                stmt.execute(remAttr);

                List<EdgeAttribute> attributes = edgeToUpdate.getAttributes();
                for (int i = 0; i < attributes.size(); i++) {
                    String insertAttr = "INSERT INTO mydb.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId1 + "," + attributes.get(i).toString() + ")";
                    stmt.execute(insertAttr);
                }
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }

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
                int nodeId = res.getInt("NODE_ID");

                String[] names = new String[20];
                int numNames = 0;
                String getNames = "SELECT NAME FROM mydb.NAMES WHERE NODE_ID = " + nodeId + ")";
                ResultSet rsName = sta.executeQuery(getNames);
                while(rsName.next() && numNames < 20) {
                    String nameToAdd = rsName.getString("NAME");
                    names[numNames] = nameToAdd;
                    numNames++;
                }

                Location loc = new Location (
                        new Point2D.Double(res.getDouble("POS_X"),
                                res.getDouble("POS_Y")),
                        res.getInt("FLOOR_NUM"), names);

                // Add locations and node id to a hash map
                hm.put(nodeId, loc);
                graph.addLocation(loc, new HashMap<>());
            }

            query = "SELECT * FROM mydb.EDGES";
            res = sta.executeQuery(query);
            while (res.next()) {
                int node1 = res.getInt("NODE1_ID");
                int node2 = res.getInt("NODE2_ID");
                int edgeId = res.getInt("EDGE_ID");

                // get all attributes in result set based on edge id
                String getAttr = "SELECT ATTRIBUTE FROM mydb.EDGE_ATTRIBUTES WHERE EDGE_ID = " +
                        edgeId + ")";
                ResultSet rsAttr = sta.executeQuery(getAttr);
                ArrayList<EdgeAttribute> attrList = new ArrayList<EdgeAttribute>();

                while(rsAttr.next()) {
                    String attr = rsAttr.getString("ATTRIBUTE");
                    EdgeAttribute attrToAdd = EdgeAttribute.valueOf(attr);
                    attrList.add(attrToAdd);
                }

                Location loc1 = hm.get(node1);
                Location loc2 = hm.get(node2);
                loc1.makeAdjacentTo(loc2, attrList);
            }

        } catch (SQLException e) {
            e.getStackTrace();
        }

        // Return location graph
        return graph;
    }

    /*
    * Update all nodes in the database
    *
    * @param addLocList List of nodes to be added
    * @param deleteLocList List of nodes to be removed
    * @param updateLocList List of nodes to be updated
    */
    private void updateNodesInDb(List<Location> addLocList, List<Location> removeLocList,
                         List<Location> updateLocList){
        try {
            Statement sta = con.createStatement();

            // Compare the delete list to the update list
            // If any location is in both lists, remove from the update list
            for (int i = 0; i < updateLocList.size(); i++) {
                if (removeLocList.contains(updateLocList.get(i))) {
                    updateLocList.remove(i);
                    if (removeLocList.contains(addLocList.get(i))) {
                        addLocList.remove(i);
                    }
                }
            }

            // Add all nodes from add list
            for (int j= 0; j < addLocList.size(); j++) {
                addNode(addLocList.get(j));
            }

            // Update all nodes from update list
            for (int k = 0; k < updateLocList.size(); k++) {
                updateNode(updateLocList.get(k));
            }

            // Delete all nodes from delete list
            // Will NOT throw an exception if node doesn't exist
            for (int m = 0; m < removeLocList.size(); m++) {
                removeNode(removeLocList.get(m));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    * Update all nodes in the database
    *
    * @param addEdgeList List of edges to be added
    * @param deleteEdgeList List of edges to be removed
    * @param updateEdgeList List of edges to be updated
    */
    private void updateEdgesInDb(List<Edge> addEdgeList, List<Edge> removeEdgeList,
                                List<Edge> updateEdgeList){
        try {
            Statement sta = con.createStatement();

            // Compare add list to delete list
            // If any edge is in both lists, remove from the delete list
            for (int i = 0; i < removeEdgeList.size(); i++) {
                if (addEdgeList.contains(removeEdgeList.get(i))) {
                    removeEdgeList.remove(i);
                }
            }

            // Compare delete list to update list
            // If any edge is in both lists, remove from the update list
            for (int j = 0; j < updateEdgeList.size(); j++) {
                if (removeEdgeList.contains(updateEdgeList.get(j))) {
                    updateEdgeList.remove(j);
                }
            }

            // Add all nodes from add list
            // Call addNode(Location loc)
            for (int n= 0; n < addEdgeList.size(); n++) {
                addEdge(addEdgeList.get(n));
            }

            // Update all nodes from update list
            // Call updateNode(Location loc)
            for (int k = 0; k < updateEdgeList.size(); k++) {
                updateEdge(updateEdgeList.get(k));
            }

            // Delete all nodes from delete list
            // Will NOT throw an exception if node doesn't exist
            // Call removeNode(Location loc)
            for (int m = 0; m < removeEdgeList.size(); m++) {
                Location loc1 = removeEdgeList.get(m).getNode1();
                Location loc2 = removeEdgeList.get(m).getNode2();
                removeEdge(loc1, loc2);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    * Update the database with all new locations/edges
    *
    * @param addLocList List of nodes to be added
    * @param removeLocList List of nodes to be removed
    * @param updateLocList List of nodes to be updated
    * @param addEdgeList List of edges to be added
    * @param removeEdgeList List of edges to be removed
    * @param updateEdgeList List of edges to be updated
    *
    */
    public void updateDB(List<Location> addLocList, List<Location> removeLocList,
                         List<Location> updateLocList, List<Edge> addEdgeList,
                         List<Edge> removeEdgeList, List<Edge> updateEdgeList){
        updateNodesInDb(addLocList, removeLocList, updateLocList);
        updateEdgesInDb(addEdgeList, removeEdgeList, updateEdgeList);
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
