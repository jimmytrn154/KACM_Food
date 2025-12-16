# Knowledge Graph Setup Guide

This guide explains how to set up and use the Food4healthKG Knowledge Graph integration.

## Prerequisites

1. **Neo4j Cloud Account**
   - Sign up at: https://neo4j.com/cloud/platform/
   - Create a Neo4j AuraDB Free instance
   - Note down your:
     - Bolt URI (e.g., `bolt://xxx.databases.neo4j.io:7687`)
     - Username (usually `neo4j`)
     - Password

## Configuration

### 1. Update application.properties

Add your Neo4j credentials to `src/main/resources/application.properties`:

```properties
# Neo4j Configuration (Food4healthKG)
spring.neo4j.uri=bolt://your-instance.databases.neo4j.io:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=your-password
spring.data.neo4j.database=neo4j
```

Or set environment variables:
```bash
export NEO4J_URI=bolt://your-instance.databases.neo4j.io:7687
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=your-password
```

### 2. Initialize the Knowledge Graph

**Option A: Via Web UI**
1. Start your application
2. Navigate to `/kg-recommend`
3. Click "Initialize Graph" button
4. Wait for import to complete (may take several minutes)

**Option B: Via API**
```bash
curl -X POST http://localhost:8080/api/kg/initialize
```

**Option C: Via Neo4j Browser (Manual)**
1. Open Neo4j Browser from Aura console
2. Run the import commands from `KnowledgeGraphService.java` manually

## Data Import Process

The system imports the following from Food4healthKG:

1. **Nodes:**
   - Food nodes
   - Disease nodes
   - Nutrient nodes
   - HealthOutcome nodes (if available)

2. **Relationships:**
   - Food → Disease (ASSOCIATED_WITH)
   - Food → Nutrient (CONTAINS)
   - Food → HealthOutcome (HAS_EFFECT)

3. **Weights:**
   - Food → Disease (negative): +10
   - Food → Disease (positive): -3
   - Food → HealthOutcome: -5

## Using the System

### Web Interface

1. Navigate to `/kg-recommend`
2. Select your underlying diseases
3. Enter any allergies
4. Click "Get Recommendations"
5. View recommended foods sorted by suitability (lowest cost = best)

### API Endpoints

#### Get Recommendations
```bash
POST /api/kg/recommend
Content-Type: application/json

{
  "userId": "user123",
  "diseases": ["Diabetes", "Hypertension"],
  "allergies": ["Peanuts"],
  "limit": 10
}
```

#### Get Food Details
```bash
GET /api/kg/food/{foodName}
```

#### Get All Diseases
```bash
GET /api/kg/diseases
```

#### Get All Foods
```bash
GET /api/kg/foods
```

#### Check Connection Status
```bash
GET /api/kg/status
```

## Algorithm Details

### Dijkstra's Algorithm Implementation

The system uses two approaches:

1. **GDS Library (if available):**
   - Uses Neo4j Graph Data Science library
   - Requires Neo4j Enterprise or AuraDS
   - Faster and more efficient

2. **Cypher-based Fallback:**
   - Works with Neo4j AuraDB Free
   - Uses weighted path queries
   - Calculates total cost based on relationship weights

### Weight Calculation

- **Lower totalCost = Better suitability**
- Foods with negative disease associations get +10 penalty
- Foods with positive health outcomes get -5 benefit
- Final recommendation sorted by ascending totalCost

## Troubleshooting

### Connection Issues

1. **Check credentials:**
   ```bash
   GET /api/kg/status
   ```

2. **Verify Neo4j instance is running:**
   - Check Aura console
   - Ensure instance is not paused

3. **Check firewall/network:**
   - Ensure port 7687 is accessible
   - Check if VPN is blocking connection

### Import Issues

1. **CSV files not found:**
   - Verify GitHub repository is accessible
   - Check CSV file paths in service methods
   - Some files might not exist in the repository

2. **Import takes too long:**
   - Food4healthKG can have thousands of nodes
   - Be patient, import may take 5-10 minutes
   - Check Neo4j Browser for progress

3. **Memory issues:**
   - Free tier has limited memory
   - Consider importing data in batches
   - Use smaller subsets for testing

### Recommendation Issues

1. **No recommendations returned:**
   - Check if graph is initialized
   - Verify user-disease connections exist
   - Try different diseases
   - Check if foods exist in graph

2. **GDS errors:**
   - Free tier doesn't include GDS
   - System automatically falls back to Cypher
   - This is normal and expected

## Data Sources

- **Food4healthKG Repository:** https://github.com/ccszbd/Food4healthKG
- **CSV Files:** Located in `/data` directory
- **Raw URLs:** Used for importing via LOAD CSV

## Next Steps

1. Set up Neo4j Cloud instance
2. Configure credentials
3. Initialize knowledge graph
4. Start getting recommendations!

For more details, see the main documentation in `ADVANCED_ROUTING_FEATURES.md`.

