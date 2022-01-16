package org.dstadler.gpx;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class GPX2Route {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	// pretty-print the JSON files
	private static final ObjectWriter WRITER = MAPPER.writer(new DefaultPrettyPrinter());

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.err.println("Usage: gpx2route <name> <id> <gpx-file>");
			System.exit(1);
		}

		String name = args[0];
		String id = args[1];
		String file = args[2];

		// parse input GPX file
		GPX gpx = GPX.read(file);

		gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.forEach(System.out::println);

		File route = new File("routes_" + id + "_" + name + ".json");
		File points = new File("routes_" + id + "_points_" + name + "_points.json");

		writeRouteFile(name, id, gpx, route);
		wrtiePointsFile(gpx, points);
	}

	protected static void writeRouteFile(String name, String id, GPX gpx, File routeFile) throws IOException {
		System.out.println("Writing route-file " + routeFile + " for GPX: " +
				(gpx.getMetadata().isPresent() ? gpx.getMetadata().get().getName() : "unknown"));

		Optional<WayPoint> firstPoint = gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.findFirst();

		double longitude = 0;
		double latitude = 0;
		if (firstPoint.isPresent()) {
			longitude = firstPoint.get().getLongitude().doubleValue();
			latitude = firstPoint.get().getLatitude().doubleValue();
		}

		/*
		 * {
		 *     "ActivityID": 5,
		 *     "AscentAltitude": 782.7,
		 *     "CreatedBy": 128009,
		 *     "DescentAltitude": 733.7,
		 *     "Description": null,
		 *     "Distance": 21760,
		 *     "LastModifiedDate": "2013-07-11T13:17:45.6",
		 *     "Name": "GIS Dornach",
		 *     "Points": null,
		 *     "RouteID": 310212,
		 *     "RoutePointsCount": null,
		 *     "RoutePointsURI": "routes/310212/points",
		 *     "SelfURI": "routes/310212",
		 *     "StartLatitude": 48.333343,
		 *     "StartLongitude": 14.307307,
		 *     "Thumbs": 0,
		 *     "TimesUsed": 0,
		 *     "UsersCount": 0,
		 *     "WaypointCount": 0
		 * }
		 */
		ObjectNode routeNode = JsonNodeFactory.instance.objectNode();
		routeNode
				//.put("ActivityID", "1")
				.put("Name", name)
				.putNull("Points")
				.put("RouteID", Long.parseLong(id))
				.putNull("RoutePointsCount")
				.put("RoutePointsURI", "routes/" + id + "/points")
				.put("SelfURI", "routes/" + id)
				.put("StartLatitude", latitude)
				.put("StartLongitude", longitude)
				.put("Thumbs", 0)
				.put("TimesUsed", 0)
				.put("UsersCount", 0)
				.put("WaypointCount", 0)
		;

		WRITER.writeValue(routeFile, routeNode);
	}

	protected static void wrtiePointsFile(GPX gpx, File pointsFile) throws IOException {
		System.out.println("Writing points-file " + pointsFile + " for GPX: " +
				(gpx.getMetadata().isPresent() ? gpx.getMetadata().get().getName() : "unknown"));

		/*
		{
    "CompressedRoutePoints": null,
    "Points": null,
    "RoutePoints": [
        {
            "Altitude": 233.3,
            "Latitude": 48.333343,
            "Longitude": 14.307307,
            "Name": null,
            "RelativeDistance": 0,
            "Type": null
        },
        {
            "Altitude": 238.5,
            "Latitude": 48.333984,
            "Longitude": 14.306092,
            "Name": null,
		 */
		ArrayNode pointsArray = JsonNodeFactory.instance.arrayNode();
		gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.forEach(wayPoint -> {
					ObjectNode wayPointNode = JsonNodeFactory.instance.objectNode();
					wayPointNode
							.put("Altitude", wayPoint.getElevation().orElse(Length.of(0, Length.Unit.METER)).doubleValue())
							.put("Latitude", wayPoint.getLatitude().doubleValue())
							.put("Longitude", wayPoint.getLongitude().doubleValue())
					;

					pointsArray.add(wayPointNode);
				});


		ObjectNode pointsNode = JsonNodeFactory.instance.objectNode();
		pointsNode
				.putNull("CompressedRoutePoints")
				.putNull("Points")
				.set("RoutePoints", pointsArray)
		;

		WRITER.writeValue(pointsFile, pointsNode);
	}
}
