package ui;

import core.Location;

import javax.swing.*;

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
