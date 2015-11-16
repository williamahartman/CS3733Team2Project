package core;


import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Created by Scott on 11/14/2015.
 */
public class Database {
    Connection con = null;
    public Database() throws Exception{
        File tempfile = new File("src/main/java/DatabaseTest/Database");
        String path = tempfile.getAbsolutePath();
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection(
                    "jdbc:derby:" + path ,
                    "aztecwash", "aztecwash");
            Statement sta = con.createStatement();
        ResultSet res = sta.executeQuery(
                "SELECT * FROM NAMES");
        System.out.println("List of Addresses: ");
        while (res.next()) {
            System.out.println(
                    "  " + res.getInt("NAMELIST_ID")
                            + ", " + res.getString("NAME"));
        }

    }

    /*
     * @param locToAdd The location/node that needs to be added to the database
     */
    public void AddNode(Location locToAdd) {
        //// TODO: 11/15/2015 Add in names functionality
        double x = locToAdd.getPosition().getX();
        double y = locToAdd.getPosition().getY();
        int floorNum = locToAdd.getFloorNumber();
        String query = "INSERT INTO NODES (POS_X, POS_Y, FLOOR_NUM)" +
                "(" + x + "," + y + "," + floorNum + ")";
        String addNames = null;
        try {
            Statement stmt = con.createStatement();
            stmt.execute(query);
            /* Add location NAMES
            for (int i = 0; i < locToAdd.) {

            */
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }
    }

    /*
     * @param locToRem The location/node that needs to be removed to the database
     */
    public void RemoveNode(Location locToRem) {
        double x = locToRem.getPosition().getX();
        double y = locToRem.getPosition().getY();
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

    }

    /*
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
     * @param edgeToAdd The edge that needs to be added to the database
     */
    public void AddEdge(Edge edgeToAdd) {
        //// TODO: 11/15/2015 add in attributes functionality
        double x1 = edgeToAdd.getNode1().getPosition().getX();
        double y1 = edgeToAdd.getNode1().getPosition().getY();
        double x2 = edgeToAdd.getNode2().getPosition().getX();
        double y2 = edgeToAdd.getNode2().getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            String getID1 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x1 + "and POS_Y =" + y1;
            String getID2 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x2 + "and POS_Y =" + y2;
            ResultSet rs = stmt.executeQuery(getID1);
            int nodeId1 = rs.getInt("NODE_ID");
            rs = stmt.executeQuery(getID2);
            int nodeId2 = rs.getInt("NODE_ID");

            String query = "INSERT INTO NODES (NODE1_ID, NODE2_ID)" +
                    "(" + nodeId1 + "," + nodeId2 + ")";
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }


    }

    /*
     * @param edgeToRem The edge that needs to be removed to the database
     */
    public void RemoveEdge(Edge edgeToRem) {
        double x1 = edgeToRem.getNode1().getPosition().getX();
        double y1 = edgeToRem.getNode1().getPosition().getY();
        double x2 = edgeToRem.getNode2().getPosition().getX();
        double y2 = edgeToRem.getNode2().getPosition().getY();
        try {
            Statement stmt = con.createStatement();
            String getID1 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x1 + "and POS_Y =" + y1;
            String getID2 = "SELECT NODE_ID FROM NODES WHERE POS_X = "  +
                    x2 + "and POS_Y =" + y2;
            ResultSet rs = stmt.executeQuery(getID1);
            int nodeId1 = rs.getInt("NODE_ID");
            rs = stmt.executeQuery(getID2);
            int nodeId2 = rs.getInt("NODE_ID");
            
            /*
            String remNodeEdge = "DELETE FROM NODE_EDGES WHERE EDGE_ID ="
                    + nodeId;
                    */
            String remEdge = "DELETE FROM EDGES WHERE (NODE1_ID = "
                    + nodeId1 + "AND NODE2_ID = " + nodeId2 + 
                    ") OR ( NODE1_ID = " + nodeId2 + "AND NODE2_ID = " + nodeId1 + ")";
            stmt.execute(remEdge);
        } catch (SQLException e) {
            System.out.print("Couldn't add location\n" + e);
        }

    }

    /*
     * @param edgeToUpdate The edge that needs to be updated in the database
     */
    public void UpdateEdge(Edge edgeToUpdate) {
        //// TODO: 11/15/2015 Add in update funcitonality 

    }
}
