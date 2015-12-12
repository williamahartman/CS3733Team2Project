import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;
import database.Database;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import ui.MainAppUI;
import ui.TextToVoice;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;

/**
 * This runs the application.
 */

public class AppLauncher{

    public static void main(String[] args) {
        try {
            Database graphData = new Database();
            //Make a frame
            MainAppUI app = new MainAppUI(graphData.createGraph(), graphData.getMaps());
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
