package com.Chese.KACM_Recommendation.model;

import java.util.List;

public class RestaurantSummary {
    private String id;
    private String name;
    private String imageUrl;
    private double rating;
    private int reviewCount;
    private String address;
    private List<String> tags;

    // No-args constructor for JSON deserialization
    public RestaurantSummary() {}

    // All-args constructor
    public RestaurantSummary(String id, String name, String imageUrl, double rating, int reviewCount, String address, List<String> tags) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.address = address;
        this.tags = tags;
    }

    // Public Getters for all private fields
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getAddress() { return address; }
    public List<String> getTags() { return tags; }
}
