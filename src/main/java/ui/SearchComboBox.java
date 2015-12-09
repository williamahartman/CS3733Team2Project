package ui;

import core.EdgeAttributeManager;
import core.Location;
import core.LocationGraph;
import dev.DevPassword;
import dev.DevTools;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by hollyn on 12/7/15.
 */
public class SearchComboBox extends JComboBox {
    LocationGraph graph;
    HashSet<Location> nameLoc;

    public SearchComboBox(HashSet<Location> nameLoc) {
        super();
        //this.graph = graph;
        this.nameLoc = nameLoc;
        setEditable(true);
        Component c = this.getEditor().getEditorComponent(); // Get editable component
        JTextComponent tc = (JTextComponent) c;
        this.removeAllItems();
        this.addItem("");
        this.setPopupVisible(false);
        //List<Location> locationList = graph.getAllLocations();
        ArrayList<String> nameList = new ArrayList<>();

        List<Location> nameLocs = new ArrayList<Location>(nameLoc);

        for (Location loc: nameLoc){
            for (String name: loc.getNameList()){
                nameList.add(name);
            }
        }
        Collections.sort(nameList);
        for (String name: nameList){
            this.addItem(name);
        }

        /*for (Location loc: nameLoc){
            for (String name: loc.getNameList()){
                nameList.add(name);
            }
        }
        Collections.sort(nameList);
        for (String name: nameList){
            this.addItem(name);
        }*/

        tc.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent d) {
                //System.out.println("change update\n");
            }
            @Override
            public void insertUpdate(DocumentEvent d) {
                //System.out.println("insert update\n");
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent d) {
                //System.out.println("remove update\n");
                update();
            }

            public void update () {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("Called update\n");

                        // List of all names that match user's text input
                        List<String> founds = new ArrayList<String>();

                        // Hash set that also tracks names
                        Set<String> foundSet = new HashSet<String>();

                        //System.out.println("1) get text/" + tc.getText() + "/end");

                        // List of locations that have at least one name matching
                        //List<Location> loc = graph.searchLocationByName(tc.getText());

                        List<Location> loc = searchNamesInList(nameLoc, tc.getText());

                        for (Location l : loc) {
                            for (String name : l.getNameList()) {
                                if (name.toLowerCase().contains(tc.getText().toLowerCase())) {
                                    //System.out.println("1) add name to list/" + name + "/end");
                                    // Populate "founds" list with every name that has
                                    founds.add(name);
                                }
                            }
                        }
                        for (String s : founds) {
                            //System.out.println("2) add name to set/" + s + "/end");
                            foundSet.add(s.toLowerCase()); // Add to hash set
                        }
                        Collections.sort(founds); // Alphabetical list that gets added as items in dropdown

                        setEditable(false); // Don't edit while list changes
                        removeAllItems(); // Remove items in combo box


                        if (!foundSet.contains(tc.getText().toLowerCase())) { // Faster
                            // If the text was not in the hash set, add to combo box dropdown
                            //System.out.println("1) add item, get text/" + tc.getText() + "/end");
                            addItem(tc.getText()); // Adds string that was typed in
                        }

                        // Add names in alphabetical order
                        for (String s : founds) { // Add to dropdown when not editable
                            //System.out.println("2) add item, get text/" + s + "/end");
                            addItem(s);
                        }

                        setEditable(true);
                        if (founds.size() == 1 && founds.get(0).equals(tc.getText())){
                            setPopupVisible(false);
                        } else {
                            //System.out.println(founds.size());
                            setPopupVisible(true);
                            tc.requestFocus();
                        }
                    }
                });
            }
        });

        tc.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent f) {
                if (tc.getText().length() > 0) {
                    setPopupVisible(true);
                }
            }
            @Override
            public void focusLost(FocusEvent f) {
            }
        });

    }

    public List<Location> searchNamesInList(HashSet<Location> locationList, String searchString) {
        ArrayList<Location> result = new ArrayList<>();

        locationList.stream().filter(loc -> loc.namesInclude(searchString)).forEach(result::add);

        return result;
    }

   /* public ItemListener checkItem() {
        return new ItemListener() {
            public void itemStateChanged (ItemEvent i) {
                JComboBox jcb = (JComboBox) i.getSource();
                String selectedName = (String) jcb.getSelectedItem();

                jcb.removeAllItems();
                jcb.addItem(selectedName);
            }
        };
    }*/
}
