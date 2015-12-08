package ui;

import core.EdgeAttribute;
import core.EdgeAttributeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * This class is an interface the modifies edge attribute weights.
 */
public class EdgeWeightMenu extends JPanel{
    private static final double AVOID_WEIGHT = 10;
    private static final double NEUTRAL_WEIGHT = 1;
    private static final double PREFER_WEIGHT = 0.01;

    private EdgeAttributeManager attributeManager;

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
        sliderLabelTable.put(0, new JLabel("Try to Avoid"));
        sliderLabelTable.put(1, new JLabel("Neutral"));
        sliderLabelTable.put(2, new JLabel("Prefer"));

        JSlider preferIndoors = new JSlider(JSlider.HORIZONTAL, 0, 2, 1);
        preferIndoors.setMaximumSize(new Dimension(500, 70));
        preferIndoors.setLabelTable(sliderLabelTable);
        preferIndoors.setPaintLabels(true);
        preferIndoors.setBorder(BorderFactory.createTitledBorder("Interior Paths"));
        preferIndoors.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                switch (source.getValue()) {
                    case 0:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, AVOID_WEIGHT);
                        break;
                    case 1:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, NEUTRAL_WEIGHT);
                        break;
                    case 2:
                        attributeManager.addModifierForAttribute(EdgeAttribute.INDOORS, PREFER_WEIGHT);
                        break;
                }
            }
        });
        setDefaultValue(EdgeAttribute.INDOORS, preferIndoors);

        JSlider preferElevators = new JSlider(JSlider.HORIZONTAL, 0, 2, 1);
        preferElevators.setMaximumSize(new Dimension(500, 70));
        preferElevators.setLabelTable(sliderLabelTable);
        preferElevators.setPaintLabels(true);
        preferElevators.setBorder(BorderFactory.createTitledBorder("Elevators"));
        preferElevators.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                switch (source.getValue()) {
                    case 0:
                        attributeManager.addModifierForAttribute(EdgeAttribute.ELEVATOR, AVOID_WEIGHT);
                        break;
                    case 1:
                        attributeManager.addModifierForAttribute(EdgeAttribute.ELEVATOR, NEUTRAL_WEIGHT);
                        break;
                    case 2:
                        attributeManager.addModifierForAttribute(EdgeAttribute.ELEVATOR, PREFER_WEIGHT);
                        break;
                }
            }
        });
        setDefaultValue(EdgeAttribute.ELEVATOR, preferElevators);

        JSlider stairs = new JSlider(JSlider.HORIZONTAL, 0, 2, 1);
        stairs.setMaximumSize(new Dimension(500, 70));
        stairs.setLabelTable(sliderLabelTable);
        stairs.setPaintLabels(true);
        stairs.setBorder(BorderFactory.createTitledBorder("Stairs"));
        stairs.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                switch (source.getValue()) {
                    case 0:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, AVOID_WEIGHT);
                        break;
                    case 1:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, NEUTRAL_WEIGHT);
                        break;
                    case 2:
                        attributeManager.addModifierForAttribute(EdgeAttribute.STAIRS, PREFER_WEIGHT);
                        break;
                }
            }
        });
        setDefaultValue(EdgeAttribute.STAIRS, stairs);

        add(Box.createVerticalStrut(50));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(preferIndoors);
        add(Box.createVerticalStrut(50));
        add(stairs);
        add(Box.createVerticalStrut(50));
        add(preferElevators);
        add(Box.createVerticalStrut(50));
    }

    private void setDefaultValue(EdgeAttribute attribute, JSlider slider) {
        double currentStairsVal = attributeManager.getModifierFromAttribute(attribute);
        if (currentStairsVal == AVOID_WEIGHT) {
            slider.setValue(0);
        } else if (currentStairsVal == NEUTRAL_WEIGHT) {
            slider.setValue(1);
        } else if (currentStairsVal == PREFER_WEIGHT) {
            slider.setValue(2);
        }
    }
}
