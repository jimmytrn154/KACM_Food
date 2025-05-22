package com.Chese.KACM_Recommendation.model;
import java.util.List;

public class Food {
    private String id;
    private String name;
    private String imageUrl;
    private List<String> tags;
    private double rating;
    private String description;

    public Food() { /* no-arg for Jackson if you ever POST */ }
    // constructors, getters & setters
    public Food(String id,
                String name,
                String imageUrl,
                List<String> tags,
                double rating,
                String description) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.rating = rating;
        this.description = description;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }
}
