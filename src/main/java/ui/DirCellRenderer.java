package ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by hollyn on 12/14/15.
 */
class DirCellRenderer extends JLabel implements ListCellRenderer<Object> {
    //Need images
    ImageIcon slightRightIcon =
            new ImageIcon("slightRightIcon.png", "slight right");
    ImageIcon hardRightIcon; /* =
            new ImageIcon(getClass().getClassLoader().getResource("resources/hardRightIcon.png"));*/
    ImageIcon rightIcon; /* =
            new ImageIcon(getClass().getClassLoader().getResource("resources/rightIcon.png"));*/
    ImageIcon slightLeftIcon; /* =
            new ImageIcon(getClass().getClassLoader().getResource("resources/slightLeftIcon.png"));*/
    ImageIcon hardLeftIcon; /* =
            new ImageIcon(getClass().getClassLoader().getResource("resources/hardLeftIcon.png"));*/
    ImageIcon leftIcon; /*=
            new ImageIcon(getClass().getClassLoader().getResource("resources/leftIcon.png"));*/
    ImageIcon dummyIcon;
    public String html1 = "<html><body style='width: ";
    public String html2 = "px'>";
    public String html3 = "</html>";
    private int width;

    public DirCellRenderer (int width) {
        this.width = width;
    }

    //The list, text value, index, cell selected, cell focus
    public Component getListCellRendererComponent (JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        //Set text of cell
        String s = html1 + String.valueOf(width) + html2 + value.toString() + html3;
        setText(s);

        if (s.contains("slightly right")) {
            setIcon(slightRightIcon);
        } else if (s.contains("slightly left")) {
            setIcon(slightLeftIcon);
        } else if (s.contains("hard right")) {
            setIcon(hardRightIcon);
        } else if (s.contains("hard left")) {
            setIcon(hardLeftIcon);
        } else if (s.contains("right")) {
            setIcon(rightIcon);
        } else if (s.contains("left")) {
            setIcon(leftIcon);
        } else {
            setIcon(dummyIcon);
        }

        //this.setBorder(BorderFactory.createBevelBorder(1, Color.BLUE, Color.cyan, Color.BLUE, Color.cyan));
        setBorder(BorderFactory.createLineBorder(Color.lightGray, 3));

        if (isSelected) {
            //setBorder(BorderFactory.createLineBorder(new Color(4, 206, 220), 4));
            setBorder(BorderFactory.createLineBorder(Color.black, 4));
            setBackground(new Color(137, 228, 228));
            //setBackground(new Color(247, 247, 148));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.lightGray, 3));
            setBackground(Color.white);
        }

        setEnabled(list.isEnabled());
        setOpaque(true);

        return this;
    }
}
