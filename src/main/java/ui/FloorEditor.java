package ui;

import core.MapImage;
import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * This class builds a Dialog box that can edit floors and add them to the database.
 */
public class FloorEditor {
    List<FloorEditPanel> floorPanels;
    JPanel mapListPanel;
    HashMap<Integer, MapImage> mapImages;
    JSpinner defaultFloorSpinner;

    /**
     * Constructor.
     *
     * @param mapImages the HashSet of map images
     * @param defaultFloorNum the default floor number
     */
    public FloorEditor(HashMap<Integer, MapImage> mapImages, int defaultFloorNum) {
        this.mapImages = mapImages;
        floorPanels = new ArrayList<>();
        mapListPanel = new JPanel();
        defaultFloorSpinner = new JSpinner(new SpinnerNumberModel(defaultFloorNum, 0, mapImages.size() - 1, 1));
        defaultFloorSpinner.setMaximumSize(new Dimension(60, 30));
    }

    private void addFloorEditPanel(FloorEditPanel panel) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            mapListPanel.remove(panel);
            floorPanels.remove(panel);

            ((SpinnerNumberModel) defaultFloorSpinner.getModel()).setMaximum(floorPanels.size() - 1);

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
        ((SpinnerNumberModel) defaultFloorSpinner.getModel()).setMaximum(floorPanels.size() - 1);

        mapListPanel.revalidate();
        mapListPanel.repaint();
    }

    /**
     * Show the dialog box.
     *
     * @param owner the window owner of the box
     * @param toUpdate The mapview that will be updated on db save.
     */
    public void showDialog(Window owner, MapView toUpdate) {
        //Build the dialog box
        JDialog editFloorsWindow = new JDialog(owner, "Edit Route Preferences", Dialog.ModalityType.APPLICATION_MODAL);
        editFloorsWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Make panel, add initial values
        mapListPanel.setLayout(new BoxLayout(mapListPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < mapImages.keySet().size(); i++) {
            addFloorEditPanel(new FloorEditPanel(i, mapImages.get(i)));
        }

        //Declare buttons, listenters
        JButton addFloor = new JButton("Add a Floor");
        addFloor.addActionListener(e -> {
            FloorEditPanel floorEditPanel = new FloorEditPanel(floorPanels.size(),
                    "Add an image URL...", "", "");
            addFloorEditPanel(floorEditPanel);
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> editFloorsWindow.dispose());

        JButton saveToDatabase = new JButton("Save to Database");
        saveToDatabase.addActionListener(e -> {
            try {
                Database database = new Database();
                for (int i: mapImages.keySet()) {
                    database.removeMap(i, null);
                }
                mapImages.clear();

                for (int i = 0; i < floorPanels.size(); i++) {
                    mapImages.put(i, floorPanels.get(i).buildMapImage());
                    floorPanels.get(i).addMapToDatabase(database, i);
                }
                database.setDefaultFloor((Integer) defaultFloorSpinner.getValue());

                toUpdate.setSvgList(mapImages);
                toUpdate.setDefaultFloor((Integer) defaultFloorSpinner.getValue());
                toUpdate.setFloor(toUpdate.getDefaultFloorNumber());
                toUpdate.repaint();

                editFloorsWindow.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the online database (be on the internet!)",
                        "Database error!",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        //Declare and setup additional components
        JLabel spinnerLabel = new JLabel("Default Floor:   ");
        JScrollPane scrollPane = new JScrollPane(mapListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Adding to panels, JDialog
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(spinnerLabel);
        buttonPanel.add(defaultFloorSpinner);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(addFloor);
        buttonPanel.add(saveToDatabase);
        buttonPanel.add(cancel);
        buttonPanel.add(Box.createHorizontalGlue());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //Set sizes
        editFloorsWindow.setMinimumSize(new Dimension(
                mapListPanel.getPreferredSize().width + 50,
                480));
        editFloorsWindow.setLocationRelativeTo(null);
        editFloorsWindow.setContentPane(mainPanel);
        editFloorsWindow.setVisible(true);
    }
}
