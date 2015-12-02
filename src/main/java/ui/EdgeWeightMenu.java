package ui;

import core.EdgeAttribute;
import core.EdgeAttributeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

/**
 * This class is an interface the modifies edge attribute weights.
 */
public class EdgeWeightMenu extends JPanel{
    EdgeAttributeManager attributeManager;

    /**
     * Constructor.
     *
     * @param attributeManager The EdgeAttributeManager that this menu will modify.
     */
    public EdgeWeightMenu(EdgeAttributeManager attributeManager) {
        this.attributeManager = attributeManager;
        setUpEdgeWeightMenu();
    }

    /**
     * Add all the controls to the frame.
     */
    public void setUpEdgeWeightMenu() {
        Hashtable<Integer, JLabel> sliderLabelTable = new Hashtable<>();
        sliderLabelTable.put(0, new JLabel("Avoid"));
        sliderLabelTable.put(1, new JLabel("Neutral"));
        sliderLabelTable.put(2, new JLabel("Prefer"));

        JSlider preferIndoors = new JSlider(JSlider.HORIZONTAL, 0, 2, 1);
        preferIndoors.setLabelTable(sliderLabelTable);
        preferIndoors.setPaintLabels(true);
        preferIndoors.setBorder(BorderFactory.createTitledBorder("Interior Paths"));
        preferIndoors.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                switch (source.getValue()) {
                    case 0:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, 10);
                        break;
                    case 1:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, 1);
                        break;
                    case 2:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, 0.01);
                        break;
                }
            }
        });
        setDefaultValue(EdgeAttribute.INDOORS, preferIndoors);

        JSlider stairs = new JSlider(JSlider.HORIZONTAL, 0, 2, 1);
        stairs.setLabelTable(sliderLabelTable);
        stairs.setPaintLabels(true);
        stairs.setBorder(BorderFactory.createTitledBorder("Stairs"));
        stairs.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                switch (source.getValue()) {
                    case 0:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, 3);
                        break;
                    case 1:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, 1);
                        break;
                    case 2:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, 0.1);
                        break;
                }
            }
        });
        setDefaultValue(EdgeAttribute.STAIRS, stairs);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(preferIndoors);
        panel.add(Box.createVerticalGlue());
        panel.add(stairs);
        panel.add(Box.createVerticalGlue());

        this.setLayout(new BorderLayout());
        this.add(panel);
        setMinimumSize(new Dimension(300, 200));
    }

    private void setDefaultValue(EdgeAttribute attribute, JSlider slider) {
        double currentStairsVal = attributeManager.getModifierFromAttribute(attribute);
        if (currentStairsVal == 1.5) {
            slider.setValue(0);
        } else if (currentStairsVal == 1) {
            slider.setValue(1);
        } else if (currentStairsVal == 0.5) {
            slider.setValue(2);
        }
    }
}
