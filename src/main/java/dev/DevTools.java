package dev;

import core.*;
import ui.LocationButton;
import ui.MainAppUI;
import ui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alora on 11/21/2015.
 */
public class DevTools extends JPanel {

    private LocationGraph lg;
    private MapView mv;
    private static boolean inDevMode = false;
    private LocationButton b;
    private Edge currentEdge;
    private boolean deleteEdge;
    private boolean edgeMode;
    private LocationButton originalButton;

    public DevTools(LocationGraph newGraph, MapView newView) {
        lg = newGraph;
        mv = newView;
        b = mv.getLocationButtonList().get(0);
        setLayout(new BorderLayout());
        this.add(createSaveButton(), BorderLayout.SOUTH);
        this.add(createEditor());
        addEditListener(b, lg, mv);
    }

    private void updateGraph() {
        mv.updateGraph(lg, MainAppUI.floorNumber);
        for (LocationButton lb : mv.getLocationButtonList()) {
            addEditListener(lb, lg, mv);
        }
    }

    public void devModeCheck(MouseEvent e, JScrollPane mvs) {
        if (inDevMode) {
            Point vpp = mvs.getViewport().getViewPosition();
            //Creates a new button object where the panel is clicked
            double x = (double) (e.getX() + vpp.x) / (double) mv.getWidth();
            double y = (double) (e.getY() + vpp.y) / (double) mv.getHeight();
            lg.addLocation(new Location(new Point2D.Double(x, y), 0, new String[0]), new HashMap<>());
            updateGraph();
        }//end of dev mode check
    }

    private JButton createSaveButton() {
        JButton saveToDatabase = new JButton("Save to database");
        saveToDatabase.addActionListener(listener -> {
            try {
                Database graphData = new Database();
                //graphData.updateDB(lg);
                graphData.closeConnection();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mv.getParent(),
                        "Failed to connect to the online database (be on the internet!)",
                        "Database error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return saveToDatabase;
    }

    public void updateLastButtonClicked(LocationButton nb){
        b = nb;
    }

    private JPanel createEditor() {
        //Labels that appear on the left side and describe the open fields
        JLabel buttonLabel1 = new JLabel("Floor Number: ");
        JLabel buttonLabel2 = new JLabel("Name List:");
        JCheckBox edgeToggle = new JCheckBox("Edge Mode");

        edgeToggle.setSelected(false);

        //Fields with their initial entries
        JFormattedTextField field1 = new JFormattedTextField(b.getAssociatedLocation()
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
                    b.getAssociatedLocation().setFloorNumber((int) field1.getValue());
                    String tempString = (String) field2.getValue();
                    String[] nameList = tempString.split(",");
                    if (!field2.getValue().equals("Enter a String")) {
                        for (int i = 0; i < nameList.length; i++) {
                            nameList[i] = nameList[i].trim().toLowerCase();
                        }
                        b.getAssociatedLocation().setNameList(nameList);
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
        attributeList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentEdge.addAttribute(EdgeAttribute.values()[attributeList.getSelectedIndex()]);
            }
        });

        edgeToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    originalButton = b;
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

    public void addEditListener(LocationButton b, LocationGraph lg, MapView mapView) {

        b.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                updateLastButtonClicked(b);
                b.setBackground(Color.CYAN);
                b.repaint();
                if (e.getButton() == 1) {//Left mouse click
                    if (edgeMode) {//if in Edge Mode
                        Edge edge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(b.getAssociatedLocation());
                        if (deleteEdge) {
                            //remove edge
                            if (edge != null) {
                                originalButton.getAssociatedLocation().removeEdge(edge);
                                updateGraph();
                                b.setBackground(Color.CYAN);
                            }
                        } else if (edge != null) { //already has edge
                            //TODO change edge attributes
                        } else { //does not have an edge
                            //add an edge
                            originalButton.getAssociatedLocation()
                                    .makeAdjacentTo(b.getAssociatedLocation(), new ArrayList<>());
                        }
                    }
                } else if (e.getButton() == 3) {//Right mouse click
                    if (!edgeMode) {
                        lg.removeLocation(b.getAssociatedLocation());
                        mv.remove(b);
                        originalButton = mv.getLocationButtonList().get(0);
                        mv.repaint();
                    } else {
                        Edge edge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(b.getAssociatedLocation());
                        if (edge != null) {
                            originalButton.getAssociatedLocation().removeEdge(edge);
                            updateGraph();
                        }
                    }
                }
                updateGraph();
                b.setBackground(Color.CYAN);
                b.repaint();
            }
        });

    }
    public static boolean getDevMode(){
        return inDevMode;
    }
    public static void setDevMode(boolean devMode){
        inDevMode = devMode;
    }
}