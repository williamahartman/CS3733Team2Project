import database.Database;
import ui.MainAppUI;
import com.thehowtotutorial.splashscreen.JSplash;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * This runs the application.
 */

public class AppLauncher{
    public static void main(String[] args) {
        try {
            //Set up loading screen
            JSplash splashscreen = new JSplash(AppLauncher.class.getResource (""),
                    true, true, false, "AZTEC WASH Mapper", null, Color.BLACK, Color.BLUE);
            splashscreen.splashOn();
            splashscreen.setProgress(20, "Initializing....");
            Thread.sleep(1000);
            Database graphData = new Database();
            //Make a frame
            MainAppUI app = new MainAppUI(graphData.createGraph());
            graphData.closeConnection();
            splashscreen.setProgress(40, "Loading....");

            //change the look and feel to the Nimbus style
            try {
                UIManager
                        .setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            splashscreen.setProgress(60, "Applying Configurations....");
            Thread.sleep(1000);

            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));
            app.setExtendedState(JFrame.MAXIMIZED_BOTH);
            splashscreen.setProgress(80, "Starting Application....");
            Thread.sleep(1000);

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
