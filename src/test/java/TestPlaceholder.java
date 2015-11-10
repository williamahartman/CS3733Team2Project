import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestPlaceholder {

    //This test will pass
    @Test
    public void testCase1() {
        String teamName = AppLauncher.getTeamName();
        assertEquals("AZTEC WASH!", teamName);
    }

    //If you uncomment this, you can see what happens when a test fails

    /*
    @Test
    public void testCase2() {
        String teamName = AppLauncher.getTeamName();
        assertEquals("some other boring team", teamName);
    }
    */
}