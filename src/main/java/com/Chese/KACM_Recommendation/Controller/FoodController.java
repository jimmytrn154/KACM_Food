package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.model.FoodDetail;
import com.Chese.KACM_Recommendation.model.FoodSummary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Chese.KACM_Recommendation.Service.FoodService;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FoodController {
    private final FoodService svc;
    public FoodController(FoodService svc) { this.svc = svc; }

    @GetMapping("/featured")
    public List<FoodSummary> featured() {
        return svc.getFeatured();
    }

    @GetMapping("/dishes")
    public List<FoodSummary> allDishes() {
        return svc.getAll();
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<FoodDetail> getDish(@PathVariable String id) {
        return svc.getById(id)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
}
