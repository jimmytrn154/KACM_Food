package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.Chese.KACM_Recommendation.Service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/route")
    public List<LocationCoordinate> route(
            @RequestParam String from,
            @RequestParam String to) {
        return routeService.getShortestPath(from, to);
    }
}