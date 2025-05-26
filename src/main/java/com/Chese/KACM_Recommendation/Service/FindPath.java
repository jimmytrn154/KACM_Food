package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.algorithms.Dijkstra;
import com.Chese.KACM_Recommendation.Config.LocationHashTable;

import org.apache.http.client.utils.URIBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class FindPath {

    // Haversine formula fallback in case we need direct computation
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static double getDistanceFromOSM(String source, String target) {
        try {
            URI uri = new URIBuilder("http://router.project-osrm.org/route/v1/driving/" + source + ";" + target)
                    .addParameter("overview", "false")
                    .build();

            HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();

            String json = output.toString();
            int index = json.indexOf("distance");
            if (index != -1) {
                String substring = json.substring(index + 9);
                double distance = Double.parseDouble(substring.split(",")[0]);
                return distance / 1000.0; // Convert from meters to km
            }

        } catch (Exception e) {
            System.err.println("Error calling OSRM API: " + e.getMessage());
        }
        return Double.MAX_VALUE;
    }

    public static void main(String[] args) {
        // Locations with coordinates (longitude,latitude format for OSRM)
        Map<String, String> coordinates = new HashMap<>();
        coordinates.put("VinUniversity", "105.94601,20.98892");
        coordinates.put("Pho24", "105.94350,20.98970");
        coordinates.put("BunChaHaNoi", "105.94820,20.98650");
        coordinates.put("ComTam", "105.94410,20.98730");

        // Construct graph using OSM distances
        Map<String, Map<String, Integer>> graph = new HashMap<>();

        for (String from : coordinates.keySet()) {
            Map<String, Integer> neighbors = new HashMap<>();
            for (String to : coordinates.keySet()) {
                if (!from.equals(to)) {
                    String source = coordinates.get(from);
                    String target = coordinates.get(to);
                    double distance = getDistanceFromOSM(source, target);
                    neighbors.put(to, (int) Math.round(distance));
                }
            }
            graph.put(from, neighbors);
        }

        // Run Dijkstra from VinUniversity
        Map<String, Integer> distances = Dijkstra.dijkstra(graph, "VinUniversity");
        System.out.println("Shortest paths from VinUniversity:");
        for (String node : distances.keySet()) {
            System.out.println("To " + node + " : " + distances.get(node) + " km");
        }
    }
}

