package dev;

import core.Edge;
import core.EdgeAttribute;
import core.Location;
import core.LocationGraph;
import database.Database;
import database.DatabaseList;
import ui.LocationButton;
import ui.MapView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by Alora on 11/21/2015.
 *
 * DevTools is the class that adds all the tools to the map for developers
 * makes devPanel and listeners for editing
 */
public class DevTools extends JPanel {

    private LocationGraph graph;
    private MapView mapView;
    private boolean inDevMode = false;
    private Edge currentEdge;
    private LocationButton lastButtonClicked; //The last button you clicked
    private LocationButton originalButton; //The button you clicked before the last one
    private DatabaseList dblist;

    private JLabel buttonLabel1;
    private JLabel buttonLabel2;

    //Edge attribute check boxes
    private JCheckBox indoors = new JCheckBox("Indoors");
    private JCheckBox handicapAccess = new JCheckBox("Not Handicap Accessible");
    private JCheckBox stairs = new JCheckBox("Stairs");
    private JCheckBox elevator = new JCheckBox("Elevator");

    //Fields with their initial entries (for floor numbers & location names)
    private JFormattedTextField field1 = new JFormattedTextField();
    private JFormattedTextField field2 = new JFormattedTextField();

    /**
     * Creates a DevTools.
     *
     * @param newGraph is the LocationGraph that DevTools works on
     * @param newView is the Mapview that DevTools works on
     */
    public DevTools(LocationGraph newGraph, MapView newView) {
        graph = newGraph;
        mapView = newView;
        dblist = new DatabaseList();

        lastButtonClicked = null;

        setLayout(new BorderLayout());
        this.add(createEditor());

        refreshGraph();
    }

    /**
     * builds a listener for DevTools to use.
     *
     * @param mapPanel Clicked on mapPanel.
     * @return A mouse adapter that can handle adding buttons
     */
    //Mouse adapter for creating LocationButtons
    public MouseAdapter buildAddLocationListener(JPanel mapPanel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inDevMode) {
                    Point2D mousePos = mapPanel.getMousePosition();
                    Point2D.Double doubleMousePos = new Point2D.Double(
                            mousePos.getX() / mapPanel.getWidth(),
                            mousePos.getY() / mapPanel.getHeight());

                    //Creates a new button object where the panel is clicked
                    Location locAdd = new Location(doubleMousePos, mapView.getFloorNumber(), new String[0]);
                    graph.addLocation(locAdd, new HashMap<>());

                    //Adds an edge to the new location if in edge mode
                    if (e.isShiftDown() && lastButtonClicked != null) {
                        addEdge(lastButtonClicked.getAssociatedLocation(), locAdd);
                    }

                    dblist.addedLocation(locAdd);

                    //Set last buttonClicked to the location we just added
                    mapView.updateGraph(graph);
                    mapView.getLocationButtonList().forEach(locationButton -> {
                        if (locationButton.getAssociatedLocation() == locAdd) {
                            lastButtonClicked = locationButton;
                        }
                    });
                    refreshGraph();
                    mapPanel.repaint();
                }
            }
        };
    }

    /**
     * Makes the panels, buttons, fields, and labels for the dev panel.
     *
     * @return JPanel, the sidepanel that is edit mode
     */
    private JPanel createEditor() {
        //Labels that appear on the left side and describe the open fields
        buttonLabel1 = new JLabel("Floor Number:");
        buttonLabel2 = new JLabel("Name List:");

        //Labels that describe the X and Y position of a button
        JLabel xPositionLabel = new JLabel("X Position:");
        JLabel yPositionLabel = new JLabel("Y Position:");

        //Blank labels created to make the formatting of the panel better
        JLabel blank1 = new JLabel("");
        JLabel blank2 = new JLabel("");
        JLabel blank3 = new JLabel("");
        JLabel blank4 = new JLabel("");
        JLabel blank5 = new JLabel("");
        JLabel blank6 = new JLabel("");

        field1.setValue("");
        field1.setToolTipText("<html>The floor number associated with the currently selected node.<br>" +
                "Only valid integers will be accepted.</html>");
        field2.setToolTipText("<html>The list of names that this node should be searchable by.<br>" +
                "Separate multiple names with a comma.</html>");

        //Creates an OK button that updates the nodes with their inputted floor numbers & location names when clicked
        JButton okButton = new JButton("OK");
        okButton.setToolTipText("Apply changes to the local version of this map object.");
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1 && lastButtonClicked != null) {
                    //update values for Location object
                    lastButtonClicked.getAssociatedLocation().setFloorNumber((int) field1.getValue());
                    String tempString = (String) field2.getValue();
                    String[] nameList;
                    if (tempString.contains(",")) {
                        nameList = tempString.split(",");
                    }
                    else if (tempString.length() > 0){
                        nameList = new String[]{tempString};
                    } else {
                        nameList = new String[0];
                    }
                    if (!(field2.getValue() == null)) {
                        for (int i = 0; i < nameList.length; i++) {
                            nameList[i] = nameList[i].trim();
                        }
                        lastButtonClicked.getAssociatedLocation().setNameList(nameList);

                    }
                    dblist.updatedLocation(lastButtonClicked.getAssociatedLocation());
                }
            }
        });

        //Mouse listener for stairs edge attribute
        stairs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1 && currentEdge != null) {
                    if (stairs.isSelected() && !currentEdge.hasAttribute(EdgeAttribute.STAIRS)){
                        currentEdge.addAttribute(EdgeAttribute.STAIRS);
                    } else if (!stairs.isSelected() && currentEdge.hasAttribute(EdgeAttribute.STAIRS)){
                        currentEdge.removeAttribute(EdgeAttribute.STAIRS);
                    }
                    dblist.updatedEdge(currentEdge);
                }
            }
        });

        //Mouse listener for elevator edge attribute
        elevator.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1 && currentEdge != null) {
                    if (elevator.isSelected() && !currentEdge.hasAttribute(EdgeAttribute.ELEVATOR)){
                        currentEdge.addAttribute(EdgeAttribute.ELEVATOR);
                    } else if (!elevator.isSelected() && currentEdge.hasAttribute(EdgeAttribute.ELEVATOR)){
                        currentEdge.removeAttribute(EdgeAttribute.ELEVATOR);
                    }
                    dblist.updatedEdge(currentEdge);
                }
            }
        });

        //Mouse listener for indoors edge attribute
        indoors.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1 && currentEdge != null) {
                    if (indoors.isSelected() && !currentEdge.hasAttribute(EdgeAttribute.INDOORS)){
                        currentEdge.addAttribute(EdgeAttribute.INDOORS);
                    } else if (!indoors.isSelected() && currentEdge.hasAttribute(EdgeAttribute.INDOORS)){
                        currentEdge.removeAttribute(EdgeAttribute.INDOORS);
                    }
                    dblist.updatedEdge(currentEdge);
                }
            }
        });

        //Mouse listener for handicap accessible edge attribute
        handicapAccess.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1 && currentEdge != null) {
                    if (handicapAccess.isSelected() &&
                            !currentEdge.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)){
                        currentEdge.addAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE);
                    } else if (!handicapAccess.isSelected() &&
                            currentEdge.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)){
                        currentEdge.removeAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE);
                    }
                    dblist.updatedEdge(currentEdge);
                }
            }
        });

        field1.setColumns(15);

        //Attach labels to fields
        buttonLabel1.setLabelFor(field1);
        buttonLabel2.setLabelFor(field2);

        //Panel displaying all the labels
        JPanel labelPanel1 = new JPanel(new GridLayout(0, 1, 5, 20));
        JPanel labelPanel2 = new JPanel(new GridLayout(0, 1, 5, 20));
        labelPanel1.add(buttonLabel1);
        labelPanel1.add(buttonLabel2);
        labelPanel1.add(blank3);
        labelPanel1.add(blank5);
        labelPanel2.add(xPositionLabel);
        labelPanel2.add(yPositionLabel);


        //Panel displaying all the fields
        JPanel textPanel1 = new JPanel(new GridLayout(0, 1, 5, 20));
        JPanel textPanel2 = new JPanel(new GridLayout(0, 1, 5, 20));
        textPanel1.add(field1);
        textPanel1.add(field2);
        textPanel1.add(okButton);
        textPanel1.add(blank1);

        textPanel2.add(handicapAccess);
        textPanel2.add(indoors);
        textPanel2.add(stairs);
        textPanel2.add(elevator);

        //create save to database button
        JButton saveToDatabase = new JButton("Save to database");
        saveToDatabase.setToolTipText("Commit the changes made to the online database.");
        saveToDatabase.addActionListener(listener -> {
            try {
                Database graphData = new Database();
                graphData.updateDB(dblist);
                graphData.closeConnection();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mapView.getParent(),
                        "Failed to connect to the online database (be on the internet!)",
                        "Database error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Edge Attributes");
        title.setTitleJustification(TitledBorder.CENTER);
        textPanel2.setBorder(title);

        setElementsEnabled(false);

        //Panel created to display both the label panel and the field panel
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel panelLayout = new JPanel(new BorderLayout());

        panel1.add(labelPanel1, BorderLayout.WEST);
        panel1.add(textPanel1, BorderLayout.EAST);

        panel2.add(labelPanel2, BorderLayout.WEST);
        panel2.add(textPanel2, BorderLayout.EAST);
        panel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel2.setPreferredSize(new Dimension(330, 50));

        panelLayout.add(panel1, BorderLayout.PAGE_START);
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setPreferredSize(new Dimension(330, 5));
        panelLayout.add(separator1, BorderLayout.LINE_END);
        panelLayout.add(panel2, BorderLayout.LINE_START);

        panelLayout.add(saveToDatabase, BorderLayout.SOUTH);
        panelLayout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLayout.setPreferredSize(new Dimension(350, 768));

        return panelLayout;
    }

    private void setElementsEnabled(boolean enabled) {
        buttonLabel1.setEnabled(enabled);
        buttonLabel2.setEnabled(enabled);
        field1.setEnabled(enabled);
        field2.setEnabled(enabled);
        indoors.setEnabled(enabled);
        stairs.setEnabled(enabled);
        elevator.setEnabled(enabled);
        handicapAccess.setEnabled(enabled);
        repaint();
    }

    /**
     * Reset the state, un-selecting an previously selected buttons and re-disabling UI elements.
     */
    public void reset() {
        lastButtonClicked = null;
        originalButton = null;
        setElementsEnabled(false);
    }

    /** Mouse listener that checks for clicks on nodes.
     *  -- changes the behavior when edge mode is selected
     */
    public MouseAdapter buildEditListener(LocationGraph lg) {
        return new MouseAdapter() {
            private boolean pointIsBeingDragged;

            /**
             * Indicate that a drag has started
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                pointIsBeingDragged = true;
            }

            /**
             * A drag has finished. Place the new point in the new location.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (pointIsBeingDragged) {
                    Point2D mousePos = mapView.getMapPanel().getMousePosition();
                    Point2D.Double doubleMousePos = new Point2D.Double(
                            mousePos.getX() / mapView.getMapPanel().getWidth(),
                            mousePos.getY() / mapView.getMapPanel().getHeight());

                    Location clicked = ((LocationButton) e.getSource()).getAssociatedLocation();
                    Location newLoc = new Location(doubleMousePos, clicked.getFloorNumber(), clicked.getNameList());

                    java.util.List<Edge> edgeList = clicked.getEdges();
                    for (int i = 0; i < edgeList.size(); i++) {
                        Edge edge = edgeList.get(i);
                        Location destLoc = edge.getNode1() == clicked ? edge.getNode2() : edge.getNode1();
                        Edge newEdge = newLoc.makeAdjacentTo(destLoc, edge.getAttributes());

                        dblist.addedEdge(newEdge);
                    }

                    dblist.removedLocation(clicked);
                    dblist.addedLocation(newLoc);
                    graph.removeLocation(clicked);
                    graph.addLocation(newLoc, new HashMap<>());

                    refreshGraph();
                    mapView.repaint();
                    pointIsBeingDragged = false;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                originalButton = lastButtonClicked;
                lastButtonClicked = (LocationButton) e.getSource();
                if (lastButtonClicked != null && originalButton != null){
                    //Set the current edge being edited
                    currentEdge = originalButton.getAssociatedLocation()
                            .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
                }
                if (currentEdge != null){
                    updateEdgeAttributes();
                }
                if (lastButtonClicked != null && lastButtonClicked.getAssociatedLocation() != null) {
                    if (originalButton == null) {
                        originalButton = lastButtonClicked;
                    }
                    setElementsEnabled(true);
                    updateLocationData();
                    if (e.getButton() == 1 && e.isShiftDown() && currentEdge == null) {
                        //add an edge with the attributes currently selected
                        addEdge(originalButton.getAssociatedLocation(), lastButtonClicked.getAssociatedLocation());
                    } else if (e.getButton() == 3) { //Right mouse click (deleting edges/nodes)
                        if (!e.isShiftDown()) { //remove the clicked node
                            dblist.removedLocation(lastButtonClicked.getAssociatedLocation());
                            lg.removeLocation(lastButtonClicked.getAssociatedLocation());
                            mapView.remove(lastButtonClicked);
                            reset();
                            mapView.repaint();
                        } else { //remove the selected edge
                            Edge edge = originalButton.getAssociatedLocation()
                                    .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
                            if (edge != null) {
                                originalButton.getAssociatedLocation().removeEdge(edge);
                                dblist.removedEdge(edge);
                            }
                        }
                    }
                    refreshGraph();
                }
            }
        };
    }

    /**
     * Redraws the graph with colors and stuff.
     */
    public void refreshGraph() {
        mapView.updateGraph(graph);

        //Search for the new button that will be last button clicked
        mapView.getLocationButtonList().forEach(loc -> {
            if (lastButtonClicked != null &&
                    loc.getAssociatedLocation() == lastButtonClicked.getAssociatedLocation()) {
                lastButtonClicked = loc;
            }
        });
        //Now set colors
        if (lastButtonClicked != null) {
            lastButtonClicked.setBgColor(Color.CYAN);
        }

        mapView.repaint();
    }

    /**
     * Adds an edge with the currently selected edge attributes.
     */
    private void addEdge(Location loc1, Location loc2){
        ArrayList<EdgeAttribute> listOfAttributes = new ArrayList<>();
        if (handicapAccess.isSelected() &&
                !listOfAttributes.contains(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)){
            listOfAttributes.add(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE);
        }
        if (indoors.isSelected() && !listOfAttributes.contains(EdgeAttribute.INDOORS)){
            listOfAttributes.add(EdgeAttribute.INDOORS);
        }
        if (stairs.isSelected() && !listOfAttributes.contains(EdgeAttribute.STAIRS)){
            listOfAttributes.add(EdgeAttribute.STAIRS);
        }
        if (elevator.isSelected() && !listOfAttributes.contains(EdgeAttribute.ELEVATOR)){
            listOfAttributes.add(EdgeAttribute.ELEVATOR);
        }
        Edge newEdge = loc1.makeAdjacentTo(loc2, listOfAttributes);
        if (newEdge != null) {
            dblist.addedEdge(newEdge);
        }
    }

    /**
     * update the check boxes to reflect the edge attributes of the edge selected.
     */
    private void updateEdgeAttributes(){
        currentEdge = originalButton.getAssociatedLocation()
                .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
        handicapAccess.setSelected(
                currentEdge.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE));
        indoors.setSelected(currentEdge.hasAttribute(EdgeAttribute.INDOORS));
        stairs.setSelected(currentEdge.hasAttribute(EdgeAttribute.STAIRS));
        elevator.setSelected(currentEdge.hasAttribute(EdgeAttribute.ELEVATOR));
    }

    /**
     * Updates the location names and floor number of a node.
     */
    private void updateLocationData(){
        field1.setValue(lastButtonClicked.getAssociatedLocation().getFloorNumber());
        StringBuilder locationNames = new StringBuilder();
        int i;
        if (lastButtonClicked.getAssociatedLocation().getNameList().length > 0) {
            for (i = 0; i < lastButtonClicked.getAssociatedLocation().getNameList().length; i++) {
                locationNames.append(lastButtonClicked.getAssociatedLocation().getNameList()[i]);
                locationNames.append(',');
            }
        }
        field2.setValue(locationNames.toString());
    }

    public boolean getDevMode(){ return inDevMode; }
    public void setDevMode(boolean devMode){ inDevMode = devMode; }
}
