package com.Chese.KACM_Recommendation.model;
import java.util.*;

public class FoodDetail extends FoodSummary {
    private String prepTime;
    private String cookTime;
    private String difficulty;
    private String servings;
    private List<String> ingredients;
    private List<String> instructions;
    private Nutrition nutrition;

    public FoodDetail() { }

    public FoodDetail(String id,
                      String name,
                      String imageUrl,
                      List<String> tags,
                      double rating,
                      String description,
                      String prepTime,
                      String cookTime,
                      String difficulty,
                      String servings,
                      List<String> ingredients,
                      List<String> instructions,
                      Nutrition nutrition) {
        super(id, name, imageUrl, tags, rating, description);
        this.prepTime     = prepTime;
        this.cookTime     = cookTime;
        this.difficulty   = difficulty;
        this.servings     = servings;
        this.ingredients  = ingredients;
        this.instructions = instructions;
        this.nutrition    = nutrition;
    }

    public String getPrepTime() { return prepTime; }
    public String getCookTime() { return cookTime; }
    public String getDifficulty() { return difficulty; }
    public String getServings() { return servings; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getInstructions() { return instructions; }
    public Nutrition getNutrition() { return nutrition; }

    public static class Nutrition {
        private String calories;
        private String protein;
        private String carbs;
        private String fat;

        public Nutrition() { }

        public Nutrition(String calories,
                         String protein,
                         String carbs,
                         String fat) {
            this.calories = calories;
            this.protein  = protein;
            this.carbs    = carbs;
            this.fat      = fat;
        }

        public String getCalories() { return calories; }
        public String getProtein()  { return protein; }
        public String getCarbs()    { return carbs; }
        public String getFat()      { return fat; }
    }
}
