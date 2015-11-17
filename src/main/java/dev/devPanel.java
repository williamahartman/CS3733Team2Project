package dev;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.*;
import core.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alora on 11/14/2015.
 *
 * Creates a blank GUI
 * --Clicking on empty space creates a new node (represented as a button)
 * --Right clicking on a node deletes that node
 * --Left clicking on a node opens a new window with editable node information
 *
 * @TODO create a new location object for each node created graphically
 */
public class devPanel {

//    public devPanel(){
//        //Sets up the framework for the GUI
//        JPanel p = new JPanel(null);
//        JFrame frame = new JFrame();
//        p.setSize(new Dimension(1000,500));
//        frame.setSize(new Dimension(1000,500));
//        frame.setLayout(null);
//
//        p.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                //Creates a new button object where the panel is clicked
//                System.out.println(e.getPoint());
//                float x = e.getX() / frame.getWidth();
//                float y = e.getY() / frame.getHeight();
//                //Location L = new Location(new Point2D.Double(x, y), 0, new String[1], new ArrayList<Edge>() {JButton b = new JButton("NODE");});
//                JButton b = new JButton();
//                b.setBounds(e.getX(), e.getY(), 10, 10);
//                b.addMouseListener(new MouseAdapter() {
//                    @Override
//                    public void mouseClicked(MouseEvent e) {
//                        if(e.getButton() == 1){//Left mouse click
//                            JFrame buttonFrame = new JFrame();
//                            buttonFrame.setSize(new Dimension(300, 100));
//
//                            //Labels that appear on the left side and describe the open fields
//                            JLabel buttonLabel1 = new JLabel("Floor Number: ");
//                            JLabel buttonLabel2 = new JLabel("Name List:");
//                            JLabel buttonLabel3 = new JLabel("");
//
//                            //Fields with their initial entries
//                            JFormattedTextField field1 = new JFormattedTextField("Enter an integer");
//                            JFormattedTextField field2 = new JFormattedTextField("Enter a string");
//
//                            JButton okButton = new JButton("OK");
//
//                            field1.setColumns(10);
//                            field2.setColumns(10);
//
//                            //Attach labels to fields
//                            buttonLabel1.setLabelFor(field1);
//                            buttonLabel2.setLabelFor(field2);
//                            buttonLabel3.setLabelFor(okButton);
//
//                            //Panel displaying all the labels
//                            JPanel labelPanel = new JPanel(new GridLayout(0,1));
//                            labelPanel.add(buttonLabel1);
//                            labelPanel.add(buttonLabel2);
//                            labelPanel.add(buttonLabel3);
//
//                            //Panel displaying all the fields
//                            JPanel textPanel = new JPanel(new GridLayout(0,1));
//                            textPanel.add(field1);
//                            textPanel.add(field2);
//                            textPanel.add(okButton);
//
//                            //Panel created to display both the label panel and the field panel
//                            JPanel panelLayout = new JPanel(new BorderLayout());
//                            panelLayout.add(labelPanel, BorderLayout.CENTER);
//                            panelLayout.add(textPanel, BorderLayout.LINE_END);
//
//                            buttonFrame.add(panelLayout);
//                            buttonFrame.setVisible(true);
//                            panelLayout.repaint();
//
//                            okButton.addMouseListener(new MouseAdapter() {
//                                @Override
//                                public void mouseClicked(MouseEvent e) {
//                                    if(e.getButton() == 1){
//                                        //update values for Location object
//                                        buttonFrame.dispose();
//                                    }
//                                }
//                            });
//                        }
//                        else if(e.getButton() == 3){//Right mouse click
//                            p.remove(b);
//                            p.repaint();
//                        }
//                    }
//                });
//                p.add(b);
//                b.setVisible(true);
//                p.repaint();
//            }
//        });
//        frame.add(p);
//        frame.setVisible(true);
//    }
//
//    public static void main(String[] args){
//        devPanel d = new devPanel();
//
//    }

}
