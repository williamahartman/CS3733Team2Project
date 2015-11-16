package core;


import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Created by Scott on 11/14/2015.
 */
public class Database {
    Connection con = null;

    /**
     * Constructor.
     * Connects to the database
     */
    public Database() throws Exception {
        //create a file to find the absolute path of the project
        File tempfile = new File("src/main/java/DatabaseTest/Database");
        String path = tempfile.getAbsolutePath();

        //load in the derby client driver
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        //connect to the database
        con = DriverManager.getConnection(
                    "jdbc:derby:" + path ,
                    "aztecwash", "aztecwash");
        Statement sta = con.createStatement();
        //get all nodes stored in the database
        ResultSet res = sta.executeQuery(
                "SELECT * FROM NODES");
        System.out.println("List of Nodes: ");
        while (res.next()) {
            System.out.println(
                    "  " + res.getInt("NODE_ID")
                            + ", " + res.getDouble("POS_X")
                            + ", " + res.getDouble("POS_Y") );
        }

    }

    /*
     * Add a node to the database
     *
     * @param locToAdd The location/node that needs to be added to the database
     */
    public void AddNode(Location locToAdd) {
        //// TODO: 11/15/2015 Add in names functionality
        //get the x and y of the node to add
        double x = locToAdd.getPosition().getX();
        double y = locToAdd.getPosition().getY();
        //get the floor number
        int floorNum = locToAdd.getFloorNumber();

        //query for inserting the node into the database
        String query = "INSERT INTO NODES (POS_X, POS_Y, FLOOR_NUM) VALUES " +
                "(" + x + "," + y + "," + floorNum + ")";
        try {
            Statement stmt = con.createStatement();
            //execute the query to add the node
            stmt.execute(query);
            /* Add location NAMES
            for (int i = 0; i < locToAdd.) {

            */
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }
    }

    /*
     * Remove a node from the database
     *
     * @param locToRem The location/node that needs to be removed to the database
     */
    public void RemoveNode(Location locToRem) {
        //get the x and y of the location
        double x = locToRem.getPosition().getX();
        double y = locToRem.getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            //get the id of the node that is to be removed
            String getID = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
            x + "and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            int nodeId = rs.getInt("NODE_ID");
            /*
            String remNodeEdge = "DELETE FROM NODE_EDGES WHERE NODE_ID ="
                    + nodeId;
            stmt.execute(remNodeEdge);
            */
            // Remove the edges that include this node
            String remEdge = "DELETE FROM EDGES WHERE NODE1_ID = "
                    + nodeId + "or NODE2_ID = " + nodeId;
            //execute query to remove edges
            stmt.execute(remEdge);

            //remove the node
            String remNode = "DELETE FROM NODES WHERE POS_X = " +
                     x + "and POS_Y =" + y;
            //execute the query to remove nodes
            stmt.execute(remNode);

        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }

    }

    /*
     * Update a node in the database
     *
     * @param locToUpdate The location/node that needs to be updated in the database
     */
    public void UpdateNode(Location locToUpdate) {
        //// TODO: 11/15/2015 will add when adding names
        /*
        double x = locToUpdate.getPosition().getX();
        double y = locToUpdate.getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            String getID = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x + "and POS_Y =" + y;
            ResultSet rs = stmt.executeQuery(getID);
            int nodeId = rs.getInt("NODE_ID");
            String remNodeEdge = "DELETE FROM NODE_EDGES WHERE NODE_ID ="
                    + nodeId;
            String remEdge = "DELETE FROM EDGES WHERE NODE1_ID = "
                    + nodeId + "or NODE2_ID = " + nodeId;
            String remNode = "DELETE FROM NODES WHERE POS_X = " +
                    x + "and POS_Y =" + y;
            stmt.execute(remNodeEdge);
            stmt.execute(remEdge);
            stmt.execute(remNode);
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }
        */
    }

    /*
     * Add an edge to the database
     *
     * @param edgeToAdd The edge that needs to be added to the database
     */
    public void AddEdge(Edge edgeToAdd) {
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
            String getID1 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x1 + "and POS_Y =" + y1;
            //query to get the id of the second node
            String getID2 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x2 + "and POS_Y =" + y2;
            //get the result set of the query for the first nodeID
            ResultSet rs = stmt.executeQuery(getID1);
            //store the nodeID
            int nodeId1 = rs.getInt("NODE_ID");
            //get the result set of the query for the second nodeID
            rs = stmt.executeQuery(getID2);
            //store the node ID
            int nodeId2 = rs.getInt("NODE_ID");

            //query for inserting the edge into the database
            String query = "INSERT INTO EDGES (NODE1_ID, NODE2_ID) VALUES " +
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
    public void RemoveEdge(Edge edgeToRem) {
        //gets the x and y of both nodes in the edge
        double x1 = edgeToRem.getNode1().getPosition().getX();
        double y1 = edgeToRem.getNode1().getPosition().getY();
        double x2 = edgeToRem.getNode2().getPosition().getX();
        double y2 = edgeToRem.getNode2().getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            //query to get the first nodeID
            String getID1 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x1 + "and POS_Y =" + y1;
            //query to get the second nodeID
            String getID2 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x2 + "and POS_Y =" + y2;
            //execute the query to get the first nodeID
            ResultSet rs = stmt.executeQuery(getID1);
            //store the first nodeID
            int nodeId1 = rs.getInt("NODE_ID");
            //execute the query to get the second nodeID
            rs = stmt.executeQuery(getID2);
            //store the second nodeID
            int nodeId2 = rs.getInt("NODE_ID");
            
            /*
            String remNodeEdge = "DELETE FROM NODE_EDGES WHERE EDGE_ID ="
                    + nodeId;
                    */
            //query to delete the edge from the database
            String remEdge = "DELETE FROM EDGES WHERE (NODE1_ID = "
                    + nodeId1 + "AND NODE2_ID = " + nodeId2 + 
                    ") OR ( NODE1_ID = " + nodeId2 + "AND NODE2_ID = " + nodeId1 + ")";
            //remove the edge from the database
            stmt.execute(remEdge);
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }

    }

    /*
     * Update an edge in the database
     *
     * @param edgeToUpdate The edge that needs to be updated in the database
     */
    public void UpdateEdge(Edge edgeToUpdate) {
        //// TODO: 11/15/2015 Add in update funcitonality 

    }
}
