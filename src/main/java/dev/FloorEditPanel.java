package dev;

import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;
import core.MapImage;
import database.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Will on 12/13/2015.
 */
public class FloorEditPanel extends JPanel {
    private int floorNum;

    private String imageURL;
    private String scaleX;
    private String scaleY;

    /**
     * Constructor.
     *
     * @param floorNum  the floor number of this object
     * @param floor The floor the fields will be populated from.
     */
    public FloorEditPanel(int floorNum, MapImage floor) {
        this.floorNum = floorNum;

        this.imageURL = floor.getSVG().getSvgURI().toString();
        this.scaleX = "" + floor.getScaleX();
        this.scaleY = "" + floor.getScaleY();

        setUpPanel();
    }

    /**
     * Constructor.
     *
     * @param floorNum the floor number of this object
     * @param imageURL the image URL
     * @param scaleX the x scale as a string
     * @param scaleY the y scale as a string
     */
    public FloorEditPanel(int floorNum, String imageURL, String scaleX, String scaleY) {
        this.floorNum = floorNum;
        this.imageURL = imageURL;
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        setUpPanel();
    }

    private void setUpPanel() {
        setBorder(BorderFactory.createTitledBorder(""));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel floorLabel = new JLabel("Index " + floorNum);

        JLabel imgLinkLabel = new JLabel(".svg URL:   ");
        JTextField imgLink = new JTextField(imageURL, 20);
        imgLink.setMinimumSize(new Dimension(200, 30));
        imgLink.setPreferredSize(new Dimension(200, 30));
        imgLink.setMaximumSize(new Dimension(200, 30));
        imgLink.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                imgLink.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    URI imgURI = new URI(imgLink.getText());
                    imageURL = imgURI.toString();
                } catch (URISyntaxException ex) {
                    JOptionPane.showMessageDialog(null,
                            "The entered value is not a valid URI!",
                            "Invalid Input!",
                            JOptionPane.ERROR_MESSAGE);
                }
                checkSVGValid();
            }
        });

        add(imgLinkLabel);
        add(imgLink);

        JLabel scaleXLabel = new JLabel("Width (feet):   ");
        JTextField xScale = new JTextField("" + scaleX, 4);
        xScale.setMinimumSize(new Dimension(40, 30));
        xScale.setPreferredSize(new Dimension(40, 30));
        xScale.setMaximumSize(new Dimension(40, 30));
        xScale.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                xScale.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    //Check that the entered text is a number
                    Integer.parseInt(xScale.getText());
                    scaleX = xScale.getText();
                } catch (NumberFormatException ex) {
                    xScale.setText("" + scaleX);
                    JOptionPane.showMessageDialog(null,
                            "The entered value is not a valid integer!",
                            "Invalid Input!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel scaleYLabel = new JLabel("Height (feet):   ");
        JTextField yScale = new JTextField("" + scaleY, 4);
        yScale.setMinimumSize(new Dimension(40, 30));
        yScale.setPreferredSize(new Dimension(40, 30));
        yScale.setMaximumSize(new Dimension(40, 30));
        yScale.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                yScale.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    //Check that the entered text is a number
                    Integer.parseInt(yScale.getText());
                    scaleY = yScale.getText();
                } catch (NumberFormatException ex) {
                    yScale.setText("" + scaleY);
                    JOptionPane.showMessageDialog(null,
                            "The entered value is not a valid integer!",
                            "Invalid Input!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(Box.createHorizontalStrut(10));
        add(Box.createGlue());
        add(floorLabel);
        add(Box.createHorizontalStrut(20));
        add(imgLinkLabel);
        add(imgLink);
        add(Box.createHorizontalStrut(20));
        add(scaleXLabel);
        add(xScale);
        add(Box.createHorizontalStrut(20));
        add(scaleYLabel);
        add(yScale);
        add(Box.createHorizontalStrut(10));
    }

    /**
     * Build a map to add to the DB, based on the info held in this object.
     *
     * @param d the database the map will be added to
     * @param floor the floor number that will be associated
     */
    public void addMapToDatabase(Database d, int floor) {
        if (checkValid()) {
            d.addMap(floor, imageURL, Integer.parseInt(scaleX), Integer.parseInt(scaleY));
        }
    }

    public MapImage buildMapImage() {
        SVGIcon svg = new SVGIcon();
        try {
            URL url = new URL(imageURL);
            SVGUniverse universe = new SVGUniverse();
            universe.loadSVG(url);
            svg.setSvgURI(url.toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        MapImage result = new MapImage(svg, Integer.parseInt(scaleX), Integer.parseInt(scaleY));
        return result;
    }

    private boolean checkValid() {
        try {
            Integer.parseInt(scaleX);
            Integer.parseInt(scaleY);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "The value " + scaleX + " or " + scaleY + "is not a valid integer!",
                    "Invalid Input!",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return checkSVGValid();
    }

    private boolean checkSVGValid() {
        try {
            URL url = new URL(imageURL);
            SVGUniverse universe = new SVGUniverse();
            universe.loadSVG(url);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "The entered URL (" + imageURL + ") does not appear to have a valid .svg file!",
                    "Invalid Input!",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
