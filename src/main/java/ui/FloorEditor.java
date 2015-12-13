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

    public static void editFloors(Window owner) {
        //Build the dialog box
        JDialog editFloorsWindow = new JDialog(owner, "Edit Route Preferences", Dialog.ModalityType.APPLICATION_MODAL);
        editFloorsWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        editFloorsWindow.setMinimumSize(new Dimension(640, 480));
        editFloorsWindow.setLocationRelativeTo(null);
        editFloorsWindow.getContentPane().setLayout(new BorderLayout());
        editFloorsWindow.setResizable(false);

        java.util.List<FloorEditPanel> floorPanels = new ArrayList<>();
        List<Integer> floorNums = new ArrayList<>();

        JPanel mapListPanel = new JPanel();
        mapListPanel.setLayout(new BoxLayout(mapListPanel, BoxLayout.Y_AXIS));
        JButton saveToDatabase = new JButton("Save to Database");
        JButton addFloor = new JButton("Add a Floor");
        addFloor.addActionListener(e -> {
            FloorEditPanel floorEditPanel = new FloorEditPanel(floorPanels.size(),
                    "Add an image URL...", "Add an X Scale...", "Add an X Scale...");
            floorPanels.add(floorEditPanel);
            mapListPanel.add(floorEditPanel);
            mapListPanel.revalidate();
            mapListPanel.repaint();
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> editFloorsWindow.dispose());

        //Stuff that uses the DB
        try {
            Database database = new Database();
            HashMap<Integer, MapImage> mapImages = database.getMaps();

            for (int i: mapImages.keySet()) {
                floorNums.add(i);
                FloorEditPanel floorEditPanel = new FloorEditPanel(i, mapImages.get(i));
                floorPanels.add(floorEditPanel);
                mapListPanel.add(floorEditPanel);
            }

            saveToDatabase.addActionListener(e -> {
                for (int i: floorNums) {
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

        editFloorsWindow.setContentPane(mainPanel);
        editFloorsWindow.setVisible(true);
    }
}
