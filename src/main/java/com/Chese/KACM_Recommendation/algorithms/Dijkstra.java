package com.Chese.KACM_Recommendation.algorithms;

import java.util.*;

public class Dijkstra {
    public static Map<String, Integer> dijkstra(Map<String, Map<String, Integer>> graph, String start) {
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            visited.add(current);

            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                String nextNode = neighbor.getKey();
                int weight = neighbor.getValue();
                if (!visited.contains(nextNode)) {
                    int newDist = distances.get(current) + weight;
                    if (newDist < distances.get(nextNode)) {
                        distances.put(nextNode, newDist);
                        pq.add(nextNode);
                    }
                }
            }
        }

        return distances;
    }
}
