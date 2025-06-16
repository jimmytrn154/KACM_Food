package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RestaurantService {

    private final Map<String, RestaurantDetail> restaurantMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        Map<String, String> hours = Map.of(
            "Monday", "11:00-22:00", "Tuesday", "11:00-22:00", "Wednesday", "11:00-22:00",
            "Thursday", "11:00-22:00", "Friday", "11:00-22:00", "Saturday", "11:00-22:00", "Sunday", "Closed"
        );

        var resto1 = new RestaurantDetail("r3", "Oliver’s Pizza", "https://i.imgur.com/uRk7c2r.jpeg", 4.7, 83, "Sao Biển 9A, VinHomes Ocean Park", List.of("Italian", "Steak", "Pizza"), "A cozy spot for classic Italian food.", "₫200-300K", hours, "0876847979", "https://retro-pizza.com", List.of("f3", "f20"));
        var resto2 = new RestaurantDetail("r6", "Phở Bò Gia Truyền", "https://i.imgur.com/tY9D4f3.jpeg", 4.9, 152, "Ngọc Trai 6, VinHomes Ocean Park", List.of("Vietnamese", "Noodles", "Beef"), "Authentic Hanoi-style Phở.", "₫50-70K", hours, "0987654321", "https://phogiatruyen.com", List.of("f6"));

        restaurantMap.put("r3", resto1);
        restaurantMap.put("r6", resto2);
    }

    public List<RestaurantSummary> getAllSummaries() {
        return restaurantMap.values().stream()
            .map(r -> new RestaurantSummary(r.getId(), r.getName(), r.getImageUrl(), r.getRating(), r.getReviewCount(), r.getAddress(), r.getTags()))
            .toList();
    }

    public Optional<RestaurantDetail> getById(String id) {
        return Optional.ofNullable(restaurantMap.get(id));
    }
}
