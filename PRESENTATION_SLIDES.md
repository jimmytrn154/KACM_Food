# KACM Food Recommendation System
## Presentation Slides

---

## Slide 1: Title Slide
# KACM Food Recommendation System
### Advanced Routing & Knowledge Graph Integration

**Team Members:**
- Nguyen Van Duy Anh
- Tran Anh Chuong
- Duong Hien Chi Kien

**Technologies:** Spring Boot, Neo4j, Graph Algorithms

---

## Slide 2: Project Overview
# What is KACM?

**A Smart Food Recommendation Platform**

- 🍽️ Personalized food recommendations
- 🗺️ Intelligent route planning
- 🧠 Knowledge graph-based suggestions
- 🎯 Multi-criteria optimization

**Key Features:**
- User authentication & preferences
- Dietary restriction handling
- Traffic-aware routing
- Disease-aware recommendations

---

## Slide 3: System Architecture
# Architecture Overview

```
┌─────────────┐
│   Frontend  │  Thymeleaf Templates
│  (Browser)  │  + Leaflet.js Maps
└──────┬──────┘
       │
┌──────▼──────────────────┐
│   Spring Boot Backend   │
│  ┌────────────────────┐ │
│  │  Controllers       │ │
│  │  Services          │ │
│  │  Algorithms       │ │
│  └────────────────────┘ │
└──────┬──────────────────┘
       │
┌──────▼──────┐
│   Neo4j     │  Knowledge Graph
│   Cloud     │  (Food4healthKG)
└─────────────┘
```

---

## Slide 4: Core Features
# Main Features

### 1. Food Recommendations
- Tag-based matching
- Dietary preferences
- Allergy filtering
- Max-heap scoring algorithm

### 2. Route Planning
- Shortest path calculation
- Restaurant location mapping
- Distance calculation (Haversine)

### 3. User Management
- Registration & Login
- Session management
- Preference storage

---

## Slide 5: Advanced Routing Features
# Modern Routing Algorithms

## 1. Time-Dependent Routing
- **Traffic-aware paths**
- Rush hour detection
- Dynamic edge weights
- Algorithm: Time-Dependent Dijkstra

## 2. Customizable Routing (CCH/CRP)
- **Fast preference updates**
- Contraction Hierarchies
- Real-time customization
- 6-7x faster than standard Dijkstra

---

## Slide 6: Advanced Routing (Continued)
# More Routing Features

## 3. Top-K POI Search
- **Nearest restaurants by travel time**
- Not Euclidean distance!
- Multi-source Dijkstra
- Search frontier visualization

## 4. Multi-Criteria Routing
- **Pareto-optimal paths**
- Multiple objectives:
  - Travel time
  - Highway avoidance
  - Scenic routes
  - Safety scores

---

## Slide 7: Advanced Routing (Final)
# Route Alternatives

## 5. K-Shortest Paths
- **Multiple route options**
- Yen's Algorithm
- Ranked by travel time
- User choice & explainability

**Example Routes:**
- Fastest
- Avoid Highways
- Most Scenic
- Most Reliable

---

## Slide 8: Knowledge Graph Integration
# Food4healthKG Integration

**What is Food4healthKG?**
- Heterogeneous knowledge graph
- Food-health relationships
- Evidence-based connections
- Deployed on Neo4j Cloud

**Node Types:**
- Food, Disease, Nutrient
- Compound, Microbiota
- HealthOutcome

---

## Slide 9: Knowledge Graph Algorithm
# Graph-Based Recommendations

**Dijkstra's Algorithm on Knowledge Graph**

```
User → Disease → Food → HealthOutcome
  ↓       ↓        ↓         ↓
 10    +10/-3    -5        -5
```

**Weight System:**
- Food → Disease (negative): +10 penalty
- Food → Disease (positive): -3 benefit
- Food → HealthOutcome: -5 benefit

**Result:** Lower totalCost = Better suitability

---

## Slide 10: Technical Stack
# Technologies Used

**Backend:**
- Spring Boot 3.4.5
- Java 21
- Neo4j Java Driver
- Maven

**Frontend:**
- Thymeleaf
- Leaflet.js (Maps)
- Material Icons
- Custom CSS

**Algorithms:**
- Dijkstra's Algorithm
- Time-Dependent Dijkstra
- Contraction Hierarchies
- Yen's K-Shortest Paths
- Multi-Criteria Routing

---

## Slide 11: Data Flow
# How Recommendations Work

### Traditional System:
```
User Preferences → Tag Matching → Max-Heap → Recommendations
```

### Knowledge Graph System:
```
User Diseases → Graph Traversal → Dijkstra → Weighted Scores → Recommendations
```

### Routing System:
```
Start/End → Graph Construction → Algorithm → Path → Map Visualization
```

---

## Slide 12: Key Algorithms
# Algorithm Highlights

| Algorithm | Use Case | Complexity |
|-----------|----------|-------------|
| **Dijkstra** | Basic shortest path | O((V+E)log V) |
| **Time-Dependent Dijkstra** | Traffic-aware routing | O((V+E)log V) |
| **Contraction Hierarchy** | Fast customizable queries | O(V log V) preprocessing |
| **Yen's Algorithm** | K-shortest paths | O(K·V·(E+V log V)) |
| **Multi-Criteria** | Pareto-optimal paths | O(V·L·log(V·L)) |

---

## Slide 13: Performance
# Benchmark Results

**CCH vs Standard Dijkstra:**

```
Dijkstra:  ~1250 microseconds
CCH:       ~180 microseconds
Speedup:   6.94x faster
```

**Benefits:**
- Fast preference updates
- No graph rebuild needed
- Scalable to large networks

---

## Slide 14: Demo Screenshots
# User Interface

**Main Features:**
1. Landing Page - Welcome screen
2. Login/Register - User authentication
3. Food Exploration - Browse all dishes
4. Recommendations - Personalized suggestions
5. KG Recommendations - Disease-aware
6. Advanced Routing - Interactive demo

**Visual Elements:**
- Interactive maps
- Real-time route visualization
- Preference sliders
- Performance charts

---

## Slide 15: Use Cases
# Real-World Applications

**For Users:**
- Find suitable foods for health conditions
- Discover new restaurants
- Plan optimal routes
- Avoid traffic congestion

**For Restaurants:**
- Understand customer preferences
- Optimize delivery routes
- Health-conscious menu planning

**For Healthcare:**
- Dietary recommendations
- Disease management
- Nutritional planning

---

## Slide 16: Innovation Points
# What Makes This Special?

1. **Dual Recommendation Systems**
   - Traditional tag-based
   - Knowledge graph-based
   - User can choose

2. **Modern Routing Algorithms**
   - Not just basic Dijkstra
   - Traffic-aware, multi-criteria
   - Production-ready techniques

3. **Graph-Based Intelligence**
   - Evidence-based recommendations
   - Disease-aware filtering
   - Health outcome optimization

---

## Slide 17: Technical Challenges
# Challenges Overcome

**Challenge 1: Neo4j GDS Library**
- Free tier doesn't include GDS
- **Solution:** Cypher-based fallback algorithm

**Challenge 2: Large Graph Import**
- Food4healthKG has thousands of nodes
- **Solution:** Efficient CSV loading with error handling

**Challenge 3: Real-Time Updates**
- Preference changes need fast response
- **Solution:** CCH allows instant customization

---

## Slide 18: Results & Metrics
# System Performance

**Recommendation Quality:**
- ✅ Accurate disease filtering
- ✅ Allergy avoidance
- ✅ Preference matching

**Routing Performance:**
- ✅ 6.94x faster with CCH
- ✅ Traffic-aware paths
- ✅ Multiple route alternatives

**User Experience:**
- ✅ Intuitive interface
- ✅ Real-time updates
- ✅ Interactive visualizations

---

## Slide 19: Future Enhancements
# Roadmap

**Short Term:**
- Real-time traffic data integration
- Machine learning for preference learning
- Mobile app development

**Long Term:**
- Full CH implementation with node contraction
- ALT (A* with Landmarks) for faster queries
- Dynamic graph updates
- Multi-user collaborative filtering

---

## Slide 20: Conclusion
# Summary

**What We Built:**
- ✅ Complete food recommendation system
- ✅ Advanced routing with 5 modern algorithms
- ✅ Knowledge graph integration
- ✅ Production-ready architecture

**Key Achievements:**
- 🎯 Multi-criteria optimization
- 🚀 High-performance algorithms
- 🧠 Evidence-based recommendations
- 🗺️ Intelligent route planning

**Thank You!**

---

## Slide 21: Q&A
# Questions?

**Contact:**
- Project Repository: [GitHub]
- Documentation: See project README files
- Demo: http://localhost:8080

**Key Files:**
- `ADVANCED_ROUTING_FEATURES.md`
- `KNOWLEDGE_GRAPH_SETUP.md`
- `INTEGRATION_SUMMARY.md`

