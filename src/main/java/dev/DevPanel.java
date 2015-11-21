package dev;

import core.Database;
import core.Edge;
import core.Location;
import core.LocationGraph;
import ui.LocationButton;
import ui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alora on 11/14/2015.
 *
 * DevPanel is a helper class that allows you to add
 * Dev Tools to any panel (such as a map)
 * by calling its static function(s)
 *
 * To activate developer mode, flip the static bool
 *
 */
public class DevPanel {

    // booleans to toggle developer features
    public static boolean inDevMode = false;
    private static boolean edgeMode = false;
    // temporary storage for the button being edited;
    private static LocationButton originalButton;

    private static void updateGraph(LocationGraph lg, MapView mapView) {
        mapView.updateGraph(lg);

        for (LocationButton lb: mapView.getLocationButtonList()) {
            addEditListener(lb, lg, mapView);
        }
    }

    public static void devModeCheck(JPanel p, LocationGraph lg){
        if (inDevMode) {
            //Creates a new button object where the panel is clicked
            double x = p.getMousePosition().x;
            double y = p.getMousePosition().y;
            lg.addLocation(new Location(new Point2D.Double(x, y), 0, new String[0]), new HashMap<>());
        }
    }

    public static void createDevWindow(BufferedImage backgroundImage, double defaultZoom, LocationGraph lg){
        MapView mapView = new MapView(lg, backgroundImage, defaultZoom);

        JFrame frame = new JFrame();

        mapView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                DevPanel.devModeCheck(mapView.getMapPanel(), lg);
                updateGraph(lg, mapView);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DevPanel.inDevMode = false;
            }
        });

        JButton saveToDatabase = new JButton("Save to database");
        saveToDatabase.addActionListener(listener -> {
            try {
                Database graphData = new Database();
                graphData.updateDB(lg);
                graphData.closeConnection();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(frame,
                        "Failed to connect to the online database (be on the internet!)",
                        "Database error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        frame.setLayout(new BorderLayout());
        frame.add(mapView);
        frame.add(saveToDatabase, BorderLayout.SOUTH);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setVisible(true);

        DevPanel.inDevMode = true;
        updateGraph(lg, mapView);
    }

    public static void addEditListener(LocationButton b, LocationGraph lg, MapView mapView){
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {//Left mouse click
                    if (edgeMode){//if in Edge Mode
                        Edge edge = originalButton.getAssociatedLocation()
                                .getConnectingEdgeFromNeighbor(b.getAssociatedLocation());
                        if (edge != null){ //already has edge
                            //remove edge
                            originalButton.getAssociatedLocation().removeEdge(edge);
                        } else { //does not have an edge
                            //add an edge
                            originalButton.getAssociatedLocation()
                                    .makeAdjacentTo(b.getAssociatedLocation(), new ArrayList<>());
                        }
                    } else {
                        JFrame buttonFrame = new JFrame();
                        buttonFrame.setLocation(800, 0);
                        buttonFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                        buttonFrame.setSize(new Dimension(300, 100));

                        //Labels that appear on the left side and describe the open fields
                        JLabel buttonLabel1 = new JLabel("Floor Number: ");
                        JLabel buttonLabel2 = new JLabel("Name List:");
                        JCheckBox nodeButton = new JCheckBox("Edge Mode");

                        nodeButton.setSelected(false);


                        //Fields with their initial entries
                        JFormattedTextField field1 = new JFormattedTextField(b.getAssociatedLocation()
                                .getFloorNumber());
                        JFormattedTextField field2 = new JFormattedTextField();
                        if (b.getAssociatedLocation().getNameList().length == 0) {
                            field2.setValue("Enter a String");
                        } else {
                            String tempString = "";
                            for (int i = 0; i < b.getAssociatedLocation().getNameList().length; i++) {
                                tempString = tempString.concat(b.getAssociatedLocation().getNameList()[i]);
                                tempString = tempString.concat(",");
                            }
                            field2.setValue(tempString);
                        }

                        JButton okButton = new JButton("OK");

                        field1.setColumns(10);
                        field2.setColumns(10);

                        //Attach labels to fields
                        buttonLabel1.setLabelFor(field1);
                        buttonLabel2.setLabelFor(field2);

                        //Panel displaying all the labels
                        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
                        labelPanel.add(buttonLabel1);
                        labelPanel.add(buttonLabel2);
                        labelPanel.add(nodeButton);

                        //Panel displaying all the fields
                        JPanel textPanel = new JPanel(new GridLayout(0, 1));
                        textPanel.add(field1);
                        textPanel.add(field2);
                        textPanel.add(okButton);

                        //Panel created to display both the label panel and the field panel
                        JPanel panelLayout = new JPanel(new BorderLayout());
                        panelLayout.add(labelPanel, BorderLayout.CENTER);
                        panelLayout.add(textPanel, BorderLayout.LINE_END);

                        buttonFrame.add(panelLayout);
                        buttonFrame.setVisible(true);
                        panelLayout.repaint();

                        okButton.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getButton() == 1) {
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
                                    buttonFrame.dispose();
                                    edgeMode = false;
                                }
                            }
                        });
                        nodeButton.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getButton() == 1) {
                                    originalButton = b;
                                    edgeMode = nodeButton.isSelected();
                                }
                            }
                        });
                    }
                } else if (e.getButton() == 3) {//Right mouse click
                    lg.removeLocation(b.getAssociatedLocation());
                    Container temp =  b.getParent();
                    temp.remove(b);
                    temp.repaint();
                }
                updateGraph(lg, mapView);
            }
        });
    }

//    public static void main(String[] args){
//        //Sets up the framework for the GUI
//        JPanel p = new JPanel(null);
//        JFrame frame = new JFrame();
//        p.setSize(new Dimension(1000, 500));
//        frame.setSize(new Dimension(1000, 500));
//        frame.setLayout(null);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.add(p);
//        DevPanel.addDevFunction(p);
//        inDevMode = true;
//        frame.setVisible(true);
//        frame.repaint();
//    }
}
