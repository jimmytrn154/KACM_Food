# Knowledge Graph Debugging Guide

## Issues Fixed

### 1. Error: "The string did not match the expected pattern"

**Problem:** This error occurs when the Neo4j URI format is incorrect or the connection fails.

**Fixes Applied:**
- ✅ Added better error handling in `getAllDiseases()` method
- ✅ Added connection validation in `Neo4jConfig`
- ✅ Added fallback to default diseases when connection fails
- ✅ Improved frontend error handling with better messages

### 2. Connection Issues

**Problem:** Neo4j connection might fail silently or show unclear errors.

**Fixes Applied:**
- ✅ Added connection status check on page load
- ✅ Better error messages in console
- ✅ Graceful fallback when Neo4j is unavailable

## How to Debug

### Step 1: Check Neo4j Connection

1. **Check application.properties:**
   ```properties
   spring.neo4j.uri=bolt://localhost:7687
   # OR for Neo4j Cloud:
   spring.neo4j.uri=neo4j+s://your-instance.databases.neo4j.io
   spring.neo4j.authentication.username=neo4j
   spring.neo4j.authentication.password=your-password
   ```

2. **Check Console Logs:**
   - Look for: `✓ Neo4j connection successful`
   - Or: `✗ Neo4j connection test failed`

3. **Test Connection:**
   - Go to: `http://localhost:8080/kg-recommend`
   - Check the status badge (green = connected, red = disconnected)

### Step 2: Initialize the Graph

If Neo4j is connected but no data exists:

1. Click **"Initialize Graph"** button
2. Wait for import to complete (may take several minutes)
3. Check console for import progress

### Step 3: Verify Data

**Check if diseases are loaded:**
```cypher
MATCH (d:Disease) RETURN d.name LIMIT 10
```

**Check if foods are loaded:**
```cypher
MATCH (f:Food) RETURN f.name LIMIT 10
```

**Check relationships:**
```cypher
MATCH (f:Food)-[r:ASSOCIATED_WITH]->(d:Disease)
RETURN f.name, r.effect, d.name
LIMIT 10
```

## Common Issues and Solutions

### Issue 1: "URI format error"

**Solution:**
- For local Neo4j: `bolt://localhost:7687`
- For Neo4j Cloud: `neo4j+s://your-instance.databases.neo4j.io`
- Make sure URI doesn't have extra spaces or characters

### Issue 2: "Authentication failed"

**Solution:**
- Check username and password in `application.properties`
- For Neo4j Cloud, use the credentials from your AuraDB dashboard
- Make sure password doesn't have special characters that need escaping

### Issue 3: "No diseases found"

**Solution:**
- Graph may not be initialized
- Click "Initialize Graph" button
- Or manually run the import Cypher queries in Neo4j Browser

### Issue 4: "Connection timeout"

**Solution:**
- Check if Neo4j server is running (for local)
- Check firewall settings
- For Neo4j Cloud, verify instance is active

## Testing the Fixes

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Navigate to KG page:**
   ```
   http://localhost:8080/kg-recommend
   ```

3. **Check status:**
   - Should show connection status badge
   - Should load diseases (either from graph or defaults)
   - Should not show error message

4. **Test recommendations:**
   - Select diseases
   - Enter allergies (optional)
   - Click "Get Recommendations"
   - Should see food recommendations with costs

## Default Behavior

If Neo4j is not connected or graph is empty:
- ✅ System shows default diseases (Diabetes, Hypertension, etc.)
- ✅ Recommendations may be limited but won't crash
- ✅ Error messages are clear and helpful
- ✅ User can still interact with the UI

## Next Steps

1. **Set up Neo4j Cloud:**
   - Create account at https://neo4j.com/cloud
   - Create AuraDB Free instance
   - Copy connection URI and credentials
   - Update `application.properties`

2. **Initialize Graph:**
   - Click "Initialize Graph" button
   - Wait for import to complete
   - Verify data in Neo4j Browser

3. **Test Recommendations:**
   - Select diseases
   - Get recommendations
   - Verify results make sense

## Debugging Commands

**Check Neo4j connection:**
```bash
curl http://localhost:8080/api/kg/status
```

**Get all diseases:**
```bash
curl http://localhost:8080/api/kg/diseases
```

**Get recommendations:**
```bash
curl -X POST http://localhost:8080/api/kg/recommend \
  -H "Content-Type: application/json" \
  -d '{"diseases":["Diabetes"],"allergies":[],"limit":5}'
```

---

**All errors should now be handled gracefully with clear messages!**

