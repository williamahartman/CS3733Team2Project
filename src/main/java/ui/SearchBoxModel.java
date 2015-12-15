package ui;

import core.Location;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by ziyanding on 12/11/15.
 */
public class SearchBoxModel extends AbstractListModel
        implements ComboBoxModel, KeyListener, ItemListener {
    private List<String> nameInList;
    private List<String> allNames;
    private String nameSelected;
    private JComboBox searchBox;
    private ComboBoxEditor searchText;
    private int currPos;
    private MapView mapView;
    private List<Location> locations;

    public SearchBoxModel(JComboBox searchBox, HashSet<Location> locations, MapView mapView){
        this.nameInList = new ArrayList<>();
        this.allNames = new ArrayList<>();
        this.mapView = mapView;
        this.locations = new ArrayList<>(locations);

        for (Location l: locations){
            for (String name: l.getNameList()){
                allNames.add(name);
            }
        }
        this.searchBox = searchBox;
        this.searchText = searchBox.getEditor();
        searchText.getEditorComponent().addKeyListener(this);
        currPos = 0;
    }

    public void updateModel(String str){
        mapView.clearSearchList();

        nameInList.clear();
        nameInList = allNames.stream()
                .filter(name->name.toLowerCase().contains(str.toLowerCase()))
                .collect(Collectors.toList());
        Collections.sort(nameInList);
        super.fireContentsChanged(this, 0, nameInList.size());
        //searchBox.hidePopup();
        if (str.length() > 0) {
            searchBox.showPopup();
        } else {
            searchBox.setSelectedIndex(-1);
        }

        //Change the floor if all points are on one floor
        List<Location> searchResultLocs = locations.stream()
                .filter(loc -> loc.namesInclude(str))
                .collect(Collectors.toList());
        if (!searchResultLocs.isEmpty()) {
            int floor = searchResultLocs.get(0).getFloorNumber();
            boolean floorHasChanged = false;
            for (Location loc : searchResultLocs) {
                if (loc.namesInclude(str) && loc.getFloorNumber() != floor) {
                    floorHasChanged = true;
                    break;
                }
            }

            if (!floorHasChanged) {
                mapView.setFloor(floor);
            }
        }

        if(str.length() > 1) {
            mapView.addToSearchList(searchResultLocs);
            mapView.repaint();
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        nameSelected = (String) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return nameSelected;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        searchText.setItem(e.getItem().toString());
        searchBox.setSelectedItem(e.getItem());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        String name = searchText.getItem().toString();
        JTextField textField = (JTextField) searchText.getEditorComponent();
        currPos = textField.getCaretPosition();

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            searchBox.setSelectedIndex(searchBox.getSelectedIndex());
        } else if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            searchText.setItem(name);
            textField.setCaretPosition(currPos);

        } else {
            updateModel(searchBox.getEditor().getItem().toString());
            searchText.setItem(name);
            textField.setCaretPosition(currPos);
        }

    }

    @Override
    public int getSize() {
        return nameInList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return nameInList.get(index);
    }

}

