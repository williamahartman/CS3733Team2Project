import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * This is a little example of a test case. Real test cases should be less
 * stupid and more organized.
 */
public class TestPlaceholder {

    /**
     * This is a test that will pass.
     */
    @Test
    public void testCase1() {
        String teamName = AppLauncher.getTeamName();
        assertEquals("AZTEC WASH!", teamName);
    }

//    //If you uncomment this, you can see what happens when a test fails
//
//    /**
//     * This is a test that will fail.
//     */
//    @Test
//    public void testCase2() {
//        String teamName = AppLauncher.getTeamName();
//        assertEquals("some other boring team", teamName);
//    }
}
