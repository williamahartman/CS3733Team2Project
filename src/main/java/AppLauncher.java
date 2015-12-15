import core.LocationGraph;
import core.MapImage;
import database.Database;
import ui.MainAppUI;
import com.thehowtotutorial.splashscreen.JSplash;

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
            //Set up loading screen
            JSplash splashscreen = new JSplash(AppLauncher.class.getResource ("Aztec_Wash.png"),
                    true, true, false, "AZTEC WASH Mapper", null, Color.BLACK, Color.BLUE);
            splashscreen.splashOn();
            splashscreen.setProgress(15, "Initializing....");
            Thread.sleep(10);
            Database graphData = new Database();

            LocationGraph graph = graphData.createGraph();
            HashMap<Integer, MapImage> floors = graphData.getMaps();
            int defaultFloor = graphData.getDefaultFloor();

            MainAppUI app = new MainAppUI(graph, floors, defaultFloor);
            graphData.closeConnection();
            splashscreen.setProgress(50, "Loading....");
            Thread.sleep(10);


            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));
            app.setExtendedState(JFrame.MAXIMIZED_BOTH);
            splashscreen.setProgress(90, "Starting Application....");
            Thread.sleep(10);

            app.setUpMainApp();
            app.repaint();

            //Show the frame
            app.setVisible(true);
            //turn the loading screen off
            splashscreen.splashOff();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the online database (be on the internet!)",
                    "Database error!",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
