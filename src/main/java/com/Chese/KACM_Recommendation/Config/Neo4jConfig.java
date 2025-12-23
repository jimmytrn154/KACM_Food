package com.Chese.KACM_Recommendation.Config;

import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

/**
 * Configuration for Neo4j and RestTemplate
 */
@Configuration
public class Neo4jConfig {
    
    @Value("${spring.neo4j.uri:bolt://localhost:7687}")
    private String uri;
    
    @Value("${spring.neo4j.authentication.username:neo4j}")
    private String username;
    
    @Value("${spring.neo4j.authentication.password:password}")
    private String password;
    
    @Bean
    public Driver neo4jDriver() {
        try {
            System.out.println("Connecting to Neo4j at: " + uri);
            System.out.println("Username: " + username);
            
            // Create driver (don't test connection here to allow graceful degradation)
            Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
            
            // Test connection asynchronously (non-blocking)
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Wait a bit before testing
                    try (var session = driver.session()) {
                        var result = session.run("RETURN 1 as test");
                        if (result.hasNext()) {
                            System.out.println("✓ Neo4j connection successful");
                        }
                    }
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    System.err.println("✗ Neo4j connection test failed: " + errorMsg);
                    
                    if (errorMsg != null && errorMsg.contains("Unable to connect")) {
                        System.err.println("");
                        System.err.println("═══════════════════════════════════════════════════════");
                        System.err.println("  NEO4J CONNECTION ERROR");
                        System.err.println("═══════════════════════════════════════════════════════");
                        System.err.println("The system cannot connect to Neo4j at: " + uri);
                        System.err.println("");
                        System.err.println("OPTIONS:");
                        System.err.println("1. Start local Neo4j: docker run -p 7474:7474 -p 7687:7687 neo4j");
                        System.err.println("2. Use Neo4j Cloud: Update application.properties with your AuraDB URI");
                        System.err.println("3. Continue without Neo4j: System will use default data");
                        System.err.println("");
                        System.err.println("The application will continue to work with limited functionality.");
                        System.err.println("═══════════════════════════════════════════════════════");
                    } else {
                        System.err.println("Please check your Neo4j URI, username, and password in application.properties");
                    }
                }
            }).start();
            
            return driver;
        } catch (Exception e) {
            System.err.println("Failed to create Neo4j driver: " + e.getMessage());
            System.err.println("URI format: bolt://host:port or neo4j+s://host:port");
            // Don't throw - allow application to start without Neo4j
            System.err.println("Application will continue without Neo4j connection.");
            return null; // Return null to indicate no connection
        }
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

