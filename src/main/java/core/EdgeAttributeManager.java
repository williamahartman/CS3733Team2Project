package core;

import java.util.HashMap;
import java.util.Map;

/**
 * This class associates EdgeAttribute with float values. These values can be used
 * in the Edge class when calculating costs.
 *
 * Associating through this class allows the user to modify associated cost values at runtime,
 * without traversing or rebuilding the graph.
 */
public class EdgeAttributeManager {
    private Map<EdgeAttribute, Float> attributeModifierValues;

    /**
     * Default constructor.
     */
    public EdgeAttributeManager() {
        attributeModifierValues = new HashMap<EdgeAttribute, Float>();
    }

    /**
     * Get the modifier value associated with the passed EdgeAttribute, or return 1 if none
     * are associated.
     *
     * @param edgeAttribute The EdgeAttribute whose modifier we are looking for
     * @return The associated modifier, or 1
     */
    public double getModifierFromAttribute(EdgeAttribute edgeAttribute) {
        if (attributeModifierValues.containsKey(edgeAttribute)) {
            return attributeModifierValues.get(edgeAttribute);
        }

        return 1;
    }

    /**
     * Associate a new value with an EdgeAttribute.
     *
     * @param edgeAttribute The EdgeAttribute the value will be associated with
     * @param modifier The value for the modifier
     */
    public void addModifierForAttribute(EdgeAttribute edgeAttribute, float modifier) {
        attributeModifierValues.put(edgeAttribute, modifier);
    }
}
