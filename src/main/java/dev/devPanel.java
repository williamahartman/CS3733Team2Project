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
        JLabel label = new JLabel("TEST");
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
                Location L = new Location(new Point2D.Double(x, y), 0, new String[1], new ArrayList<Edge>() {JButton b = new JButton("NODE");
                });
                JButton b = new JButton("Node");
                b.setBounds(e.getX(), e.getY(), 10, 10);
                //b.setAlignmentX(x);
               // b.setAlignmentY(y);
                //b.setSize(new Dimension(10,10));
                p.add(b);
                p.setVisible(true);
                frame.setVisible(true);
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
