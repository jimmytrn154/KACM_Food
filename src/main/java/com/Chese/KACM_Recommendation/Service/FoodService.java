package com.Chese.KACM_Recommendation.Service;
import com.Chese.KACM_Recommendation.model.Food;
import org.springframework.stereotype.Service;
import com.Chese.KACM_Recommendation.model.FoodDetail;
import com.Chese.KACM_Recommendation.model.FoodSummary;
import com.Chese.KACM_Recommendation.model.FoodDetail.Nutrition;
import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class FoodService {
    private final Map<String, FoodDetail> foodMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        // Example entries (fill out f2, f3 similarly)        
        
        foodMap.put("f5", new FoodDetail(
            "f5", "Com Chien duong chau", "https://trumfood.vn/wp-content/uploads/2022/09/bdmt.jpg",
            List.of("spicy","grilled"), 4.5,
            "Tender meat with chili glaze.",
            "15 minutes", "25 minutes", "Medium", "4 people",
            List.of("8 oz pasta", "2 chicken breasts", "1 tbsp chili flakes"),
            List.of("Cook pasta al dente.", "Season chicken and grill.", "Toss together."),
            new Nutrition("520 kcal","28g","42g","26g")
        ));
        foodMap.put("f1", new FoodDetail(
            "f1", "Spicy Grill", "https://images2.thanhnien.vn/528068263637045248/2024/7/1/anh-man-hinh-2024-07-01-luc-113638-17198087615431007741437.png",
            List.of("spicy","grilled"), 4.5,
            "Tender meat with chili glaze.",
            "15 minutes", "25 minutes", "Medium", "4 people",
            List.of("8 oz pasta", "2 chicken breasts", "1 tbsp chili flakes"),
            List.of("Cook pasta al dente.", "Season chicken and grill.", "Toss together."),
            new Nutrition("520 kcal","28g","42g","26g")
        ));
        foodMap.put("f2", new FoodDetail(
            "f2", "Com Tam", "https://trumfood.vn/wp-content/uploads/2022/09/bdmt.jpg",
            List.of("spicy","grilled"), 4.5,
            "Tender meat with chili glaze.",
            "15 minutes", "25 minutes", "Medium", "4 people",
            List.of("8 oz pasta", "2 chicken breasts", "1 tbsp chili flakes"),
            List.of("Cook pasta al dente.", "Season chicken and grill.", "Toss together."),
            new Nutrition("520 kcal","28g","42g","26g")
        ));
        foodMap.put("f3", new FoodDetail(
            "f3", "Pho Bo", "https://trumfood.vn/wp-content/uploads/2022/09/bdmt.jpg",
            List.of("spicy","grilled"), 4.5,
            "Tender meat with chili glaze.",
            "15 minutes", "25 minutes", "Medium", "4 people",
            List.of("8 oz pasta", "2 chicken breasts", "1 tbsp chili flakes"),
            List.of("Cook pasta al dente.", "Season chicken and grill.", "Toss together."),
            new Nutrition("520 kcal","28g","42g","26g")
        ));
        foodMap.put("f4", new FoodDetail(
            "f4", "Bun bo Hue", "https://trumfood.vn/wp-content/uploads/2022/09/bdmt.jpg",
            List.of("spicy","grilled"), 4.5,
            "Tender meat with chili glaze.",
            "15 minutes", "25 minutes", "Medium", "4 people",
            List.of("8 oz pasta", "2 chicken breasts", "1 tbsp chili flakes"),
            List.of("Cook pasta al dente.", "Season chicken and grill.", "Toss together."),
            new Nutrition("520 kcal","28g","42g","26g")
        ));
        // ...add f2, f3, etc.
    }

    /** first three in insertion order */
    public List<FoodSummary> getFeatured() {
        List<FoodSummary> list = new ArrayList<>();
        for (FoodDetail fd : foodMap.values()) {
            list.add(fd);
        }
        return list.subList(0, Math.min(3, list.size()));
    }

    /** full list for the Food tab */
    public List<FoodSummary> getAll() {
        return new ArrayList<>(foodMap.values());
    }

    /** detail by id */
    public Optional<FoodDetail> getById(String id) {
        return Optional.ofNullable(foodMap.get(id));
    }
}

// foodMap.put("f1", new Food("f1", "Spicy Grill", "https://images2.thanhnien.vn/528068263637045248/2024/7/1/anh-man-hinh-2024-07-01-luc-113638-17198087615431007741437.png",
        //     List.of("spicy","grilled"), 4.5, "Tender meat with chili glazee."));
        // foodMap.put("f2", new Food("f2", "Vegan Bowl", "https://trumfood.vn/wp-content/uploads/2022/09/bdmt.jpg",
        //     List.of("vegan","fresh"), 4.2, "Fresh veggies & quinoa."));
        // foodMap.put("f3", new Food("f3", "Sweet Dessert", "https://nineshield.com.vn/wp-content/uploads/2024/03/com-chien-duong-chau-ngon.jpg",
        //     List.of("sweet","dessert"), 4.8, "Creamy custard with berries."));