package com.Chese.KACM_Recommendation.model;

import java.util.List;
import java.util.Map;

public class RestaurantDetail extends RestaurantSummary{
    

    // Additional Detail Fields
    private String description;
    private String priceRange;
    private Map<String, String> hoursOfOperation;
    private String phoneNumber;
    private String websiteUrl;
    private List<String> foodIds; // List of IDs for popular dishes

    // Constructors
    public RestaurantDetail() {}

    public RestaurantDetail(String id, String name, String imageUrl, double rating, int reviewCount, String address, List<String> tags, String description, String priceRange, Map<String, String> hoursOfOperation, String phoneNumber, String websiteUrl, List<String> foodIds) {
        super(id, name, imageUrl, rating, reviewCount, address, tags);
        this.description = description;
        this.priceRange = priceRange;
        this.hoursOfOperation = hoursOfOperation;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.foodIds = foodIds;
    }

    // Getters for all fields
    public String getDescription() { return description; }
    public String getPriceRange() { return priceRange; }
    public Map<String, String> getHoursOfOperation() { return hoursOfOperation; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsiteUrl() { return websiteUrl; }
    public List<String> getFoodIds() { return foodIds; }
}
