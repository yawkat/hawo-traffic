package at.yawk.hawo.traffic;

import java.time.LocalDate;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author yawkat
 */
public class BillingWeekTest {
    @Test
    public void testFromDate() throws Exception {
        assertEquals(BillingWeek.fromDate(LocalDate.of(2015, 1, 1)), new BillingWeek(2014, 52));
        assertEquals(BillingWeek.fromDate(LocalDate.of(2015, 9, 24)), new BillingWeek(2015, 38));
        assertEquals(BillingWeek.fromDate(LocalDate.of(2015, 9, 27)), new BillingWeek(2015, 39));
    }
}