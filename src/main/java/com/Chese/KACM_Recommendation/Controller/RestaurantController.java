package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.Service.RestaurantService;
import com.Chese.KACM_Recommendation.model.RestaurantDetail;
import com.Chese.KACM_Recommendation.model.RestaurantSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantSummary> getAllRestaurantSummaries() {
        return restaurantService.getAllSummaries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetail> getRestaurantById(@PathVariable String id) {
        return restaurantService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
