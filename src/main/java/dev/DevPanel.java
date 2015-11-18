package dev;

import core.Edge;
import core.Location;
import core.LocationGraph;
import ui.LocationButton;
import ui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
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

    public DevPanel(){
    }

    public static void devModeCheck(MouseEvent e, JPanel p, JViewport vp, LocationGraph lg){
        if (inDevMode) {
            Point vpp = vp.getViewPosition();
            //Creates a new button object where the panel is clicked
            double x = (double) (e.getX() + vpp.x) / (double) p.getWidth();
            double y = (double) (e.getY() + vpp.y) / (double) p.getHeight();
            LocationButton b = new LocationButton(new Location(new Point2D.Double(x, y),
                    0, new String[0], new ArrayList<>()));
            b.setBounds(e.getX() + vpp.x, e.getY() + vpp.y, 10, 10);
            lg.addLocation(b.getAssociatedLocation(), new HashMap<>());
            addEditListener(b, lg);
            // move this to locationbutton later

            p.add(b);
            b.setVisible(true);
            p.repaint();
        }//end of dev mode check
    }

    public static void createDevWindow(LocationGraph lg){
        MapView mapView = new MapView(lg,
                "src/main/resources/campusmap.png");

        JFrame frame = new JFrame();
        //Make the scroll pane, set up click and drag
        JScrollPane mapScrollPane = new JScrollPane(mapView);
        mapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            int mouseStartX = 0;
            int mouseStartY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                JViewport viewPort = mapScrollPane.getViewport();
                Point vpp = viewPort.getViewPosition();
                vpp.translate(mouseStartX - e.getXOnScreen(), mouseStartY - e.getYOnScreen());
                mapView.scrollRectToVisible(new Rectangle(vpp, viewPort.getSize()));

                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                mapView.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e){
                DevPanel.devModeCheck(e, mapView, mapScrollPane.getViewport(), lg);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseStartX = e.getXOnScreen();
                mouseStartY = e.getYOnScreen();
                mapScrollPane.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mapScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        };

        for (LocationButton lb: mapView.getLocationButtonList()) {
            addEditListener(lb, lg);
        }

        mapScrollPane.getViewport().addMouseListener(mouseAdapter);
        mapScrollPane.getViewport().addMouseMotionListener(mouseAdapter);
        mapScrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                DevPanel.inDevMode = false;
                mapView.resetGraphData(lg);
                mapView.redrawButtons();
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(mapScrollPane);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setVisible(true);

        DevPanel.inDevMode = true;
    }

    public static void addEditListener(LocationButton b, LocationGraph lg){
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
                        buttonFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
