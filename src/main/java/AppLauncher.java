import ui.MapView;

import javax.swing.*;
import java.awt.*;

/**
 * This is a baby easy example of a swing app. This can turn into the real
 * launcher for our app probably.
 */
public class AppLauncher{
    /**
     * This method just always returns the name of our team.
     * We test this method with JUnit in src/test/java/TestPlaceholder.java
     *
     * @return The name of our amazing team!
     */
    public static String getTeamName() {
        return "AZTEC WASH!";
    }

    public static void main(String[] args) {
        //Make a frame
        JFrame frame = new JFrame(getTeamName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));

        //Make the map
        JPanel mapView = new MapView(TestGraphMaker.makeTestGraph(),
                "src/main/resources/campusmap.png");

        JScrollPane mapScrollPane = new JScrollPane(mapView);
        mapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(mapScrollPane);

        frame.repaint();
        frame.pack();

        //Show the frame
        frame.setVisible(true);
    }
}
