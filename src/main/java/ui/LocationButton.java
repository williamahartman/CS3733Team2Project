package ui;

import core.Location;

import javax.swing.*;
import java.awt.*;

/**
 * This class is a JButton that is associated to a Location.
 */
public class LocationButton extends JButton {
    private Location associatedLocation;
    private Color bgColor;
    /**
     * Constructor.
     *
     * @param associatedLocation The Location that will be associated.
     * @param bgColor The background color to be set
     */
    public LocationButton(Location associatedLocation, Color bgColor) {
        super();
        this.bgColor = bgColor;
        this.associatedLocation = associatedLocation;
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(bgColor);
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * Returns the Location that is associated with the LocationButton.
     *
     * @return the Location that is associated with the LocationButton
     */
    public Location getAssociatedLocation() {
        return associatedLocation;
    }
}
