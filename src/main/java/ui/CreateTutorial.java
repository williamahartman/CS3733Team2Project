package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by will on 12/15/15.
 */
public class CreateTutorial implements ActionListener{
    private int flag = 1;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame tutorialWindow = new JFrame("AZTEC WASH Mapper Tutorial");
        tutorialWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tutorialWindow.setMinimumSize(new Dimension(1200, 700));
        tutorialWindow.setPreferredSize(new Dimension(1200, 700));
        tutorialWindow.setVisible(true);
        tutorialWindow.setLayout(new BorderLayout());
        tutorialWindow.setLocationRelativeTo(null);

        JPanel tutorialSidePanel = new JPanel();
        JPanel imagePanel = new JPanel();

        imagePanel.setLayout(new FlowLayout());
        imagePanel.setPreferredSize(new Dimension(950, 700));
        imagePanel.setVisible(true);

        tutorialSidePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        tutorialSidePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        tutorialSidePanel.setPreferredSize(new Dimension(250, 700));
        tutorialSidePanel.setVisible(true);
        JLabel user = new JLabel("User");
        user.setPreferredSize(new Dimension(200, 50));
        user.setMaximumSize(new Dimension(200, 50));
        user.setFont(new Font("Courier", Font.BOLD, 30));

        JButton search = new JButton("Search");
        JButton selectedNodes = new JButton("Click Nodes");
        // JButton findRoute = new JButton("Find Route");
        JButton stepBystep = new JButton("Navigation");
        JButton newRoute = new JButton("New Route");
        JButton editButton = new JButton("Edit Menu");
        JButton viewButton = new JButton("View Menu");

        JButton previous = new JButton("Previous Topic");
        JButton next = new JButton("Next Topic");

        previous.setPreferredSize(new Dimension(200, 40));
        previous.setMaximumSize(new Dimension(200, 40));
        next.setPreferredSize(new Dimension(200, 40));
        next.setMaximumSize(new Dimension(200, 40));


        search.setPreferredSize(new Dimension(200, 40));
        selectedNodes.setPreferredSize(new Dimension(200, 40));
        stepBystep.setPreferredSize(new Dimension(200, 40));
        editButton.setPreferredSize(new Dimension(200, 40));
        viewButton.setPreferredSize(new Dimension(200, 40));
        //findRoute.setPreferredSize(new Dimension(200, 40));
        search.setMaximumSize(new Dimension(200, 40));
        selectedNodes.setMaximumSize(new Dimension(200, 40));
        stepBystep.setMaximumSize(new Dimension(200, 40));
        //findRoute.setMaximumSize(new Dimension(200, 40));
        editButton.setMaximumSize(new Dimension(200, 40));
        viewButton.setMaximumSize(new Dimension(200, 40));
        newRoute.setPreferredSize(new Dimension(200, 40));
        newRoute.setMaximumSize(new Dimension(200, 40));

        tutorialSidePanel.add(user);
        tutorialSidePanel.add(search);
        tutorialSidePanel.add(selectedNodes);
        //tutorialSidePanel.add(findRoute);
        tutorialSidePanel.add(stepBystep);
        tutorialSidePanel.add(newRoute);
        tutorialSidePanel.add(editButton);
        tutorialSidePanel.add(viewButton);

        tutorialWindow.add(tutorialSidePanel, BorderLayout.WEST);
        ImageIcon pic1 = new ImageIcon("src/main/resources/pic1.png");
        ImageIcon pic2 = new ImageIcon("src/main/resources/pic2.png");
        ImageIcon pic3 = new ImageIcon("src/main/resources/pic3.png");
        ImageIcon pic4 = new ImageIcon("src/main/resources/pic4.png");
        ImageIcon pic5 = new ImageIcon("src/main/resources/pic5.png");
        ImageIcon pic6 = new ImageIcon("src/main/resources/pic6.png");

        JLabel pic = new JLabel(pic1);

        pic.setMaximumSize(new Dimension(1000, 600));
        pic.setMinimumSize(new Dimension(1000, 600));
        pic.setPreferredSize(new Dimension(1000, 600));

        //tutorialFrame.add(pic1, BorderLayout.EAST);
        imagePanel.add(pic);
        imagePanel.add(previous);
        imagePanel.add(next);
        tutorialWindow.add(imagePanel, BorderLayout.EAST);
        tutorialWindow.setVisible(true);

        search.addActionListener(actionEvent -> {
            pic.setIcon(pic1);
            flag = 1;
        });
        selectedNodes.addActionListener(actionEvent -> {
            pic.setIcon(pic2);
            flag = 2;
        });
        stepBystep.addActionListener(actionEvent -> {
            pic.setIcon(pic3);
            flag = 3;
        });
        newRoute.addActionListener(actionEvent -> {
            pic.setIcon(pic4);
            flag = 4;
        });
        editButton.addActionListener(actionEvent -> {
            pic.setIcon(pic5);
            flag = 5;
        });
        viewButton.addActionListener(actionEvent -> {
            pic.setIcon(pic6);
            flag = 6;
        });


        next.addActionListener(actionEvent ->{
            if (flag == 1){
                pic.setIcon(pic2);
            } else if (flag == 2){
                pic.setIcon(pic3);
            } else if (flag == 3){
                pic.setIcon(pic4);
            } else if (flag == 4){
                pic.setIcon(pic5);
            } else if (flag == 5){
                pic.setIcon(pic6);
            } else if (flag == 6){
                pic.setIcon(pic1);
                flag = 0;
            }
            if (flag < 6){
                flag++;
            }
        });

        previous.addActionListener(actionEvent ->{
            if (flag == 6){
                pic.setIcon(pic5);
            } else if (flag == 5){
                pic.setIcon(pic4);
            } else if (flag == 4){
                pic.setIcon(pic3);
            } else if (flag == 3){
                pic.setIcon(pic2);
            } else if (flag == 2){
                pic.setIcon(pic1);
            } else if (flag == 1){
                pic.setIcon(pic6);
                flag = 7;
            }
            if (flag > 1){
                flag--;
            }
        });
    }
}
