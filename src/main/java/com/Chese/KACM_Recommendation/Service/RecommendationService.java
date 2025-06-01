package com.Chese.KACM_Recommendation.Service;

import java.util.*;

import org.springframework.stereotype.Service;
import com.Chese.KACM_Recommendation.model.FoodDetail;
import com.Chese.KACM_Recommendation.entities.Restriction;
import com.Chese.KACM_Recommendation.model.FoodSummary;

@Service
public class RecommendationService {

    private final FoodService svc;

    public RecommendationService(FoodService svc) {
        this.svc = svc;
    }

    public List<FoodSummary> getRecommendationsByDiet(Restriction restriction) {
        List<FoodDetail> foods = svc.getDetail();
        List<String> hateTastes = restriction.gethate_taste_adjectives();
        List<String> foodAllergys = restriction.getfood_allergy();
        List<String> diet = restriction.getdiet();
        List<String> preferredCuisines = restriction.getPreferred_Cuisines();

        PriorityQueue<FoodSummary> sortByDiet = new PriorityQueue<>((a, b) -> {
            if(a.getScore() > b.getScore()) return -1;
            if(a.getScore() < b.getScore()) return 1;
            return 0;

        }); 

        for (FoodDetail food : foods) {
            food.setScore(0); 
        }        
        
        for(FoodDetail food : foods){
            boolean flag = false;
            for(String tag : food.getTags()){
                if(hateTastes.contains(tag)){
                    flag = true;
                    break;
                }
            }

            if(flag == true) continue;
            flag = false;

            for(String ingre : food.getIngredients()){
                if(foodAllergys.contains(ingre)){
                    flag = true;
                    break;
                }
            }

            if(flag == true) continue;

            for(String tag : food.getTags()){
                if(diet.contains(tag)){
                    food.setScore(food.getScore()+5);
                }
            }

            for(String tag : food.getTags()){
                if(preferredCuisines.contains(tag)){
                    food.setScore(food.getScore()+1);
                }
            }
            sortByDiet.add(food);
            System.out.println(food.getScore());
        }
        return new ArrayList<>(sortByDiet);
    }

    public List<FoodSummary> getRecommendationsByCusine(Restriction restriction) {
        List<FoodDetail> foods = svc.getDetail();
        List<String> hateTastes = restriction.gethate_taste_adjectives();
        List<String> foodAllergys = restriction.getfood_allergy();
        List<String> diet = restriction.getdiet();
        List<String> preferredCuisines = restriction.getPreferred_Cuisines();

        PriorityQueue<FoodSummary> sortByCusine = new PriorityQueue<>((a, b) -> {
            if(a.getScore() > b.getScore()) return -1;
            if(a.getScore() < b.getScore()) return 1;
            return 0;

        }); 

        for (FoodDetail food : foods) {
            food.setScore(0); 
        }  
        
        for(FoodDetail food : foods){
            boolean flag = false;
            for(String tag : food.getTags()){
                if(hateTastes.contains(tag)){
                    flag = true;
                    break;
                }
            }
            if(flag == true) continue;
            flag = false;
            for(String ingre : food.getIngredients()){
                if(foodAllergys.contains(ingre)){
                    flag = true;
                    break;
                }
            }
            if(flag == true) continue;

            for(String tag : food.getTags()){
                if(diet.contains(tag)){
                    food.setScore(food.getScore()+1);
                }
            }

            for(String tag : food.getTags()){
                if(preferredCuisines.contains(tag)){
                    food.setScore(food.getScore()+5);
                }
            }
            sortByCusine.add(food);
            System.out.println(food.getScore());
        }

        return new ArrayList<>(sortByCusine);
    }
}
