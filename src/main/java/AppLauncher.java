import ui.MainAppUI;

import javax.swing.*;
import java.awt.*;

/**
 * This is a baby easy example of a swing app. This can turn into the real
 * launcher for our app probably.
 */
public class AppLauncher{
    public static void main(String[] args) {
        //Make a frame
        MainAppUI app = new MainAppUI(TestGraphMaker.makeTestGraph(),
                "src/main/resources/campusmap.png");

        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setMinimumSize(new Dimension(1024, 768));

        app.setUpMainApp();

        app.repaint();

        //Show the frame
        app.setVisible(true);
    }
}
