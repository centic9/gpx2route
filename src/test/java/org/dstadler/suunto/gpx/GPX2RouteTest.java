package org.dstadler.suunto.gpx;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

class GPX2RouteTest {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	public void testWriteRouteFile() throws IOException {
		final GPX gpx = GPX.builder()
				.addTrack(track -> track
						.addSegment(segment -> segment
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
				.build();

		File tempFile = File.createTempFile("GPX2Route", ".json");
		try {
			assertTrue(tempFile.delete());

			GPX2Route.writeRouteFile("test", "123", gpx, tempFile, 1234L);

			assertTrue(tempFile.exists());

			// can read back in the JSON
			JsonNode jsonNode = MAPPER.readTree(tempFile);
			assertNotNull(jsonNode);
		} finally {
			assertTrue(!tempFile.exists() || tempFile.delete());
		}
	}

	@Test
	public void testWritePointsFile() throws IOException {
		final GPX gpx = GPX.builder()
				.addTrack(track -> track
						.addSegment(segment -> segment
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
								.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
				.build();

		File tempFile = File.createTempFile("GPX2RoutePoints", ".json");
		try {
			assertTrue(tempFile.delete());

			GPX2Route.wrtiePointsFile(gpx, tempFile);

			assertTrue(tempFile.exists());

			// can read back in the JSON
			JsonNode jsonNode = MAPPER.readTree(tempFile);
			assertNotNull(jsonNode);
		} finally {
			assertTrue(!tempFile.exists() || tempFile.delete());
		}
	}

	@Test
	public void testReadGPX() throws IOException {
		GPX gpx = GPX.read("src/test/resources/t233338634_Schneeschuwandern_Ploeckenstein-.gpx");
		assertNotNull(gpx);

		long count = gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.count();

		assertEquals(766, count);
	}
}