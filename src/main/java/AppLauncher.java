import core.Database;

import javax.swing.*;

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
        /*
        JFrame frame = new JFrame(getTeamName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Make a panel for the frame, and give it a basic layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //Add stuff to the panel
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel(getTeamName());
            mainPanel.add(label);
        }

        //Add stuff to the frame
        frame.getContentPane().add(mainPanel);
        frame.pack();

        //Show the frame
        frame.setVisible(true);
        */
        try {
            Database test = new Database();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

}
