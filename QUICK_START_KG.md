# Quick Start: Knowledge Graph Recommendations

## 🚀 5-Minute Setup

### 1. Get Neo4j Cloud Credentials
- Sign up: https://neo4j.com/cloud/platform/
- Create Neo4j AuraDB Free instance
- Copy: Bolt URI, Username, Password

### 2. Configure
Edit `src/main/resources/application.properties`:
```properties
spring.neo4j.uri=bolt://xxx.databases.neo4j.io:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=your-password
```

### 3. Start & Initialize
```bash
# Start application
mvn spring-boot:run

# Open browser
http://localhost:8080/kg-recommend

# Click "Initialize Graph" (wait 5-10 minutes)
```

### 4. Get Recommendations
1. Select diseases (e.g., Diabetes, Hypertension)
2. Enter allergies (optional)
3. Click "Get Recommendations"
4. View results sorted by suitability

## 📋 Example API Call

```bash
curl -X POST http://localhost:8080/api/kg/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "diseases": ["Diabetes", "Hypertension"],
    "allergies": ["Peanuts"],
    "limit": 10
  }'
```

## ✅ That's It!

Your Knowledge Graph recommendation system is ready to use!

For detailed documentation, see:
- `KNOWLEDGE_GRAPH_SETUP.md` - Full setup guide
- `INTEGRATION_SUMMARY.md` - Implementation details

