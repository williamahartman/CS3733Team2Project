package ui;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import dev.DevPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * This class is a JButton that is associated to a Location.
 */
public class LocationButton extends JButton {
    private Location associatedLocation;

    /**
     * Constructor.
     *
     * @param associatedLocation The Location that will be associated.
     */
    public LocationButton(Location associatedLocation) {
        super();
        this.associatedLocation = associatedLocation;
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
