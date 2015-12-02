package dev;

import core.*;
import ui.LocationButton;
import ui.MapView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

    JLabel buttonLabel1;
    JLabel buttonLabel2;
    JCheckBox edgeToggle;

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

        lastButtonClicked = null;

        setLayout(new BorderLayout());
        //this.add(createSaveButton(), BorderLayout.SOUTH);
        this.add(createEditor());

        refreshGraph();
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
                    Location locAdd = new Location(doubleMousePos, mapView.getFloorNumber(), new String[0]);
                    graph.addLocation(locAdd, new HashMap<>());
                    dblist.addedLocation(locAdd);
                    refreshGraph();
                    mapPanel.repaint();
                }
            }
        };
    }

//    //Creates button to save information to the database
//    private JButton createSaveButton() {
//        JButton saveToDatabase = new JButton("Save to database");
//        saveToDatabase.setToolTipText("Commit the changes made to the online database.");
//        saveToDatabase.addActionListener(listener -> {
//            try {
//                Database graphData = new Database();
//                graphData.updateDB(dblist);
//                graphData.closeConnection();
//            } catch (Exception exception) {
//                JOptionPane.showMessageDialog(mapView.getParent(),
//                        "Failed to connect to the online database (be on the internet!)",
//                        "Database error!",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        return saveToDatabase;
//    }

    //Makes the panels, buttons, fields, and labels for the dev panel
    private JPanel createEditor() {
        //Labels that appear on the left side and describe the open fields
        buttonLabel1 = new JLabel("Floor Number:");
        buttonLabel2 = new JLabel("Name List:");

        //Check box to show whether you are in 'edge mode' (where only edges are changed) or not
        edgeToggle = new JCheckBox("Edge Mode");
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
                    System.out.println(tempString.length());
                    System.out.println(tempString);
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
                            nameList[i] = nameList[i].trim().toLowerCase();
                        }
                        lastButtonClicked.getAssociatedLocation().setNameList(nameList);

                    }
                    dblist.updatedLocation(lastButtonClicked.getAssociatedLocation());
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
                if (e.getButton() == 1 && edgeMode) {
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
                if (e.getButton() == 1 && edgeMode) {
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

        labelPanel2.add(edgeToggle);
        labelPanel2.add(blank2);
        labelPanel2.add(blank6);


        //Panel displaying all the fields
        JPanel textPanel1 = new JPanel(new GridLayout(0, 1, 5, 20));
        JPanel textPanel2 = new JPanel(new GridLayout(0, 1, 5, 20));
        textPanel1.add(field1);
        textPanel1.add(field2);
        textPanel1.add(okButton);
        textPanel1.add(blank1);

        textPanel2.add(handicapAccess);
        textPanel2.add(indoors);
        textPanel2.add(blank4);

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
        edgeToggle.setEnabled(enabled);
        field1.setEnabled(enabled);
        field2.setEnabled(enabled);
        indoors.setEnabled(enabled);
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
            @Override
            public void mouseClicked(MouseEvent e) {
                lastButtonClicked = (LocationButton) e.getSource();
                if (lastButtonClicked != null && lastButtonClicked.getAssociatedLocation() != null) {
                    if (originalButton == null) {
                        originalButton = lastButtonClicked;
                    }

                    setElementsEnabled(true);

                    //Updates the location names and floor number of a node
                    field1.setValue(lastButtonClicked.getAssociatedLocation().getFloorNumber());
                    StringBuilder locationNames = new StringBuilder();
                    int i = 0;
                    if (lastButtonClicked.getAssociatedLocation().getNameList().length > 0) {
                        for (i = 0; i < lastButtonClicked.getAssociatedLocation().getNameList().length; i++) {
                            locationNames.append(lastButtonClicked.getAssociatedLocation().getNameList()[i]);
                            locationNames.append(',');
                        }
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
                                handicapAccess.setSelected(
                                        currentEdge.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE));
                                indoors.setSelected(currentEdge.hasAttribute(EdgeAttribute.INDOORS));
                                System.out.println("Not Handicap " +
                                        currentEdge.hasAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE));
                                System.out.println("Indoors " + currentEdge.hasAttribute(EdgeAttribute.INDOORS));
                            } else { //does not have an edge
                                //add an edge
                                ArrayList<EdgeAttribute> listOfAttributes = new ArrayList<>();
                                if (handicapAccess.isSelected() &&
                                        !listOfAttributes.contains(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE)){
                                    listOfAttributes.add(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE);
                                }
                                if (indoors.isSelected() && !listOfAttributes.contains(EdgeAttribute.INDOORS)){
                                    listOfAttributes.add(EdgeAttribute.INDOORS);
                                }
                                Edge newEdge = originalButton.getAssociatedLocation()
                                        .makeAdjacentTo(lastButtonClicked.getAssociatedLocation(), listOfAttributes);
                                if (newEdge != null) {
                                    dblist.addedEdge(newEdge);
                                }
                            }
                        }
                    } else if (e.getButton() == 3) {//Right mouse click
                        if (!edgeMode) {
                            dblist.removedLocation(lastButtonClicked.getAssociatedLocation());
                            lg.removeLocation(lastButtonClicked.getAssociatedLocation());
                            mapView.remove(lastButtonClicked);

                            reset();
                            mapView.repaint();
                        } else {
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

    public void refreshGraph() {
        mapView.updateGraph(graph);

        //Search for the new button that will be last button clicked
        for (LocationButton loc: mapView.getLocationButtonList()) {
            if (lastButtonClicked != null &&
                    loc.getAssociatedLocation() == lastButtonClicked.getAssociatedLocation()) {
                lastButtonClicked = loc;
            }
            if (originalButton != null &&
                    loc.getAssociatedLocation() == originalButton.getAssociatedLocation()) {
                originalButton = loc;
            }
        }
        //Now set colors
        if (originalButton != null && edgeMode) {
            originalButton.setBackground(Color.BLUE);
        }
        if (lastButtonClicked != null) {
            lastButtonClicked.setBackground(Color.CYAN);
        }

        mapView.repaint();
    }

    public boolean getDevMode(){ return inDevMode; }
    public void setDevMode(boolean devMode){ inDevMode = devMode; }
}
