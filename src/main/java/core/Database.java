package core;


import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Created by Scott on 11/14/2015.
 */
public class Database {
    public Database() throws Exception{
        Connection con = null;
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
}
