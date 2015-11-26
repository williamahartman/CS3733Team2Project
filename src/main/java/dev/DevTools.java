package dev;

import core.*;
import ui.LocationButton;
import ui.MainAppUI;
import ui.MapView;

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

    //Edge attribute check boxes
    JCheckBox indoors = new JCheckBox("Indoors");
    JCheckBox handicapAccess = new JCheckBox("Handicap Accessible");

    //Fields with their initial entries (for floor numbers & location names)
    JFormattedTextField field1 = new JFormattedTextField();
    JFormattedTextField field2 = new JFormattedTextField();

    public DevTools(LocationGraph newGraph, MapView newView) {
        graph = newGraph;
        mapView = newView;

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
                    graph.addLocation(new Location(doubleMousePos, 0, new String[0]), new HashMap<>());

                    rebuildGraph();
                }
            }
        };
    }

    //Creates button to save information to the database
    private JButton createSaveButton() {
        JButton saveToDatabase = new JButton("Save to database");
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
        JLabel buttonLabel1 = new JLabel("Floor Number: ");
        JLabel buttonLabel2 = new JLabel("Name List:");

        //Check box to show whether you are in 'edge mode' (where only edges are changed) or not
        JCheckBox edgeToggle = new JCheckBox("Edge Mode");
        //initially edge mode is off
        edgeToggle.setSelected(false);

        //Blank labels created to make the formatting of the panel better
        JLabel blank1 = new JLabel("");
        JLabel blank2 = new JLabel("");

        field1.setValue(lastButtonClicked.getAssociatedLocation()
                .getFloorNumber());

        //Creates an OK button that updates the nodes with their inputted floor numbers & location names when clicked
        JButton okButton = new JButton("OK");
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    //TODO update node attributes
                    //update values for Location object
                    lastButtonClicked.getAssociatedLocation().setFloorNumber((int) field1.getValue());
                    String tempString = (String) field2.getValue();
                    String[] nameList = tempString.split(",");
                    if (!field2.getValue().equals("Enter a String")) {
                        for (int i = 0; i < nameList.length; i++) {
                            nameList[i] = nameList[i].trim().toLowerCase();
                        }
                        lastButtonClicked.getAssociatedLocation().setNameList(nameList);
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
                }
            }
        });

        field1.setColumns(10);
        field2.setColumns(10);

        //Attach labels to fields
        buttonLabel1.setLabelFor(field1);
        buttonLabel2.setLabelFor(field2);

        //Panel displaying all the labels
        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        labelPanel.add(buttonLabel1);
        labelPanel.add(buttonLabel2);
        labelPanel.add(edgeToggle);
        labelPanel.add(indoors);
        labelPanel.add(handicapAccess);

        //Panel displaying all the fields
        JPanel textPanel = new JPanel(new GridLayout(0, 1));
        textPanel.add(field1);
        textPanel.add(field2);
        textPanel.add(okButton);
        textPanel.add(blank1);
        textPanel.add(blank2);

        //Panel created to display both the label panel and the field panel
        JPanel panelLayout = new JPanel(new BorderLayout());
        panelLayout.add(labelPanel, BorderLayout.WEST);
        panelLayout.add(textPanel, BorderLayout.LINE_END);

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
                            originalButton.getAssociatedLocation()
                                    .makeAdjacentTo(lastButtonClicked.getAssociatedLocation(), new ArrayList<>());
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
