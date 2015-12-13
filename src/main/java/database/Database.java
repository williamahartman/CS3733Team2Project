package database;

import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;
import core.*;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.awt.geom.Point2D;
import java.util.List;

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
    public Database() throws SQLException {
        //create a file to find the absolute path of the project

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.exit(-1);
        }
        con = DriverManager.getConnection("jdbc:mysql://" +
                        "aztecwash.cly9e1vwzwlp.us-west-2.rds.amazonaws.com:3306",
                "aztecwash", "aztecwash");

        //ACTUAL READ ONLY USER IS USERNAME: user PASSWORD: guest

        /*
        TABLES IN THE DATABASE:

        CREATE TABLE mapData.NODES(
            NODE_ID int not null AUTO_INCREMENT,
            POS_X double not null,
            POS_Y double not null,
            FLOOR_NUM int not null,
            PRIMARY KEY (NODE_ID));

        ALTER TABLE mapData.NODES
                ADD CONSTRAINT uniquePosition UNIQUE (POS_X, POS_Y, FLOOR_NUM);

        CREATE TABLE mapData.EDGES(
                EDGE_ID int not null AUTO_INCREMENT,
                NODE1_ID int not null,
                NODE2_ID int not null,
                PRIMARY KEY(EDGE_ID));

        ALTER TABLE mapData.EDGES
                ADD CONSTRAINT uniquePosition UNIQUE (NODE1_ID, NODE2_ID);

        ALTER TABLE mapData.EDGES
                ADD FOREIGN KEY (NODE1_ID)
                REFERENCES mapData.NODES(NODE_ID);

        CREATE TABLE mapData.NAMES(
                NODE_ID int not null,
                NAME varchar(30) not null);

        CREATE TABLE mapData.EDGE_ATTRIBUTES(
                EDGE_ID int not null,
                ATTRIBUTE varchar(30) not null);

        grant select on mapData.* to 'user'@'%' identified by 'guest';
        */
    }


    /**
      * Attepmts to connect to the database with the given username and password.
      *
      * @param username username of the database user
      *
      * @param password password for the user
      *
     */
    public Database(String username, String password) throws SQLException {
        //create a file to find the absolute path of the project
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        con = DriverManager.getConnection("jdbc:mysql://" +
                        "aztecwash.cly9e1vwzwlp.us-west-2.rds.amazonaws.com:3306",
                username, password);
    }

    /**
     * Add a node to the database.
     *
     * @param locToAdd The location/node that needs to be added to the database
     */
    public void addNode(Location locToAdd) {
        double x = locToAdd.getPosition().getX(); //get the x of the node to add
        double y = locToAdd.getPosition().getY(); //get the y of the node to add
        int floorNum = locToAdd.getFloorNumber(); //get the floor number

        //query for inserting the node into the database
        String query = "INSERT INTO mapData.NODES (POS_X, POS_Y, FLOOR_NUM) VALUES " +
                "(" + x + "," + y + "," + floorNum + ")";
        String getID = "SELECT NODE_ID FROM mapData.NODES WHERE" +
                "(POS_X = " + x + ") AND (POS_Y = " + y + ")";
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
            System.out.print("Couldn't add Node\n" + e + "\n");
        }
    }

    /**
     * Remove a node from the database.
     *
     * @param locToRem The location/node that needs to be removed to the database
     */
    public void removeNode(Location locToRem) {

        double x = locToRem.getPosition().getX(); //get the x of node to remove
        double y = locToRem.getPosition().getY(); //get the y of node to remove

        try {
            Statement stmt = con.createStatement();

            //get the id of the node to remove
            String getID = "SELECT NODE_ID FROM mapData.NODES WHERE POS_X = "  +
                    x + " and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No locations with those coordinates");
            }
            int nodeId = rs.getInt("NODE_ID");

            try {
                //remove the edges that link to this node
                String remEdge = "DELETE FROM mapData.EDGES WHERE NODE1_ID = "
                        + nodeId + " or NODE2_ID = " + nodeId;
                stmt.execute(remEdge); //execute query to remove edges

                String remNames = "DELETE FROM mapData.EDGE_ATTRIBUTES" +
                        "WHERE EDGE_ID = (SELECT EDGE_ID FROM EDGES WHERE NODE1_ID = " + nodeId +
                        "AND NODE2_ID = " + nodeId + ")";
                stmt.execute(remNames); //execute query to remove edge attributes

            } catch (SQLException e){
                //location not apart of any nodes
            }

            String removeNames = "DELETE FROM mapData.NAMES WHERE NODE_ID = "
                    + nodeId;
            stmt.execute(removeNames); //execute query to remove names

            String remNode = "DELETE FROM mapData.NODES WHERE NODE_ID = "
                    + nodeId;
            stmt.execute(remNode); //execute the query to remove nodes

        } catch (SQLException e) {
            System.out.print("Couldn't remove location\n");
            e.printStackTrace();
        }
    }

    /**
     * Update a node in the database.
     *
     * @param locToUpdate The location/node that needs to be updated in the database
     */
    public void updateNode(Location locToUpdate) {
        double x = locToUpdate.getPosition().getX(); //get the x of node to update
        double y = locToUpdate.getPosition().getY(); //get the y of node to update

        try {
            Statement stmt = con.createStatement();

            //get id based on x and y position
            String getID = "SELECT NODE_ID FROM mapData.NODES WHERE POS_X = "  +
                    x + " AND POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            if (!rs.next()){
                throw new SQLException("No entry with Node's location");
            }
            //get the node id of the node to update
            int nodeId = rs.getInt("NODE_ID");

            //update floor number
            String updateFloor = "UPDATE mapData.NODES SET "
                    + "FLOOR_NUM = " + locToUpdate.getFloorNumber()
                    + " WHERE NODE_ID =" + nodeId;
            stmt.execute(updateFloor);

            // update names
            String delete = "DELETE FROM mapData.NAMES WHERE " +
                    "NODE_ID =" + nodeId;
            stmt.execute(delete); //delete any entries in NAMES where id is found
            //add all names to database
            addNames(locToUpdate, nodeId);

        } catch (SQLException e) {
            System.out.print("Couldn't Update location\n" + e + "\n");
        }
    }

    /**
     * Add an edge to the database.
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
            String getID1 = "SELECT NODE_ID FROM mapData.NODES WHERE POS_X = "  +
                    x1 + " and POS_Y =" + y1;
            //query to get the id of the second node
            String getID2 = "SELECT NODE_ID FROM mapData.NODES WHERE POS_X = "  +
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
            String insertEdge = "INSERT INTO mapData.EDGES (NODE1_ID, NODE2_ID) VALUES " +
                    "(" + nodeId1 + "," + nodeId2 + ")";
            stmt.execute(insertEdge); //add the edge to the database

            //query for getting edgeId from database
            String getEdgeId = "SELECT EDGE_ID FROM mapData.EDGES WHERE " +
                    "NODE1_ID = " + nodeId1 + " AND NODE2_ID = " + nodeId2;
            //get the result set of the query for the edgeId
            ResultSet rsE = stmt.executeQuery(getEdgeId);
            if (!rsE.next()){ //if rsE is empty
                throw new SQLException("No entry with Node1's location");
            }
            //store edgeId
            int edgeId = rsE.getInt("EDGE_ID");

            List<EdgeAttribute> attributes = edgeToAdd.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                String insertAttr = "INSERT INTO mapData.EDGE_ATTRIBUTES VALUES" +
                        "(" + edgeId + "," + '"' + attributes.get(i) + '"' + ")";
                stmt.execute(insertAttr);
            }

        } catch (SQLException e) {
            System.out.print("Couldn't add Edge\n" + e + "\n");
        }


    }

    /**
     * Remove an edge from the database.
     *
     * @param loc1 The edges first location that needs to be removed to the database
     * @param loc2 The edges second location that needs to be removed to the database
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
            String query = "SELECT NODE_ID FROM mapData.NODES WHERE " +
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

            query = "SELECT EDGE_ID FROM mapData.EDGES WHERE " +
                    "(NODE1_ID = " + nodeId1 + " AND NODE2_ID = " + nodeId2 + ") OR" +
                    " (NODE1_ID = " + nodeId2 + " AND NODE2_ID = " + nodeId1 + ")";
            //query to get the Edge id
            rs = stmt.executeQuery(query);
            if (!rs.next()){
                throw new SQLException("No edges with those two locations");
            }
            //store first edgeId found
            int edgeId1 = rs.getInt("EDGE_ID");
            if (!rs.next()){
                //throw new SQLException("No locations with those coordinates(Node2)");
                String remAttr = "DELETE FROM mapData.EDGE_ATTRIBUTES WHERE (EDGE_ID = " + edgeId1 + ")";
                stmt.execute(remAttr); //remove the attributes from the database

                String remEdge = "DELETE FROM mapData.EDGES WHERE (EDGE_ID = " + edgeId1 + ")";
                stmt.execute(remEdge); //remove the edge from the database
            } else {
                //store second edgeId found
                int edgeId2 = rs.getInt("EDGE_ID");

                String remAttr = "DELETE FROM mapData.EDGE_ATTRIBUTES WHERE (EDGE_ID = " + edgeId1
                        + "OR (EDGE_ID = " + edgeId2 + ")";
                stmt.execute(remAttr); //remove the attributes from the database

                String remEdge = "DELETE FROM mapData.EDGES WHERE (EDGE_ID = " + edgeId1
                        + "OR (EDGE_ID = " + edgeId2 + ")";
                stmt.execute(remEdge); //remove the edge from the database
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Update an edge in the database.
     *
     * @param edgeToUpdate The edge that needs to be updated in the database
     */
    public void updateEdge(Edge edgeToUpdate) {
        //get the x and y locations of the nodes in the location
        double node1X = edgeToUpdate.getNode1().getPosition().getX();
        double node1Y = edgeToUpdate.getNode1().getPosition().getY();
        double node2X = edgeToUpdate.getNode2().getPosition().getX();
        double node2Y = edgeToUpdate.getNode2().getPosition().getY();

        try {
            Statement stmt = con.createStatement();

            //query to get the first nodeID
            String query = "SELECT NODE_ID FROM mapData.NODES WHERE " +
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

            query = "SELECT EDGE_ID FROM mapData.EDGES WHERE " +
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
                String remAttrBoth = "DELETE FROM mapData.EDGE_ATTRIBUTES WHERE (EDGE_ID = " + edgeId1
                        + "OR (EDGE_ID = " + edgeId2 + ")";
                stmt.execute(remAttrBoth);

                List<EdgeAttribute> attributes = edgeToUpdate.getAttributes();
                for (int i = 0; i < attributes.size(); i++) {
                    String insertAttr1 = "INSERT INTO mapData.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId1 + "," + attributes.get(i).toString() + ")";
                    stmt.execute(insertAttr1);
                    String insertAttr2 = "INSERT INTO mapData.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId2 + "," + attributes.get(i).toString() + ")";
                    stmt.execute(insertAttr2);
                }
            } else {
                //removes all attributes for both edges
                String remAttr = "DELETE FROM mapData.EDGE_ATTRIBUTES WHERE EDGE_ID = " +
                        edgeId1;
                stmt.execute(remAttr);

                List<EdgeAttribute> attributes = edgeToUpdate.getAttributes();
                for (int i = 0; i < attributes.size(); i++) {
                    String insertAttr = "INSERT INTO mapData.EDGE_ATTRIBUTES VALUES" +
                            "(" + edgeId1 + "," + '"' + attributes.get(i).toString() + '"' + ")";
                    stmt.execute(insertAttr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Create a location graph based on node and edge information in the database.
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
            String query = "SELECT COUNT(NODE_ID) AS NUM FROM mapData.NODES";
            ResultSet rset = sta.executeQuery(query);
            if (!rset.next()){
                throw new SQLException("No Nodes in table \n");
            }
            locCount = rset.getInt("NUM");
            HashMap<Integer, Location> hm = new HashMap<>(locCount);
            //create a hashmap with enough entries for the locations
            HashMap<Integer, List> nameHm = new HashMap<>(locCount);


            List<String> names = new ArrayList<>();
            int id = -1;
            int prevID = -1;
            String nameToAdd;
            //get the list of names sorted in ascending order by the node id
            String getNames = "SELECT * FROM mapData.NAMES ORDER BY NODE_ID ASC";
            Statement stmt = con.createStatement();
            ResultSet rsName = stmt.executeQuery(getNames);
            while (rsName.next()) {
                //get the node id and name of the current row
                id = rsName.getInt("NODE_ID");
                nameToAdd = rsName.getString("NAME");
                //if the previous id is the same as the current id or
                //this is the first entry in the list
                if (id == prevID || prevID == -1) {
                    //add the name to the list of names
                    names.add(nameToAdd);
                } else {
                    //if the previous id is different
                    //add the previous id and its names to a hashmap
                    nameHm.put(prevID, names);
                    //reset numNames and names
                    names = new ArrayList<>();
                    //add the current name to the new list of names
                    names.add(nameToAdd);

                }
                //set the current id to the previous id
                prevID = id;
            }
            //put the final id in the list with its names
            nameHm.put(id, names);


            // Create locations
            // Get all nodes stored in the database
            ResultSet res = sta.executeQuery(
                    "SELECT * FROM mapData.NODES");
            while (res.next()) {
                //get the values stored in the row
                int nodeId = res.getInt("NODE_ID");
                Point2D.Double coords = new Point2D.Double(res.getDouble("POS_X"),
                        res.getDouble("POS_Y"));
                int floor = res.getInt("FLOOR_NUM");
                //create a new location with the row's info and
                //the corresponding name list from the hashmap
                Location loc;
                if (nameHm.get(nodeId) != null) {
                    String[] nmList = new String[nameHm.get(nodeId).size()];
                    nmList = (String[]) nameHm.get(nodeId).toArray(nmList);
                     loc = new Location(
                            coords,
                            floor,  nmList);
                } else {
                    loc = new Location(
                            coords,
                            floor, new String[0]);
                }
                // Add locations and node id to a hash map
                hm.put(nodeId, loc);
                graph.addLocation(loc, new HashMap<>());
            }

            //create a hashmap with enough entries for 3 names for each node
            HashMap<Integer, List<EdgeAttribute>> attrHm = new HashMap<>(locCount);

            List<EdgeAttribute> attrList = new ArrayList<>();
            id = -1;
            prevID = -1;
            String attrToAdd;
            //get all the edge attributes
            String getAttr = "SELECT * FROM mapData.EDGE_ATTRIBUTES ORDER BY EDGE_ID ASC";
            rsName = stmt.executeQuery(getAttr);
            while (rsName.next()) {
                //get the current row's infromation
                id = rsName.getInt("EDGE_ID");
                attrToAdd = rsName.getString("ATTRIBUTE");
                if (id == prevID || prevID == -1) {
                    //if if the previous id is the same as the current id
                    //add it to the current attribute list
                    attrList.add(EdgeAttribute.valueOf(attrToAdd));
                } else {
                    //if the current node id is different from
                    //the previous id
                    //add the previous id and the list to the hashmap
                    attrHm.put(prevID, attrList);
                    //reset the array list
                    attrList = new ArrayList<>();
                    //add the current attribute to the new array list
                    attrList.add(EdgeAttribute.valueOf(attrToAdd));

                }
                prevID = id;
            }
            //add the final id and attributes to the hashmap
            attrHm.put(id, attrList);
            query = "SELECT * FROM mapData.EDGES";
            //get all infromation from the edges table
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                //get the current row's information
                int node1 = rs.getInt("NODE1_ID");
                int node2 = rs.getInt("NODE2_ID");
                int edgeId = rs.getInt("EDGE_ID");
                //get the locations corresponding
                //to the node ids in the edge
                Location loc1 = hm.get(node1);
                Location loc2 = hm.get(node2);
                //if this edge has attributes
                if (attrHm.get(edgeId) != null) {
                    //place the attributes in the edge
                    loc1.makeAdjacentTo(loc2, attrHm.get(edgeId));
                } else {
                    //otherwise add an empty list
                    loc1.makeAdjacentTo(loc2, new ArrayList<>());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return location graph
        return graph;
    }

    /**
    * Update the database with all new locations/edges.
    *
    * @param dbList Class that contains all necessary node and edge lists
    */
    public void updateDB(DatabaseList dbList){
        // GET LISTS
        // Get add, remove, update lists for nodes and edges
        List<Location> addLocList = dbList.getAddLocList();
        List<Location> removeLocList = dbList.getRemoveLocList();
        List<Location> updateLocList = dbList.getUpdateLocList();
        List<Edge> addEdgeList = dbList.getAddEdgeList();
        List<Edge> removeEdgeList = dbList.getRemoveEdgeList();
        List<Edge> updateEdgeList = dbList.getUpdateEdgeList();

        // COMPARE LISTS
        // Compare the node delete list to node update list
        // If any location is in both lists, remove from the update list
        for (int i = 0; i < updateLocList.size(); i++) {
            if (removeLocList.contains(updateLocList.get(i))) {
                updateLocList.remove(i);
                if (removeLocList.contains(addLocList.get(i))) {
                    addLocList.remove(i);
                }
            }
        }

        // Compare edge add list to edge delete list
        // If any edge is in both lists, remove from the delete list
        for (int i = 0; i < removeEdgeList.size(); i++) {
            if (addEdgeList.contains(removeEdgeList.get(i))) {
                System.out.print("Took edge out from remove edge list\n");
                removeEdgeList.remove(i);
            }
        }

        // Compare edge delete list to edge update list
        // If any edge is in both lists, remove from the update list
        for (int j = 0; j < updateEdgeList.size(); j++) {
            if (removeEdgeList.contains(updateEdgeList.get(j))) {
                updateEdgeList.remove(j);
            }
        }


        // CALL FUNCTIONS BELOW
        // Add all nodes from add list
        for (int j = 0; j < addLocList.size(); j++) {
            addNode(addLocList.get(j));
        }

        // Add all edges from add list
        for (int n = 0; n < addEdgeList.size(); n++) {
            addEdge(addEdgeList.get(n));
        }

        // Update all nodes from update list
        for (int k = 0; k < updateLocList.size(); k++) {
            updateNode(updateLocList.get(k));
        }

        // Update all edges from update list
        for (int k = 0; k < updateEdgeList.size(); k++) {
            updateEdge(updateEdgeList.get(k));
        }

        // Delete all edges from delete list
        // Will NOT throw an exception if edge doesn't exist
        for (int m = 0; m < removeEdgeList.size(); m++) {
            Location loc1 = removeEdgeList.get(m).getNode1();
            Location loc2 = removeEdgeList.get(m).getNode2();
            removeEdge(loc1, loc2);
        }

        // Delete all nodes from delete list
        // Will NOT throw an exception if node doesn't exist
        for (int m = 0; m < removeLocList.size(); m++) {
            removeNode(removeLocList.get(m));
        }
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.print("Error Closing Connection: \n");
            e.printStackTrace();
        }
    }

    private void addNames(Location loc, int nodeId){
        try {
            Statement stmt = con.createStatement();
            String[] nameList = loc.getNameList();
            for (int i = 0; i < nameList.length; i++) {
                String query = "INSERT INTO mapData.NAMES (NODE_ID, NAME) VALUES " +
                        "( " + nodeId + " , " + '"' + nameList[i] + '"' + " )";
                stmt.execute(query);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public HashMap<Integer, MapImage> getMaps(){

        HashMap<Integer, MapImage> svgList = new HashMap<>();
        SVGUniverse universe = new SVGUniverse();

        try {
            //get all of the maps in the map table
            Statement stmt = con.createStatement();
            String mapQuery = "SELECT * FROM mapData.MAPS";
            ResultSet rs = stmt.executeQuery(mapQuery);

            while (rs.next()) {
                try {
                    //get values of current row
                    String link = rs.getString("IMAGE");
                    int scalex = rs.getInt("SCALE_X");
                    int scaley = rs.getInt("SCALE_Y");
                    //create url
                    URL url = new URL(link);

                    //load in the image
                    universe.loadSVG(url);
                    SVGIcon svg = new SVGIcon();
                    svg.setSvgURI(url.toURI());

                    //set image properties
                    svg.setAntiAlias(true);
                    svg.setClipToViewbox(false);
                    svg.setAutosize(SVGIcon.AUTOSIZE_STRETCH);
                    int svgWidth = svg.getIconWidth();
                    int svgHeight = svg.getIconHeight();
                    svg.setPreferredSize(new Dimension(svgWidth, svgHeight));

                    //add the map infor to a Hash map with the floor number as the key
                    svgList.put(rs.getInt("FLOOR_NUM"), new MapImage(svg, scalex, scaley));
                } catch (IOException | URISyntaxException e) {
                    System.err.printf("Failed while creating URL: %s", e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return svgList;
    }

    /**
     * Adds a map to the database.
     *
     * @param floorNum Floor number associated with the map
     * @param imagePath url to the image
     * @param scalex x scale for the image
     * @param scaley y scale for the image
     */
    public void addMap(int floorNum, String imagePath, int scalex, int scaley){
        try {
            Statement stmt = con.createStatement();
            String mapQuery = "INSERT INTO mapData.MAPS (FLOOR_NUM, IMAGE, SCALE_X, SCALE_Y) VALUES " +
                    "(" + floorNum + "," + '"' + imagePath + '"'
                    + "," + scalex + "," + scaley + ")";
            stmt.execute(mapQuery);
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Removes a map from the database based on the floor number (and/or) image path.
     *
     * @param floorNum floor number of the image to remove (less than 0 if don't care)
     * @param imagePath url of the image to remove (null if don't care)
     */
    public void removeMap(int floorNum, String imagePath){
        String remMap = "DELETE FROM mapData.MAPS WHERE ";

        //add floor number to query if the floor number is greater than 0
        if (floorNum >= 0){
            remMap = remMap + "FLOOR_NUM = " + floorNum;
        }
        //if both floor number and imagePath are used, add comma and "AND"
        if (floorNum >= 0 && (imagePath != null)){
            remMap = remMap + ", AND ";
        }
        //if the image path isn't null add image to query
        if (imagePath != null){
            remMap = remMap + "IMAGE = " +
                    '"' + imagePath + '"';
        }
        try {
            //execute query
            Statement stmt = con.createStatement();
            stmt.execute(remMap);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
