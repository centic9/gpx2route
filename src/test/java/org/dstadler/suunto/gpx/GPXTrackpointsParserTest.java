package org.dstadler.suunto.gpx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.SortedMap;

import org.junit.jupiter.api.Test;

public class GPXTrackpointsParserTest {
    private static final String GPX_XML =
            "<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.cluetrust.com/XML/GPXDATA/1/0 http://www.cluetrust.com/Schemas/gpxdata10.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\" xmlns:gpxdata=\"http://www.topografix.com/GPX/1/0\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" version=\"1.1\" creator=\"Movescount - http://www.movescount.com\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n" +
            "  <trk>\n" +
            "    <name>Move</name>\n" +
            "    <trkseg>\n" +
            "      <trkpt lat=\"48.456194\" lon=\"13.99866\">\n" +
            "        <ele>512</ele>\n" +
            "        <time>2014-02-27T10:42:59.420Z</time>\n" +
            "        <extensions>\n" +
            "          <gpxdata:speed>0.413765975271989</gpxdata:speed>\n" +
            "        </extensions>\n" +
            "      </trkpt>\n" +
            "    </trkseg>\n" +
            "  </trk>\n" +
            "</gpx>\n";

    private static final String GPX_XML_NO_TIME =
            "<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.cluetrust.com/XML/GPXDATA/1/0 http://www.cluetrust.com/Schemas/gpxdata10.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\" xmlns:gpxdata=\"http://www.topografix.com/GPX/1/0\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" version=\"1.1\" creator=\"Movescount - http://www.movescount.com\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n" +
            "  <trk>\n" +
            "    <name>Move</name>\n" +
            "    <trkseg>\n" +
            "      <trkpt lat=\"48.456194\" lon=\"13.99866\">\n" +
            "        <ele>512</ele>\n" +
            "        <extensions>\n" +
            "          <gpxdata:speed>0.413765975271989</gpxdata:speed>\n" +
            "        </extensions>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"48.556194\" lon=\"13.89866\">\n" +
            "        <ele>511</ele>\n" +
            "        <extensions>\n" +
            "          <gpxdata:speed>0.412765975271989</gpxdata:speed>\n" +
            "        </extensions>\n" +
            "      </trkpt>\n" +
            "    </trkseg>\n" +
            "  </trk>\n" +
            "</gpx>\n";

    @Test
    public void parse() throws Exception {
        GPXTrackpointsParser parser = new GPXTrackpointsParser();
        SortedMap<Long, TrackPoint> map =
                parser.parseContent(new ByteArrayInputStream(GPX_XML.getBytes(StandardCharsets.UTF_8)));

        assertNotNull(map);
        assertEquals(1, map.size(), "Had: " + map);
		TrackPoint point = map.get(map.firstKey());
		assertEquals(48.456194, point.getLatitude(), 0.000001);
        assertEquals(13.99866, point.getLongitude(), 0.000001);
        assertEquals(512, point.getElevation(), 0.000001);
    }

    @Test
    public void parseFile() throws Exception {
        SortedMap<Long, TrackPoint> map =
                GPXTrackpointsParser.parseContent(new File("src/test/resources/t233338634_Schneeschuwandern_Ploeckenstein-.gpx"));

        assertNotNull(map);
        assertEquals(766, map.size(), "Had: " + map);
		TrackPoint point = map.get(map.firstKey());
		assertEquals(48.757141, point.getLatitude(), 0.000001);
        assertEquals(13.82778, point.getLongitude(), 0.000001);
        assertEquals(945.95544, point.getElevation(), 0.000001);

		TrackPoint lastPoint = map.get(map.lastKey());
		assertEquals(48.757215, lastPoint.getLatitude(), 0.000001);
        assertEquals(13.827791, lastPoint.getLongitude(), 0.000001);
        assertEquals(942.21204, lastPoint.getElevation(), 0.000001);
    }

	@Test
	public void parseNoTime() throws Exception {
		GPXTrackpointsParser parser = new GPXTrackpointsParser();
		SortedMap<Long, TrackPoint> map =
				parser.parseContent(new ByteArrayInputStream(GPX_XML_NO_TIME.getBytes(StandardCharsets.UTF_8)));

		assertNotNull(map);
		assertEquals(2, map.size(),
				"Had: " + map);

		Iterator<TrackPoint> it = map.values().iterator();

		TrackPoint point = it.next();
		assertEquals(48.456194, point.getLatitude(), 0.000001);
		assertEquals(13.99866, point.getLongitude(), 0.000001);
		assertEquals(512, point.getElevation(), 0.000001);

		point = it.next();
		assertEquals(48.556194, point.getLatitude(), 0.000001);
		assertEquals(13.89866, point.getLongitude(), 0.000001);
		assertEquals(511, point.getElevation(), 0.000001);
	}
}
