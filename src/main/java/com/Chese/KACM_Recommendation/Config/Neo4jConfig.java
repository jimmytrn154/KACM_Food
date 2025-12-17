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
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

