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
 */
public class devPanel {

    public devPanel(){
        JPanel p = new JPanel(null);
        JFrame frame = new JFrame();
        p.setSize(new Dimension(1000,500));
        frame.setSize(new Dimension(1000,500));
        frame.setLayout(null);

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getPoint());
                float x = e.getX() / frame.getWidth();
                float y = e.getY() / frame.getHeight();
                //Location L = new Location(new Point2D.Double(x, y), 0, new String[1], new ArrayList<Edge>() {JButton b = new JButton("NODE");});
                JButton b = new JButton();
                b.setBounds(e.getX(), e.getY(), 10, 10);
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton() == 1){
                            JFrame buttonFrame = new JFrame();
                            buttonFrame.setSize(new Dimension(300, 100));
                            JLabel buttonLabel1 = new JLabel("Floor Number: ");
                            JLabel buttonLabel2 = new JLabel("Name List:");
                            JLabel buttonLabel3 = new JLabel("");
                            JFormattedTextField text1 = new JFormattedTextField("Enter an integer");
                            JFormattedTextField text2 = new JFormattedTextField("Enter a string");
                            JButton okButton = new JButton("OK");

                            text1.setColumns(10);
                            text2.setColumns(10);

                            buttonLabel1.setLabelFor(text1);
                            buttonLabel2.setLabelFor(text2);
                            buttonLabel3.setLabelFor(okButton);

                            JPanel labelPanel = new JPanel(new GridLayout(0,1));
                            labelPanel.add(buttonLabel1);
                            labelPanel.add(buttonLabel2);
                            labelPanel.add(buttonLabel3);

                            JPanel textPanel = new JPanel(new GridLayout(0,1));
                            textPanel.add(text1);
                            textPanel.add(text2);
                            textPanel.add(okButton);

                            JPanel panelLayout = new JPanel(new BorderLayout());
                            panelLayout.add(labelPanel, BorderLayout.CENTER);
                            panelLayout.add(textPanel, BorderLayout.LINE_END);

                            buttonFrame.add(panelLayout);
                            buttonFrame.setVisible(true);
                            panelLayout.repaint();
                        }
                        else if(e.getButton() == 3){
                            p.remove(b);
                            p.repaint();
                        }
                    }
                });
                p.add(b);
                b.setVisible(true);
                p.repaint();
            }
        });
        frame.add(p);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        devPanel d = new devPanel();

    }

}
