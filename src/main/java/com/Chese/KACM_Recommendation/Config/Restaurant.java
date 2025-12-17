package com.Chese.KACM_Recommendation.Config;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    public static final Map<String, LocationCoordinate> locations = new HashMap<>();
    static {
        // Starting point
        locations.put("VinUniversity", new LocationCoordinate("VinUniversity", 20.98892, 105.94601));
        
        // Vietnamese Cuisine
        locations.put("f1", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f2", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("f6", new LocationCoordinate("Phở Bò Gia Truyền", 20.99118, 105.93982));
        locations.put("f9", new LocationCoordinate("ComTam", 20.98730, 105.94410));
        locations.put("r1", new LocationCoordinate("Bún Bò Huế", 20.99020, 105.94530));
        locations.put("r2", new LocationCoordinate("Bánh Mì Phố", 20.98780, 105.94450));
        locations.put("r3", new LocationCoordinate("Chả Cá Lã Vọng", 20.98910, 105.94720));
        locations.put("r4", new LocationCoordinate("Bún Riêu Cua", 20.98690, 105.94580));
        locations.put("r5", new LocationCoordinate("Phở Gà", 20.99050, 105.94280));
        
        // International Cuisine
        locations.put("f3", new LocationCoordinate("Oliver's Pizza", 20.99153, 105.94365));
        locations.put("r6", new LocationCoordinate("KFC Gia Lâm", 20.99200, 105.94450));
        locations.put("r7", new LocationCoordinate("Lotteria", 20.99080, 105.94590));
        locations.put("r8", new LocationCoordinate("Pizza Hut", 20.98850, 105.94750));
        locations.put("r9", new LocationCoordinate("McDonald's", 20.99120, 105.94220));
        locations.put("r10", new LocationCoordinate("Burger King", 20.98980, 105.94800));
        
        // Asian Cuisine
        locations.put("r11", new LocationCoordinate("Sushi Hokkaido", 20.99060, 105.94420));
        locations.put("r12", new LocationCoordinate("Korean BBQ House", 20.98820, 105.94680));
        locations.put("r13", new LocationCoordinate("Thai Express", 20.98750, 105.94520));
        locations.put("r14", new LocationCoordinate("Ramen Ichiban", 20.99100, 105.94320));
        locations.put("r15", new LocationCoordinate("Dim Sum Palace", 20.98940, 105.94780));
        
        // Coffee & Desserts
        locations.put("r16", new LocationCoordinate("Highlands Coffee", 20.99000, 105.94550));
        locations.put("r17", new LocationCoordinate("Starbucks", 20.99150, 105.94400));
        locations.put("r18", new LocationCoordinate("The Coffee House", 20.98880, 105.94650));
        locations.put("r19", new LocationCoordinate("Trà Sữa Gong Cha", 20.98960, 105.94480));
        locations.put("r20", new LocationCoordinate("Bingsu Dessert", 20.99030, 105.94700));
        
        // Fine Dining
        locations.put("f4", new LocationCoordinate("Le Champ", 21.01034, 105.93976));
        locations.put("r21", new LocationCoordinate("La Villa French Restaurant", 20.99220, 105.94580));
        locations.put("r22", new LocationCoordinate("Ngon Garden", 20.98860, 105.94720));
        locations.put("r23", new LocationCoordinate("Madame Hien", 20.99040, 105.94620));
        
        // Local Favorites
        locations.put("r24", new LocationCoordinate("Bánh Cuốn Thanh Trì", 20.98720, 105.94420));
        locations.put("r25", new LocationCoordinate("Bún Đậu Mắm Tôm", 20.98840, 105.94560));
        locations.put("r26", new LocationCoordinate("Cháo Sườn", 20.98990, 105.94380));
        locations.put("r27", new LocationCoordinate("Bánh Xèo", 20.98760, 105.94640));
        locations.put("r28", new LocationCoordinate("Nem Nướng Nha Trang", 20.99070, 105.94460));
        
        // Seafood
        locations.put("r29", new LocationCoordinate("Hải Sản Biển Đông", 20.99180, 105.94620));
        locations.put("r30", new LocationCoordinate("Cua Biển Restaurant", 20.98890, 105.94760));
        
        // Hotpot & BBQ
        locations.put("r31", new LocationCoordinate("Lẩu Thái", 20.99010, 105.94510));
        locations.put("r32", new LocationCoordinate("BBQ House", 20.98930, 105.94430));
        locations.put("r33", new LocationCoordinate("Nướng Ngon", 20.98810, 105.94600));
        
        // Vegetarian
        locations.put("r34", new LocationCoordinate("Chay Ngon", 20.99090, 105.94340));
        locations.put("r35", new LocationCoordinate("Vegetarian Garden", 20.98770, 105.94540));
        
        // Fast Food & Street Food
        locations.put("r36", new LocationCoordinate("Bánh Mì Ông Già", 20.98950, 105.94400));
        locations.put("r37", new LocationCoordinate("Xôi Gà", 20.98830, 105.94520));
        locations.put("r38", new LocationCoordinate("Bánh Tráng Trộn", 20.99110, 105.94360));
        
        // Additional Options
        locations.put("f16", new LocationCoordinate("Chicken Rice", 21.00212, 105.94017));
        locations.put("f17", new LocationCoordinate("Sammy Restaurant Vinhomes Ocean Park", 20.99215, 105.95582));
    }
}
    