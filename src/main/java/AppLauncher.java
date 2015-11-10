import javax.swing.*;

public class AppLauncher{

    //We test this method with JUnit in src/test/java/TestPlaceholder.java
    public static String getTeamName() {
        return "AZTEC WASH!";
    }

    public static void main(String[] args) {
        //Make a frame
        JFrame frame = new JFrame(getTeamName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Make a panel for the frame, and give it a basic layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //Add stuff to the panel
        for(int i = 0; i < 10; i++) {
            JLabel label = new JLabel(getTeamName());
            mainPanel.add(label);
        }

        //Add stuff to the frame
        frame.getContentPane().add(mainPanel);
        frame.pack();

        //Show the frame
        frame.setVisible(true);
    }
}
