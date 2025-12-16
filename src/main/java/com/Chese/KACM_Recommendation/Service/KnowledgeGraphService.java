package com.Chese.KACM_Recommendation.Service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for Food4healthKG Knowledge Graph operations
 * Handles data import, graph algorithms, and recommendations
 */
@Service
public class KnowledgeGraphService {
    
    @Autowired
    private Driver neo4jDriver;
    
    /**
     * Check if Neo4j connection is available
     */
    public boolean isConnected() {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run("RETURN 1 as test");
            return result.hasNext();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Import Food nodes from Food4healthKG
     */
    public void importFoodNodes() {
        String cypher = """
            LOAD CSV WITH HEADERS FROM
            'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/Food.csv'
            AS row
            MERGE (f:Food {name: row.food_name})
            SET f.id = row.food_id
            """;
        
        try (Session session = neo4jDriver.session()) {
            session.run(cypher);
        }
    }
    
    /**
     * Import Disease nodes
     */
    public void importDiseaseNodes() {
        String cypher = """
            LOAD CSV WITH HEADERS FROM
            'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/Disease.csv'
            AS row
            MERGE (d:Disease {name: row.disease_name})
            """;
        
        try (Session session = neo4jDriver.session()) {
            session.run(cypher);
        }
    }
    
    /**
     * Import Nutrient nodes
     */
    public void importNutrientNodes() {
        String cypher = """
            LOAD CSV WITH HEADERS FROM
            'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/Nutrient.csv'
            AS row
            MERGE (n:Nutrient {name: row.nutrient_name})
            """;
        
        try (Session session = neo4jDriver.session()) {
            session.run(cypher);
        }
    }
    
    /**
     * Import Food-Disease relationships
     */
    public void importFoodDiseaseRelationships() {
        String cypher = """
            LOAD CSV WITH HEADERS FROM
            'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/food_disease.csv'
            AS row
            MATCH (f:Food {name: row.food})
            MATCH (d:Disease {name: row.disease})
            MERGE (f)-[r:ASSOCIATED_WITH]->(d)
            SET r.effect = row.effect,
                r.score = toFloat(coalesce(row.score, '0'))
            """;
        
        try (Session session = neo4jDriver.session()) {
            session.run(cypher);
        }
    }
    
    /**
     * Import Food-Nutrient relationships
     */
    public void importFoodNutrientRelationships() {
        String cypher = """
            LOAD CSV WITH HEADERS FROM
            'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/food_nutrient.csv'
            AS row
            MATCH (f:Food {name: row.food})
            MATCH (n:Nutrient {name: row.nutrient})
            MERGE (f)-[r:CONTAINS]->(n)
            SET r.amount = coalesce(row.amount, '0')
            """;
        
        try (Session session = neo4jDriver.session()) {
            session.run(cypher);
        }
    }
    
    /**
     * Import HealthOutcome nodes and Food-HealthOutcome relationships
     */
    public void importHealthOutcomes() {
        try (Session session = neo4jDriver.session()) {
            // Try to import HealthOutcome nodes if CSV exists
            String cypher = """
                LOAD CSV WITH HEADERS FROM
                'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/HealthOutcome.csv'
                AS row
                MERGE (h:HealthOutcome {name: row.health_outcome_name})
                """;
            try {
                session.run(cypher);
            } catch (Exception e) {
                // HealthOutcome CSV might not exist, create some sample ones
                System.out.println("HealthOutcome CSV not found, creating sample outcomes");
            }
            
            // Try to import Food-HealthOutcome relationships
            String relCypher = """
                LOAD CSV WITH HEADERS FROM
                'https://raw.githubusercontent.com/ccszbd/Food4healthKG/main/data/food_healthoutcome.csv'
                AS row
                MATCH (f:Food {name: row.food})
                MERGE (h:HealthOutcome {name: row.health_outcome})
                MERGE (f)-[r:HAS_EFFECT]->(h)
                SET r.weight = -5.0
                """;
            try {
                session.run(relCypher);
            } catch (Exception e) {
                // Relationship CSV might not exist
                System.out.println("Food-HealthOutcome relationship CSV not found");
            }
        }
    }
    
    /**
     * Assign weights to relationships for Dijkstra algorithm
     * Food → Positive HealthOutcome: -5
     * Food → Disease (negative): +10
     * Food → Disease (positive): -3
     */
    public void assignRelationshipWeights() {
        try (Session session = neo4jDriver.session()) {
            // Assign weights to Food-Disease relationships
            String cypher1 = """
                MATCH (f:Food)-[r:ASSOCIATED_WITH]->(d:Disease)
                SET r.weight = 
                  CASE
                    WHEN r.effect = 'negative' THEN 10
                    ELSE -3
                  END
                """;
            session.run(cypher1);
            
            // Assign weights to Food-HealthOutcome relationships
            String cypher2 = """
                MATCH (f:Food)-[r:HAS_EFFECT]->(h:HealthOutcome)
                SET r.weight = -5
                """;
            session.run(cypher2);
        }
    }
    
    /**
     * Create a temporary user node and connect to diseases
     */
    public void createUserWithDiseases(String userId, List<String> diseases) {
        try (Session session = neo4jDriver.session()) {
            // Create or merge user
            String createUser = """
                MERGE (u:User {id: $userId})
                """;
            session.run(createUser, Values.parameters("userId", userId));
            
            // Connect user to diseases
            for (String disease : diseases) {
                String connectDisease = """
                    MATCH (u:User {id: $userId})
                    MATCH (d:Disease {name: $disease})
                    MERGE (u)-[r:HAS_DISEASE]->(d)
                    SET r.weight = 10
                    """;
                session.run(connectDisease, Values.parameters("userId", userId, "disease", disease));
            }
        }
    }
    
    /**
     * Get recommendations using Dijkstra algorithm
     * Returns foods with lowest total cost (best suitability)
     * Uses GDS if available, otherwise falls back to Cypher-based shortest path
     */
    public List<Map<String, Object>> getRecommendationsByDijkstra(String userId, int limit) {
        try (Session session = neo4jDriver.session()) {
            // Try GDS first (if available)
            try {
                createGraphProjection(session);
                
                String gdsCypher = """
                    MATCH (u:User {id: $userId})
                    CALL gds.shortestPath.dijkstra.stream('foodGraph', {
                      sourceNode: id(u),
                      relationshipWeightProperty: 'weight'
                    })
                    YIELD targetNode, totalCost
                    WHERE 'Food' IN labels(gds.util.asNode(targetNode))
                    RETURN gds.util.asNode(targetNode).name AS food,
                           totalCost
                    ORDER BY totalCost ASC
                    LIMIT $limit
                    """;
                
                Result result = session.run(gdsCypher, Values.parameters("userId", userId, "limit", limit));
                
                List<Map<String, Object>> recommendations = new ArrayList<>();
                while (result.hasNext()) {
                    var record = result.next();
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("food", record.get("food").asString());
                    rec.put("totalCost", record.get("totalCost").asDouble());
                    recommendations.add(rec);
                }
                
                if (!recommendations.isEmpty()) {
                    return recommendations;
                }
            } catch (Exception e) {
                // GDS not available, use fallback
                System.out.println("GDS not available, using Cypher-based shortest path");
            }
            
            // Fallback: Use Cypher-based weighted path finding
            return getRecommendationsByCypherShortestPath(session, userId, limit);
        }
    }
    
    /**
     * Fallback method: Use Cypher to find shortest weighted paths
     * This works without GDS library
     */
    private List<Map<String, Object>> getRecommendationsByCypherShortestPath(
            Session session, String userId, int limit) {
        
        // Find foods connected to user's diseases and calculate weighted cost
        String simplifiedCypher = """
            MATCH (u:User {id: $userId})-[:HAS_DISEASE]->(d:Disease)
            MATCH (f:Food)-[r:ASSOCIATED_WITH]->(d)
            WITH f, 
                 sum(CASE 
                   WHEN r.effect = 'negative' THEN 10.0 
                   WHEN r.effect = 'positive' THEN -3.0 
                   ELSE 0.0 
                 END) as diseaseCost
            OPTIONAL MATCH (f)-[h:HAS_EFFECT]->(ho:HealthOutcome)
            WITH f, diseaseCost, 
                 coalesce(sum(CASE WHEN h.weight IS NOT NULL THEN h.weight ELSE -5.0 END), 0.0) as healthBenefit
            WITH f, diseaseCost + healthBenefit as totalCost
            RETURN f.name AS food, totalCost
            ORDER BY totalCost ASC
            LIMIT $limit
            """;
        
        Result result = session.run(simplifiedCypher, Values.parameters("userId", userId, "limit", limit));
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        while (result.hasNext()) {
            var record = result.next();
            Map<String, Object> rec = new HashMap<>();
            rec.put("food", record.get("food").asString());
            rec.put("totalCost", record.get("totalCost").asDouble());
            recommendations.add(rec);
        }
        
        // If no results, try finding foods without negative disease connections (neutral/safe foods)
        if (recommendations.isEmpty()) {
            String fallbackCypher = """
                MATCH (f:Food)
                WHERE NOT EXISTS {
                  MATCH (f)-[:ASSOCIATED_WITH {effect: 'negative'}]->(:Disease)
                }
                OPTIONAL MATCH (f)-[h:HAS_EFFECT]->(ho:HealthOutcome)
                WITH f, coalesce(sum(CASE WHEN h.weight IS NOT NULL THEN h.weight ELSE -5.0 END), -5.0) as totalCost
                RETURN f.name AS food, totalCost
                ORDER BY totalCost ASC
                LIMIT $limit
                """;
            
            result = session.run(fallbackCypher, Values.parameters("limit", limit));
            while (result.hasNext()) {
                var record = result.next();
                Map<String, Object> rec = new HashMap<>();
                rec.put("food", record.get("food").asString());
                rec.put("totalCost", record.get("totalCost").asDouble());
                recommendations.add(rec);
            }
        }
        
        // If still no results, return any foods (last resort)
        if (recommendations.isEmpty()) {
            String lastResortCypher = """
                MATCH (f:Food)
                RETURN f.name AS food, 0.0 as totalCost
                LIMIT $limit
                """;
            
            result = session.run(lastResortCypher, Values.parameters("limit", limit));
            while (result.hasNext()) {
                var record = result.next();
                Map<String, Object> rec = new HashMap<>();
                rec.put("food", record.get("food").asString());
                rec.put("totalCost", record.get("totalCost").asDouble());
                recommendations.add(rec);
            }
        }
        
        return recommendations;
    }
    
    /**
     * Create graph projection for GDS algorithms
     * Note: GDS library requires Neo4j Enterprise or AuraDS
     * For free tier, we'll use a simplified Dijkstra implementation
     */
    private void createGraphProjection(Session session) {
        // Try to drop existing projection if it exists
        try {
            String dropCypher = "CALL gds.graph.drop('foodGraph', false)";
            session.run(dropCypher);
        } catch (Exception e) {
            // Graph doesn't exist or GDS not available, that's fine
        }
        
        // Try to create new projection (only works with GDS library)
        try {
            String createCypher = """
                CALL gds.graph.project(
                  'foodGraph',
                  ['User', 'Food', 'Disease', 'HealthOutcome'],
                  {
                    HAS_DISEASE: {properties: 'weight'},
                    ASSOCIATED_WITH: {properties: 'weight'},
                    HAS_EFFECT: {properties: 'weight'}
                  }
                )
                """;
            session.run(createCypher);
        } catch (Exception e) {
            // GDS not available, will use simplified algorithm
            System.out.println("GDS library not available, using simplified Dijkstra");
        }
    }
    
    /**
     * Get food details from knowledge graph
     */
    public Map<String, Object> getFoodDetails(String foodName) {
        try (Session session = neo4jDriver.session()) {
            String cypher = """
                MATCH (f:Food {name: $foodName})
                OPTIONAL MATCH (f)-[r1:ASSOCIATED_WITH]->(d:Disease)
                OPTIONAL MATCH (f)-[r2:CONTAINS]->(n:Nutrient)
                OPTIONAL MATCH (f)-[r3:HAS_EFFECT]->(h:HealthOutcome)
                RETURN f.name AS name,
                       collect(DISTINCT {disease: d.name, effect: r1.effect, score: r1.score}) AS diseases,
                       collect(DISTINCT {nutrient: n.name, amount: r2.amount}) AS nutrients,
                       collect(DISTINCT h.name) AS healthOutcomes
                """;
            
            Result result = session.run(cypher, Values.parameters("foodName", foodName));
            
            if (result.hasNext()) {
                var record = result.next();
                Map<String, Object> details = new HashMap<>();
                details.put("name", record.get("name").asString());
                details.put("diseases", record.get("diseases").asList());
                details.put("nutrients", record.get("nutrients").asList());
                details.put("healthOutcomes", record.get("healthOutcomes").asList());
                return details;
            }
            
            return null;
        }
    }
    
    /**
     * Initialize the knowledge graph (import all data)
     */
    public void initializeKnowledgeGraph() {
        System.out.println("Starting Knowledge Graph initialization...");
        System.out.println("Importing Food nodes...");
        importFoodNodes();
        System.out.println("Importing Disease nodes...");
        importDiseaseNodes();
        System.out.println("Importing Nutrient nodes...");
        importNutrientNodes();
        System.out.println("Importing Food-Disease relationships...");
        importFoodDiseaseRelationships();
        System.out.println("Importing Food-Nutrient relationships...");
        importFoodNutrientRelationships();
        System.out.println("Importing HealthOutcomes...");
        importHealthOutcomes();
        System.out.println("Assigning relationship weights...");
        assignRelationshipWeights();
        System.out.println("Knowledge Graph initialization complete!");
    }
    
    /**
     * Get all available diseases from the graph
     */
    public List<String> getAllDiseases() {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (d:Disease) RETURN d.name AS name ORDER BY name";
            Result result = session.run(cypher);
            
            List<String> diseases = new ArrayList<>();
            while (result.hasNext()) {
                diseases.add(result.next().get("name").asString());
            }
            return diseases;
        }
    }
    
    /**
     * Get all available foods from the graph
     */
    public List<String> getAllFoods() {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (f:Food) RETURN f.name AS name ORDER BY name LIMIT 100";
            Result result = session.run(cypher);
            
            List<String> foods = new ArrayList<>();
            while (result.hasNext()) {
                foods.add(result.next().get("name").asString());
            }
            return foods;
        }
    }
}

