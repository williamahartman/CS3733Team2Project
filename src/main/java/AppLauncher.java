import database.Database;
import ui.MainAppUI;
import ui.VoiceThread;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * This runs the application.
 */

public class AppLauncher{

    public static void main(String[] args) {
        try {
            Database graphData = new Database();
            VoiceThread speak = new VoiceThread("Starting W.P.I Mapper");
            try {
                speak.start();
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            try {
                System.out.println(speak.getState());
                if(speak.getState() == Thread.State.WAITING){
                    speak.interrupt();
                }
                speak = new VoiceThread("This is a test");
                speak.start();
                Thread.sleep(2000);

                //speak.interrupt();
            } catch (InterruptedException e) {}

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
