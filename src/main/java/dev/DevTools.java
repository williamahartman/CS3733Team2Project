package dev;

import core.*;
import ui.LocationButton;
import ui.MainAppUI;
import ui.MapView;
import ui.MapViewStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alora on 11/21/2015.
 */
public class DevTools extends JPanel {

    private LocationGraph graph;
    private MapView mapView;
    private boolean inDevMode = false;
    private Edge currentEdge;
    private LocationButton lastButtonClicked;
    private boolean deleteEdge;
    private boolean edgeMode;
    private LocationButton originalButton;
    private DatabaseList dblist;

    //Edge attribute check boxes
    JCheckBox indoors = new JCheckBox("Indoors");
    JCheckBox handicapAccess = new JCheckBox("Handicap Accessible");

    //Fields with their initial entries (for floor numbers & location names)
    JFormattedTextField field1 = new JFormattedTextField();
    JFormattedTextField field2 = new JFormattedTextField();

    public DevTools(LocationGraph newGraph, MapView newView) {
        graph = newGraph;
        mapView = newView;
        dblist = new DatabaseList();

        //TODO fix for empty location graph
        this.lastButtonClicked = mapView.getLocationButtonList().get(0);
        setLayout(new BorderLayout());
        this.add(createSaveButton(), BorderLayout.SOUTH);
        this.add(createEditor());
    }

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
                    Location locAdd = new Location(doubleMousePos, 0, new String[0]);
                    graph.addLocation(locAdd, new HashMap<>());
                    dblist.addedLocation(locAdd);
                    rebuildGraph();
                }
            }
        };
    }

    //Creates button to save information to the database
    private JButton createSaveButton() {
        JButton saveToDatabase = new JButton("Save to database");
        saveToDatabase.setToolTipText("Commit the changes made to the online database.");
        saveToDatabase.addActionListener(listener -> {
            try {
                Database graphData = new Database();
                graphData.updateDB(graph);
                graphData.closeConnection();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mapView.getParent(),
                        "Failed to connect to the online database (be on the internet!)",
                        "Database error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return saveToDatabase;
    }

    //Makes the panels, buttons, fields, and labels for the dev panel
    private JPanel createEditor() {
        //Labels that appear on the left side and describe the open fields
        JLabel buttonLabel1 = new JLabel("Floor Number:");
        JLabel buttonLabel2 = new JLabel("Name List:");

        //Check box to show whether you are in 'edge mode' (where only edges are changed) or not
        JCheckBox edgeToggle = new JCheckBox("Edge Mode");
        //initially edge mode is off
        edgeToggle.setSelected(false);
        edgeToggle.setToolTipText("<html>Click to activate Edge Mode.<br>" +
            "In Edge Mode, clicking on a new node will make an edge between it and the last node clicked " +
            "before entering edge mode.<br>Similarly, right-clicking on a node will delete " +
            "the edge between it and the last node clicked before entering Edge Mode.</html>");
        //Blank labels created to make the formatting of the panel better
        JLabel blank1 = new JLabel("");
        JLabel blank2 = new JLabel("");
        JLabel blank3 = new JLabel("");
        JLabel blank4 = new JLabel("");
        JLabel blank5 = new JLabel("");
        JLabel blank6 = new JLabel("");

        field1.setValue(lastButtonClicked.getAssociatedLocation()
                .getFloorNumber());
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
                if (e.getButton() == 1) {
                    //update values for Location object
                    lastButtonClicked.getAssociatedLocation().setFloorNumber((int) field1.getValue());
                    String tempString = (String) field2.getValue();
                    String[] nameList = tempString.split(",");
                    if (!field2.getValue().equals("Enter a String")) {
                        for (int i = 0; i < nameList.length; i++) {
                            nameList[i] = nameList[i].trim().toLowerCase();
                        }
                        lastButtonClicked.getAssociatedLocation().setNameList(nameList);
                        dblist.updatedLocation(lastButtonClicked.getAssociatedLocation());
                    }
                }
            }
        });

        //Mouse listener for changing into and out of edge mode
        edgeToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    originalButton = lastButtonClicked;
                    edgeMode = edgeToggle.isSelected();
                    if (!edgeMode){
                        deleteEdge = false;
                    }
                }
            }
        });

        //Mouse listener for indoors edge attribute
        indoors.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    if (indoors.isSelected()){
                        currentEdge.addAttribute(EdgeAttribute.INDOORS);
                    } else {
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
                if (e.getButton() == 1) {
                    if (indoors.isSelected()){
                        currentEdge.addAttribute(EdgeAttribute.HANDICAP_ACCESSIBLE);
                    } else {
                        currentEdge.removeAttribute(EdgeAttribute.HANDICAP_ACCESSIBLE);
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
        JPanel labelPanel = new JPanel(new GridLayout(0, 1, 10, 20));
        //labelPanel.add(blank4);
        labelPanel.add(buttonLabel1);
        labelPanel.add(buttonLabel2);
        labelPanel.add(edgeToggle);
        labelPanel.add(indoors);
        labelPanel.add(handicapAccess);
        labelPanel.add(blank3);

        //Panel displaying all the fields
        JPanel textPanel = new JPanel(new GridLayout(0, 1, 10, 20));
        textPanel.add(field1);
        textPanel.add(field2);
        textPanel.add(blank1);
        textPanel.add(blank5);
        textPanel.add(blank6);
        textPanel.add(blank4);

        //Panel created to display both the label panel and the field panel
        JPanel panelLayout = new JPanel(new BorderLayout());
        panelLayout.add(labelPanel, BorderLayout.WEST);
        panelLayout.add(textPanel, BorderLayout.EAST); //LINE_END
        panelLayout.add(okButton, BorderLayout.SOUTH);

        return panelLayout;
    }

    /** Mouse listener that checks for clicks on nodes.
     *  -- changes the behavior when edge mode is selected
     */
    private MouseAdapter buildEditListener(LocationGraph lg) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lastButtonClicked = (LocationButton) e.getSource();
                lastButtonClicked.repaint();

                //Updates the location names and floor number of a node
                field1.setValue(lastButtonClicked.getAssociatedLocation().getFloorNumber());
                StringBuilder locationNames = new StringBuilder();
                for (int i = 0; i < lastButtonClicked.getAssociatedLocation().getNameList().length; i++){
                    locationNames.append(lastButtonClicked.getAssociatedLocation().getNameList()[i]);
                    locationNames.append(',');
                }
                field2.setValue(locationNames.toString());
                if (e.getButton() == 1) { //Left mouse click
                    if (edgeMode) { //if in Edge Mode
                        currentEdge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
                        if (deleteEdge) {
                            //remove edge
                            if (currentEdge != null) {
                                originalButton.getAssociatedLocation().removeEdge(currentEdge);
                                dblist.removedEdge(currentEdge);
                            }
                        } else if (currentEdge != null) { //already has edge
                            //update the check boxes to reflect the edge attributes of the edge selected
                            handicapAccess.setSelected(currentEdge.hasAttribute(EdgeAttribute.HANDICAP_ACCESSIBLE));
                            indoors.setSelected(currentEdge.hasAttribute(EdgeAttribute.INDOORS));
                            System.out.println("Handicap " +
                                    currentEdge.hasAttribute(EdgeAttribute.HANDICAP_ACCESSIBLE));
                            System.out.println("Indoors " + currentEdge.hasAttribute(EdgeAttribute.INDOORS));
                        } else { //does not have an edge
                            //add an edge
                            Edge newEdge = originalButton.getAssociatedLocation()
                                    .makeAdjacentTo(lastButtonClicked.getAssociatedLocation(), new ArrayList<>());
                            if (newEdge != null) {
                                dblist.addedEdge(newEdge);
                            }
                        }
                    }
                } else if (e.getButton() == 3) {//Right mouse click
                    if (!edgeMode) {
                        lg.removeLocation(lastButtonClicked.getAssociatedLocation());
                        DevTools.this.mapView.remove(lastButtonClicked);
                        originalButton = DevTools.this.mapView.getLocationButtonList().get(0);
                        DevTools.this.mapView.repaint();
                    } else {
                        Edge edge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
                        if (edge != null) {
                            originalButton.getAssociatedLocation().removeEdge(edge);
                            dblist.removedEdge(edge);
                        }
                    }
                }
                rebuildGraph();
                if (!edgeMode) {
                    lastButtonClicked.setBackground(Color.CYAN);
                    lastButtonClicked.repaint();
                }
            }
        };
    }

    public void rebuildGraph() {
        mapView.updateGraph(graph, MainAppUI.floorNumber);

        MouseAdapter editListener = buildEditListener(graph);
        for (LocationButton locationButton: mapView.getLocationButtonList()) {
            locationButton.addMouseListener(editListener);
            if (locationButton.getAssociatedLocation() == lastButtonClicked.getAssociatedLocation()) {
                lastButtonClicked = locationButton;
            }
        }
    }

    public boolean getDevMode(){ return inDevMode; }
    public void setDevMode(boolean devMode){ inDevMode = devMode; }
}
