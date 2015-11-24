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

    public DevTools(LocationGraph newGraph, MapView newView) {
        graph = newGraph;
        mapView = newView;

        //TODO fix for empty location graph
        this.lastButtonClicked = mapView.getLocationButtonList().get(0);
        setLayout(new BorderLayout());
        this.add(createSaveButton(), BorderLayout.SOUTH);
        this.add(createEditor());
    }

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

    private JPanel createEditor() {
        //Labels that appear on the left side and describe the open fields
        JLabel buttonLabel1 = new JLabel("Floor Number: ");
        JLabel buttonLabel2 = new JLabel("Name List:");
        JCheckBox edgeToggle = new JCheckBox("Edge Mode");

        edgeToggle.setSelected(false);

        //Fields with their initial entries
        JFormattedTextField field1 = new JFormattedTextField(lastButtonClicked.getAssociatedLocation()
                .getFloorNumber());
        JFormattedTextField field2 = new JFormattedTextField();

        field1.setSize(10, 50);


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

        JComboBox attributeList = new JComboBox(EdgeAttribute.values());
        attributeList.addActionListener(e ->
            currentEdge.addAttribute(EdgeAttribute.values()[attributeList.getSelectedIndex()])
        );

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

        //Panel displaying all the fields
        JPanel textPanel = new JPanel(new GridLayout(0, 1));
        textPanel.add(field1);
        textPanel.add(field2);
        textPanel.add(okButton);
        textPanel.add(attributeList);

        //Panel created to display both the label panel and the field panel
        JPanel panelLayout = new JPanel(new BorderLayout());
        panelLayout.add(labelPanel, BorderLayout.WEST);
        panelLayout.add(textPanel, BorderLayout.LINE_END);

        return panelLayout;
    }

    private MouseAdapter buildEditListener(LocationGraph lg) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lastButtonClicked = (LocationButton) e.getSource();
                lastButtonClicked.setBackground(Color.CYAN);
                lastButtonClicked.repaint();
                if (e.getButton() == 1) {//Left mouse click
                    if (edgeMode) {//if in Edge Mode
                        Edge edge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(lastButtonClicked.getAssociatedLocation());
                        if (deleteEdge) {
                            //remove edge
                            if (edge != null) {
                                originalButton.getAssociatedLocation().removeEdge(edge);
                                rebuildGraph();
                                lastButtonClicked.setBackground(Color.CYAN);
                            }
                        } else if (edge != null) { //already has edge
                            //TODO change edge attributes
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
                            rebuildGraph();
                        }
                    }
                }
                rebuildGraph();
                lastButtonClicked.setBackground(Color.CYAN);
                lastButtonClicked.repaint();
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

    public boolean getDevMode(){
        return inDevMode;
    }
    public void setDevMode(boolean devMode){
        inDevMode = devMode;
    }
}
