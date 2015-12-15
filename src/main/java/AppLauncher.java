import core.LocationGraph;
import core.MapImage;
import database.Database;
import ui.MainAppUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This runs the application.
 */

public class AppLauncher{

    public static void main(String[] args) {
        try {
            Database graphData = new Database();

            LocationGraph graph = graphData.createGraph();
            HashMap<Integer, MapImage> floors = graphData.getMaps();
            int defaultFloor = graphData.getDefaultFloor();

            MainAppUI app = new MainAppUI(graph, floors, defaultFloor);
            graphData.closeConnection();

            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));
            app.setExtendedState(JFrame.MAXIMIZED_BOTH);

            app.setUpMainApp();

            app.repaint();

            //Show the frame
            app.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the online database (be on the internet!)",
                    "Database error!",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
            System.exit(-1);
        }
    }
}
