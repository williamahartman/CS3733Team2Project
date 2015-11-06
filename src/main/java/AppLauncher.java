import javax.swing.*;

public class AppLauncher{
    public static void main(String[] args) {
        JFrame frame = new JFrame("AZTEC WASH");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for(int i = 0; i < 10; i++) {
            JLabel label = new JLabel("AZTEC WASH!");
            mainPanel.add(label);
        }
        frame.getContentPane().add(mainPanel);
        frame.pack();

        frame.setVisible(true);
    }
}
