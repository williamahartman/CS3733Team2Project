package testcore;

import core.EdgeAttribute;
import core.EdgeAttributeManager;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * This class holds test cases for EdgeAttributeManager. Make one test method per actual method.
 */
public class EdgeAttributeManagerTest {
    @Test
    public void getModifierFromAttribute() {
        EdgeAttributeManager testManager = new EdgeAttributeManager();

        //Test default
        assertEquals("Test default response", 1,
                testManager.getModifierFromAttribute(EdgeAttribute.INDOORS),
                0);

        //Add a value and test
        testManager.addModifierForAttribute(EdgeAttribute.INDOORS, 0.5);

        assertEquals("Test default response with values", 1,
                testManager.getModifierFromAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE),
                0);
        assertEquals("Test response with held value", 0.5,
                testManager.getModifierFromAttribute(EdgeAttribute.INDOORS),
                0);

        //Update value and test
        testManager.addModifierForAttribute(EdgeAttribute.INDOORS, 0.7);

        assertEquals("Test default response with values", 1,
                testManager.getModifierFromAttribute(EdgeAttribute.NOT_HANDICAP_ACCESSIBLE),
                0);
        assertEquals("Test response with held value", 0.7,
                testManager.getModifierFromAttribute(EdgeAttribute.INDOORS),
                0);
    }
}
