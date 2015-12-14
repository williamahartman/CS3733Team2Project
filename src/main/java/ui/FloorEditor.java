package ui;

import core.MapImage;
import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by Will on 12/13/2015.
 */
public class FloorEditor {
    java.util.List<FloorEditPanel> floorPanels;
    JPanel mapListPanel;

    public FloorEditor() {
        floorPanels = new ArrayList<>();
        mapListPanel = new JPanel();
    }

    private void addFloorEditPanel(FloorEditPanel panel) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            mapListPanel.remove(panel);
            floorPanels.remove(panel);

            mapListPanel.revalidate();
            mapListPanel.repaint();
        });
        panel.add(deleteButton);
        panel.add(Box.createHorizontalStrut(10));

        Dimension d = panel.getPreferredSize();
        d.height = 60;
        panel.setMinimumSize(d);
        panel.setPreferredSize(d);
        panel.setMaximumSize(d);

        floorPanels.add(panel);
        mapListPanel.add(panel);

        mapListPanel.revalidate();
        mapListPanel.repaint();
    }

    public void showDialog(Window owner) {
        //Build the dialog box
        JDialog editFloorsWindow = new JDialog(owner, "Edit Route Preferences", Dialog.ModalityType.APPLICATION_MODAL);
        editFloorsWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mapListPanel.setLayout(new BoxLayout(mapListPanel, BoxLayout.Y_AXIS));
        JButton saveToDatabase = new JButton("Save to Database");
        JButton addFloor = new JButton("Add a Floor");
        addFloor.addActionListener(e -> {
            FloorEditPanel floorEditPanel = new FloorEditPanel(floorPanels.size(),
                    "Add an image URL...", "", "");
            addFloorEditPanel(floorEditPanel);
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> editFloorsWindow.dispose());

        //Stuff that uses the DB
        try {
            Database database = new Database();
            HashMap<Integer, MapImage> mapImages = database.getMaps();
            for (int i = 0; i < mapImages.keySet().size(); i++) {
                addFloorEditPanel(new FloorEditPanel(i, mapImages.get(i)));
            }

            saveToDatabase.addActionListener(e -> {
                for (int i: mapImages.keySet()) {
                    database.removeMap(i, null);
                }

                for (int i = 0; i < floorPanels.size(); i++) {
                    floorPanels.get(i).addMapToDatabase(database, i);
                }

                editFloorsWindow.dispose();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Adding to panels, JDialog
        JScrollPane scrollPane = new JScrollPane(mapListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addFloor);
        buttonPanel.add(saveToDatabase);
        buttonPanel.add(cancel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        editFloorsWindow.setMinimumSize(new Dimension(
                mapListPanel.getPreferredSize().width + 50,
                480));
        editFloorsWindow.setLocationRelativeTo(null);
        editFloorsWindow.setContentPane(mainPanel);
        editFloorsWindow.setVisible(true);
    }
}
