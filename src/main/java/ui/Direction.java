package ui;


import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by hollyn on 12/14/15.
 */
public class Direction extends JPanel {

    public Direction(String dir) {
        JPanel dirRow = new JPanel();
        dirRow.setLayout(new FlowLayout());
        dirRow.setBorder(BorderFactory.createLineBorder(Color.black));

        // Set up text pane to add directions to
        JTextArea text = new JTextArea(dir);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setPreferredSize(new Dimension(220, 40));
        text.setEditable(false);

        // Set label as an image later
        JLabel dirLabel = new JLabel("IMG");
        //dirLabel.setIcon(new Icon());

        this.add(dirLabel);
        this.add(text);
    }

}

