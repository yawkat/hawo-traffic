package at.yawk.hawo.traffic;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author yawkat
 */
public class HawoTrafficTest {
    @Test
    public void testParseSize() throws Exception {
        assertEquals(HawoTraffic.parseSize("5.0 MB"), 5 << 20);
    }
}