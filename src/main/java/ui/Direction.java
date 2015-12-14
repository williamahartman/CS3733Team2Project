package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hollyn on 12/14/15.
 */
public class Direction extends JPanel {
    private JLabel dirLabel;
    private JTextArea text;

    public Direction(String dir) {

        this.setLayout(new FlowLayout());

        // Set text to string that was passed in
        text = new JTextArea(dir);
        text.setPreferredSize(new Dimension(220, 40));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);

        // Set label as an image later
        dirLabel = new JLabel("IMG");
        //dirLabel.setIcon(new Icon());

        this.add(dirLabel);
        this.add(text);
    }

}
