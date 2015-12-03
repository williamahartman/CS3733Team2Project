import core.Database;
import ui.MainAppUI;

import javax.swing.*;
import java.awt.*;

/**
 * This is a baby easy example of a swing app. This can turn into the real
 * launcher for our app probably.
 */

public class AppLauncher{

    public static void main(String[] args) {
        try {
            Database graphData = new Database();

            //Make a frame
            MainAppUI app = new MainAppUI(graphData.createGraph());
            graphData.closeConnection();

            //change the look and feel to the Nimbus style
            try {
                UIManager
                        .setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setMinimumSize(new Dimension(1024, 768));
            //app.setExtendedState(JFrame.MAXIMIZED_BOTH);

            app.setUpMainApp();

            app.repaint();

            //Show the frame
            app.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the online database (be on the internet!)",
                    "Database error!",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
            System.exit(-1);
        }
    }
}
