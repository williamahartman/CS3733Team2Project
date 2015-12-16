import com.thehowtotutorial.splashscreen.JSplash;
import core.LocationGraph;
import core.MapImage;
import database.Database;
import ui.MainAppUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This runs the application.
 */

public class AppLauncher{
    public static void main(String[] args) {
        try {
            //Set up loading screen
            JSplash splashscreen = new JSplash(AppLauncher.class.getResource ("Aztec_Wash2.png"),
                    true, true, false, "", null, Color.BLACK, Color.BLUE);
            splashscreen.splashOn();
            splashscreen.setProgress(15, "Initializing....");
            Thread.sleep(10);
            Database graphData = new Database();

            LocationGraph graph = graphData.createGraph();
            splashscreen.setProgress(25, "Initializing....");
            HashMap<Integer, MapImage> floors = graphData.getMaps();
            splashscreen.setProgress(40, "Initializing....");
            int defaultFloor = graphData.getDefaultFloor();


            BufferedImage compass = ImageIO.read(AppLauncher.class.getResourceAsStream("compass.png"));

            MainAppUI app = new MainAppUI(graph, floors, defaultFloor, compass);
            graphData.closeConnection();
            splashscreen.setProgress(50, "Loading....");
            Thread.sleep(10);


            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));
            splashscreen.setProgress(70, "Loading....");
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
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
