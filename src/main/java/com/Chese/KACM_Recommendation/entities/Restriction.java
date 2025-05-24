package com.Chese.KACM_Recommendation.entities;

import java.util.*;


public class Restriction {
    private List<String> hate_taste_adjectives;
    private List<String> food_allergy; 
    private List<String> diet; 
    private String food_allergy_raw;
    private List<String> Preferred_Cuisines;

    public Restriction(){

    }

    public void setPreferred_Cuisines(List<String> Preferred_Cuisines){
        this.Preferred_Cuisines = Preferred_Cuisines;
    }

    public List<String> getPreferred_Cuisines(){
        return this.Preferred_Cuisines;
    }

    public List<String> gethate_taste_adjectives(){
        return this.hate_taste_adjectives;
    }

    public void sethate_taste_adjectives(List<String> hate_taste_adjectives){
        this.hate_taste_adjectives = hate_taste_adjectives;
    }
    
    public List<String> getfood_allergy(){
        return this.food_allergy;
    }

    public void setfood_allergy_raw(String food_allergy_raw){
        String[] parts = food_allergy_raw.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        List<String> result = Arrays.asList(parts);
        this.food_allergy = result;

    }

    public List<String> getdiet(){
        return this.diet;
    }

    public void setdiet(List<String> diet){
        this.diet = diet;
    }
}
