# How to Run the KACM Food Recommendation Web Application

## Prerequisites

- **Java 21** (check with `java -version`)
- **Maven** (check with `mvn -version`)
- **Internet connection** (for loading external resources)

## Quick Start

### Option 1: Using Maven (Recommended)

```bash
# Navigate to project directory
cd /Users/dienmayhaituyet/KACM_Food

# Run the application
mvn spring-boot:run
```

The application will start on: **http://localhost:8080**

### Option 2: Using Maven Wrapper

```bash
# On macOS/Linux
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

### Option 3: Build JAR and Run

```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar target/KACM_Recommendation-0.0.1-SNAPSHOT.jar
```

## Accessing the Application

Once started, open your browser and visit:

### Main Pages:
- **Landing Page:** http://localhost:8080/
- **Login:** http://localhost:8080/login
- **Register:** http://localhost:8080/register
- **Main Dashboard:** http://localhost:8080/index (requires login)
- **Explore:** http://localhost:8080/explore
- **Recommendations:** http://localhost:8080/recommend
- **Knowledge Graph Recommendations:** http://localhost:8080/kg-recommend
- **Advanced Routing:** http://localhost:8080/advanced-routing

### API Endpoints:
- **Food API:** http://localhost:8080/api/featured
- **Route API:** http://localhost:8080/api/route?from=VinUniversity&to=f1
- **KG API:** http://localhost:8080/api/kg/status

## Default Login Credentials

The system comes with 3 sample users:

1. **Username:** `Nguyen Van Duy Anh`  
   **Password:** `bachaphomai`

2. **Username:** `Tran Anh Chuong`  
   **Password:** `phuonganh`

3. **Username:** `Duong Hien Chi Kien`  
   **Password:** `chikien`

## Troubleshooting

### Port 8080 Already in Use

If you get an error that port 8080 is in use:

**Option 1:** Change the port in `application.properties`:
```properties
server.port=8081
```

**Option 2:** Kill the process using port 8080:
```bash
# Find the process
lsof -ti:8080

# Kill it
kill -9 $(lsof -ti:8080)
```

### Java Version Issues

Make sure you have Java 21:
```bash
java -version
# Should show: openjdk version "21" or similar
```

If you have multiple Java versions, set JAVA_HOME:
```bash
export JAVA_HOME=/path/to/java21
```

### Maven Not Found

Install Maven:
```bash
# macOS (using Homebrew)
brew install maven

# Or download from: https://maven.apache.org/download.cgi
```

### Neo4j Connection (Optional)

If you want to use Knowledge Graph features:
1. Set up Neo4j Cloud (see `KNOWLEDGE_GRAPH_SETUP.md`)
2. Update `application.properties` with Neo4j credentials
3. The app will work without Neo4j, but KG features won't be available

## Development Mode

For development with auto-reload:

```bash
mvn spring-boot:run
```

Spring Boot DevTools is included, so changes to Java files will trigger auto-restart.

## Production Deployment

### Build for Production:
```bash
mvn clean package -DskipTests
```

### Run Production JAR:
```bash
java -jar target/KACM_Recommendation-0.0.1-SNAPSHOT.jar
```

### Heroku Deployment:
The `Procfile` is already configured. Deploy to Heroku:
```bash
heroku create your-app-name
git push heroku main
```

## Application Features

Once running, you can:

1. **Browse Foods** - View featured dishes and explore all foods
2. **Get Recommendations** - Based on dietary restrictions and preferences
3. **Knowledge Graph Recommendations** - Disease-aware recommendations (requires Neo4j)
4. **Advanced Routing** - Modern routing algorithms with traffic awareness
5. **Route Planning** - Find shortest paths between restaurants

## Logs

Application logs will appear in the terminal. Look for:
```
Started KACM_Recommendation in X.XXX seconds
```

## Stopping the Application

Press `Ctrl+C` in the terminal to stop the application.

---

**Need Help?** Check the documentation files:
- `KNOWLEDGE_GRAPH_SETUP.md` - For KG setup
- `ADVANCED_ROUTING_FEATURES.md` - For routing features
- `INTEGRATION_SUMMARY.md` - For system overview

