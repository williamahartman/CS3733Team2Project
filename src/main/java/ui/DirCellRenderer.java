package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.*;

/**
 * Created by hollyn on 12/14/15.
 */
class DirCellRenderer extends JLabel implements ListCellRenderer<Object> {
    //Need images
    ImageIcon slightRightIcon;
    //ImageIcon hardRightIcon;
    ImageIcon rightIcon;
    ImageIcon slightLeftIcon;
    //ImageIcon hardLeftIcon;
    ImageIcon leftIcon;
    ImageIcon eastIcon;
    ImageIcon northEastIcon;
    ImageIcon southEastIcon;
    ImageIcon westIcon;
    ImageIcon northWestIcon;
    ImageIcon southWestIcon;
    ImageIcon northIcon;
    ImageIcon southIcon;
    ImageIcon extraIcon;
    ImageIcon elevatorIcon;
    ImageIcon stairsIcon;
    public String html1 = "<html><body style='width: ";
    public String html2 = "px'>";
    public String html3 = "</html>";
    private int width;

    public DirCellRenderer (int width) {
        try {
            slightRightIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("slightRightIcon.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            rightIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("rightIcon.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            slightLeftIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("slightLeftIcon.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            leftIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("leftIcon.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            eastIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("east.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            southEastIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("se.png")).getScaledInstance(20, 30, Image.SCALE_SMOOTH));
            northEastIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("ne.png")).getScaledInstance(20, 30, Image.SCALE_SMOOTH));
            westIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("west.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            southWestIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("sw.png")).getScaledInstance(20, 30, Image.SCALE_SMOOTH));
            northWestIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("nw.png")).getScaledInstance(20, 30, Image.SCALE_SMOOTH));
            northIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("north.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            southIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("south.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            extraIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("Smiley.svg.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH));
            elevatorIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("elevator.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            stairsIcon =
                    new ImageIcon(ImageIO.read(getClass().getResource("stairs.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("DID NOT WORK OH NO");
        }
        this.width = width;
    }

    //The list, text value, index, cell selected, cell focus
    public Component getListCellRendererComponent (JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        //Set text of cell
        String s = html1 + String.valueOf(width) + html2 + value.toString() + html3;
        setText(s);

        if (value.toString().contains("North East")) {
            setIcon(northEastIcon);
        } else if (value.toString().contains("North West")) {
            setIcon(northWestIcon);
        } else if (value.toString().contains("South East")) {
            setIcon(southEastIcon);
        } else if (value.toString().contains("South West")) {
            setIcon(southWestIcon);
        } else if (value.toString().contains("North")) {
            setIcon(northIcon);
        } else if (value.toString().contains("South")) {
            setIcon(southIcon);
        } else if (value.toString().contains("East")) {
            setIcon(eastIcon);
        } else if (value.toString().contains("West")) {
            setIcon(westIcon);
        } else if (value.toString().contains("slightly right")) {
            //System.out.println(value.toString() + "Set slightly right icon");
            setIcon(slightRightIcon);
        } else if (value.toString().contains("slightly left")) {
            //System.out.println(value.toString() + "Set slightly left icon");
            setIcon(slightLeftIcon);
        } else if (value.toString().contains("hard right")) {
            //System.out.println(value.toString() + "Set hard right icon");
            setIcon(rightIcon);
        } else if (value.toString().contains("hard left")) {
            //System.out.println(value.toString() + "Set hard left icon");
            setIcon(leftIcon);
        } else if (value.toString().contains("right")) {
            //System.out.println(value.toString() + "Set right icon");
            setIcon(rightIcon);
        } else if (value.toString().contains("left")) {
            //System.out.println(value.toString() + "Set left icon");
            setIcon(leftIcon);
        } else if (value.toString().contains("elevator")) {
            setIcon(elevatorIcon);
        } else if (value.toString().contains("stairs")) {
            setIcon(stairsIcon);
        } else {
            setIcon(extraIcon);
            //System.out.println("Cell: In else");
        }

        //this.setBorder(BorderFactory.createBevelBorder(1, Color.BLUE, Color.cyan, Color.BLUE, Color.cyan));
        setBorder(BorderFactory.createLineBorder(Color.lightGray, 3));

        if (isSelected) {
            //setBorder(BorderFactory.createLineBorder(new Color(4, 206, 220), 4));
            //setBorder(BorderFactory.createLineBorder(new Color(94, 186, 0), 3));
            setBorder(BorderFactory.createLineBorder(new Color(0, 140, 0), 3));
            //setBorder(BorderFactory.createLineBorder(Color.blue, 3));
            //setBackground(new Color(137, 228, 228));
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
