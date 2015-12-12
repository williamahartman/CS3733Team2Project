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


/**
 * Created by ziyanding on 12/11/15.
 */
public class SearchBoxModel extends AbstractListModel
        implements ComboBoxModel, KeyListener, ItemListener {
    List<String> nameInList;
    List<String> allNames;
    String nameSelected;
    JComboBox searchBox;
    ComboBoxEditor searchText;
    int currPos;

    public SearchBoxModel(JComboBox searchBox, HashSet<Location> locations){
        this.nameInList = new ArrayList<>();
        this.allNames = new ArrayList<>();
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
        nameInList.clear();
        allNames.stream().filter(name->name.toLowerCase().startsWith(str.toLowerCase())).forEach(nameInList::add);
        Collections.sort(nameInList);
        super.fireContentsChanged(this, 0, nameInList.size());
       // searchBox.hidePopup();
        if (str.length() > 0) {
            searchBox.showPopup();
        } else {
            searchBox.setSelectedIndex(-1);
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

