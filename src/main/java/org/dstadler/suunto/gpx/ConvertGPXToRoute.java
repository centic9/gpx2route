package org.dstadler.suunto.gpx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.xml.sax.SAXException;

/**
 * Small tool for reading a GPX file and converting it into the
 * json-format that OpenAmbit expects for updating the list of
 * routes that are stored on watches.
 *
 * Resulting files are stored in ~/.openambit
 *
 * Note: You may need to simplify GPX tracks to not exceed a certain
 * number of points, e.g. via:
 * gpsbabel -i gpx -f way_iii.gpx -x simplify,count=1000 -o gpx -F merge.gpx
 */
public class ConvertGPXToRoute {
    // 2020-07-04T14:52:41.5
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.z");

    public static void main(String[] args) throws IOException, SAXException {
        if (args.length < 1) {
            System.err.println("Need some .gpx files to convert");
            System.err.println();
            System.err.println("Usage: ConvertGPXToRoute <gpx-file> [<gpx-file> ...] ");
            System.exit(1);
        }

        for (String file : args) {
            File gpxFile = new File(file);
            if (!gpxFile.exists()) {
                System.err.println("Cannot read file: " + file);
                System.exit(2);
            }

            String name = StringUtils.removeEnd(gpxFile.getName(), ".gpx");

            // produce a simple id based on the file-name of the input GPX
            // but ensure to only use positive values
            int routeId = Math.abs(name.hashCode());

            File outFilePoints = new File(System.getenv("HOME") + "/.openambit/",
                    "routes_" + routeId + "_points_" +
                            name + ".json");

            File outFile = new File(System.getenv("HOME") + "/.openambit/",
                    "routes_" + routeId + "_" +
                            name + ".json");

            System.out.println("Reading from " + gpxFile.getAbsolutePath() +
                    ", writing to " + outFile.getAbsolutePath() + " and " + outFilePoints.getAbsolutePath());

            SortedMap<Long, TrackPoint> points = GPXTrackpointsParser.parseContent(gpxFile);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                TrackPoint start = points.get(points.firstKey());

                writer.write("{\n" +
                        "    \"ActivityID\": 5,\n" +
                        "    \"AscentAltitude\": 0,\n" +
                        "    \"CreatedBy\": 111111,\n" +
                        "    \"DescentAltitude\": 0,\n" +
                        "    \"Description\": \"\",\n" +
                        "    \"Distance\": 0,\n" +
                        "    \"LastModifiedDate\": \"" + DATE_FORMAT.format(gpxFile.lastModified()) + "\",\n" +
                        "    \"Name\": \"" + name + "\",\n" +
                        "    \"Points\": null,\n" +
                        "    \"RouteID\": " + routeId + ",\n" +
                        "    \"RoutePointsCount\": null,\n" +
                        "    \"RoutePointsURI\": \"routes/" + routeId + "/points\",\n" +
                        "    \"SelfURI\": \"routes/" + routeId + "\",\n" +
                        "    \"StartLatitude\": " + start.getLatitude() + ",\n" +
                        "    \"StartLongitude\": " + start.getLongitude() + ",\n" +
                        "    \"Thumbs\": 0,\n" +
                        "    \"TimesUsed\": 0,\n" +
                        "    \"UsersCount\": 0,\n" +
                        "    \"WaypointCount\": 0\n" +
                        "}\n");
            }

            try (BufferedWriter writerPoints = new BufferedWriter(new FileWriter(outFilePoints))) {
                writerPoints.write("{\n" +
                        "    \"CompressedRoutePoints\": null,\n" +
                        "    \"Points\": null,\n" +
                        "    \"RoutePoints\": [\n");

                boolean first = true;
                for (TrackPoint point : points.values()) {
                    if (!first) {
                        writerPoints.write(",\n");
                    }
                    first = false;

                    writerPoints.write(
                            "        {\n" +
                            "            \"Altitude\": " + point.getElevation() + ",\n" +
                            "            \"Latitude\": " + point.getLatitude() + ",\n" +
                            "            \"Longitude\": " + point.getLongitude() + ",\n" +
                            "            \"Name\": null,\n" +
                            "            \"RelativeDistance\": 0,\n" +
                            "            \"Type\": null\n" +
                            "        }");
                }

                writerPoints.write("\n    ]\n" +
                        "}\n");
            }

            System.out.println("Done writing to " + outFile.getAbsolutePath() + " and " + outFilePoints.getAbsolutePath());
        }
    }
}
