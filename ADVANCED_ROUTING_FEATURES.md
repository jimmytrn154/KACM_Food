# Advanced Routing Features Documentation

This document describes the 5 advanced routing features implemented in the KACM Food Recommendation System.

## Overview

The system now includes modern routing algorithms that handle:
1. **Time-dependent routing** (traffic-aware)
2. **Customizable routing** (CCH/CRP for fast preference-based queries)
3. **Top-K POI search** (nearest restaurants by travel time)
4. **Multi-criteria routing** (Pareto-optimal paths)
5. **K-shortest paths** (route alternatives)

## Feature Details

### 1. Time-Dependent Routing (Traffic-Aware)

**What it does:** Routes adapt based on departure time, accounting for traffic congestion during rush hours.

**Algorithm:** Time-Dependent Dijkstra

**Key Features:**
- Edge weights change based on departure time: `w_e(t) = length / speed_e(t)`
- Rush hour detection (7-9 AM, 5-7 PM) with speed reduction
- Congestion penalty calculation

**API Endpoint:**
```
GET /api/route/time-dependent?from=VinUniversity&to=f1&departureHour=8
```

**Response:**
```json
{
  "path": [...],
  "departureHour": 8,
  "congestionInfo": {
    "congestedSegments": 2,
    "totalCongestionPenalty": 0.8,
    "rushHour": true
  }
}
```

**Demo:** Visit `/advanced-routing` and use Feature 1 section. Change departure hour to see different routes.

---

### 2. Customizable Routing (CCH/CRP)

**What it does:** Fast routing with customizable preferences using Contraction Hierarchies.

**Algorithm:** Contraction Hierarchy (CH) with Customizable Route Planning (CRP)

**Key Features:**
- **Preprocessing (offline):** Builds hierarchy once
- **Customization (fast):** Updates edge weights based on preferences without rebuilding
- **Query (very fast):** Bidirectional search in hierarchical graph

**Preference Weights:**
- `timeWeight`: Prioritize speed
- `highwayWeight`: Penalty for using highways
- `scenicWeight`: Preference for scenic routes
- `safetyWeight`: Preference for safe routes
- `turnWeight`: Penalty for many turns
- `tollWeight`: Penalty for tolls

**API Endpoint:**
```
POST /api/route/customizable
Content-Type: application/json

{
  "from": "VinUniversity",
  "to": "f1",
  "preferences": {
    "timeWeight": 1.0,
    "highwayWeight": 0.5,
    "scenicWeight": 0.3,
    "safetyWeight": 0.4,
    "turnWeight": 0.2,
    "tollWeight": 0.1
  }
}
```

**Demo:** Use Feature 2 section with sliders to adjust preferences in real-time.

---

### 3. Top-K POI Search (Nearest Restaurants)

**What it does:** Finds K nearest restaurants by actual travel time (not Euclidean distance).

**Algorithm:** Multi-source Dijkstra with early termination

**Key Features:**
- Searches outward from user location
- Stops when K restaurants are found
- Returns travel time and path for each restaurant
- Shows search frontier visualization

**API Endpoint:**
```
GET /api/route/nearest-restaurants?from=VinUniversity&k=5
```

**Response:**
```json
[
  {
    "restaurantId": "f1",
    "travelTime": 8.5,
    "path": [...]
  },
  ...
]
```

**Demo:** Use Feature 3 section to find nearest restaurants. Compare with straight-line distance.

---

### 4. Multi-Criteria Routing (Pareto-Optimal)

**What it does:** Returns multiple optimal routes based on different criteria (time, safety, scenic, etc.).

**Algorithm:** Label-setting algorithm for multi-objective shortest paths

**Key Features:**
- Returns Pareto frontier (non-dominated solutions)
- Each route optimizes different criteria
- No single "best" route - user chooses based on preferences

**Criteria:**
- Travel time (minimize)
- Highway penalty (minimize)
- Scenic score (maximize)
- Safety score (maximize)
- Turn penalty (minimize)
- Toll cost (minimize)

**API Endpoint:**
```
GET /api/route/pareto?from=VinUniversity&to=f1
```

**Response:**
```json
[
  {
    "path": [...],
    "criteria": {
      "travelTime": 12.5,
      "highwayPenalty": 0.8,
      "scenicScore": 0.2,
      "safetyScore": 0.5,
      "turnPenalty": 0.1,
      "tollCost": 1.5
    },
    "description": "Pareto-optimal"
  },
  ...
]
```

**Demo:** Use Feature 4 section to see multiple optimal routes with different trade-offs.

---

### 5. K-Shortest Paths

**What it does:** Finds K distinct shortest paths between two points.

**Algorithm:** Yen's Algorithm

**Key Features:**
- Returns K alternative routes
- Each route is distinct (no duplicate paths)
- Ranked by travel time
- Useful for route comparison and user choice

**API Endpoint:**
```
GET /api/route/k-shortest?from=VinUniversity&to=f1&k=3
```

**Response:**
```json
[
  {
    "path": [...],
    "travelTime": 10.5,
    "description": "Shortest"
  },
  {
    "path": [...],
    "travelTime": 12.3,
    "description": "Alternative 2"
  },
  ...
]
```

**Demo:** Use Feature 5 section to see multiple route alternatives.

---

## Performance Benchmark

Compare Dijkstra vs CCH query performance:

**API Endpoint:**
```
GET /api/route/benchmark?from=VinUniversity&to=f1
```

**Response:**
```json
{
  "dijkstraTime": 1250,
  "chTime": 180,
  "speedup": 6.94,
  "dijkstraPath": [...],
  "chPath": [...]
}
```

**Demo:** Use Benchmark section to see performance comparison with bar chart.

---

## Frontend Demo

Access the interactive demo at:
```
http://localhost:8080/advanced-routing
```

The demo page includes:
- Interactive controls for each feature
- Real-time map visualization with Leaflet.js
- Results display with detailed information
- Performance benchmarks with visual charts

---

## Implementation Details

### File Structure

```
src/main/java/com/Chese/KACM_Recommendation/
├── algorithms/
│   ├── TimeDependentDijkstra.java      # Time-dependent routing
│   ├── ContractionHierarchy.java       # CCH/CRP implementation
│   ├── TopKPOISearch.java              # Top-K POI search
│   ├── MultiCriteriaRouting.java       # Pareto-optimal routing
│   └── KShortestPaths.java             # K-shortest paths (Yen's)
├── model/
│   ├── TimeDependentEdge.java          # Time-dependent edge model
│   ├── RouteCriteria.java              # Multi-criteria weights
│   ├── RoutingPreferences.java        # User preferences
│   ├── RoutePath.java                  # Route representation
│   └── ParetoRoute.java                # Pareto-optimal route
├── Service/
│   └── AdvancedRouteService.java       # Main service orchestrating all features
└── Controller/
    ├── RouteController.java            # REST API endpoints
    └── AdvancedRoutingController.java  # Demo page controller
```

### Graph Models

The system uses three graph representations:

1. **Standard Graph:** `Map<String, Map<String, Double>>`
   - Simple weight = travel time in minutes

2. **Time-Dependent Graph:** `Map<String, Map<String, TimeDependentEdge>>`
   - Edges with time-dependent weights
   - Speed profiles for different hours

3. **Multi-Criteria Graph:** `Map<String, Map<String, RouteCriteria>>`
   - Edges with multiple criteria (time, highway, scenic, safety, etc.)

### Algorithm Complexity

- **Time-Dependent Dijkstra:** O((V + E) log V) per query
- **CCH Preprocessing:** O(V log V + E) (one-time)
- **CCH Query:** O(V' log V') where V' << V (much faster than Dijkstra)
- **Top-K POI:** O((V + E) log V) with early termination
- **Multi-Criteria:** O(V * L * log(V * L)) where L is number of labels
- **K-Shortest Paths:** O(K * V * (E + V log V))

---

## Usage Examples

### Example 1: Traffic-Aware Routing

```java
// Get route at 8 AM (rush hour)
Map<String, Object> route = advancedRouteService.getTimeDependentRoute(
    "VinUniversity", "f1", 8
);

// Get same route at 2 PM (normal traffic)
Map<String, Object> route2 = advancedRouteService.getTimeDependentRoute(
    "VinUniversity", "f1", 14
);
// Different paths due to traffic!
```

### Example 2: Preference-Based Routing

```java
RoutingPreferences prefs = new RoutingPreferences();
prefs.setHighwayWeight(2.0);  // Strongly avoid highways
prefs.setScenicWeight(1.5);   // Strongly prefer scenic routes

Map<String, Object> route = advancedRouteService.getCustomizableRoute(
    "VinUniversity", "f1", prefs
);
```

### Example 3: Find Nearest Restaurants

```java
List<Map<String, Object>> nearest = advancedRouteService.getNearestRestaurants(
    "VinUniversity", 5
);
// Returns 5 nearest restaurants by travel time
```

---

## Future Enhancements

Potential improvements:
1. Real-time traffic data integration
2. Machine learning for traffic prediction
3. Full CH implementation with node contraction
4. ALT (A* with Landmarks) for even faster queries
5. Dynamic graph updates for real-time traffic
6. User preference learning from history

---

## References

- **Time-Dependent Routing:** Delling, D., & Wagner, D. (2009). Time-dependent route planning.
- **Contraction Hierarchies:** Geisberger, R., et al. (2008). Contraction hierarchies: Faster and simpler hierarchical routing in road networks.
- **Multi-Criteria Routing:** Jahn, O., et al. (2005). Multi-criteria shortest paths in time-dependent train networks.
- **K-Shortest Paths:** Yen, J. Y. (1971). Finding the K shortest loopless paths in a network.

