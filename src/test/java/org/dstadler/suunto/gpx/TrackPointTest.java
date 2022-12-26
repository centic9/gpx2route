package org.dstadler.suunto.gpx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.dstadler.commons.testing.TestHelpers;
import org.junit.jupiter.api.Test;

public class TrackPointTest {
    @Test
    void test() {
        TrackPoint point = new TrackPoint();
        TestHelpers.ToStringTest(point);

        point.setLatitude(1.1);
        point.setLongitude(1.3);
        point.setElevation(523);
        point.setTime(new Date(123456789L));
        TestHelpers.ToStringTest(point);

		assertEquals(1.1, point.getLatitude());
		assertEquals(1.3, point.getLongitude());
    }
}