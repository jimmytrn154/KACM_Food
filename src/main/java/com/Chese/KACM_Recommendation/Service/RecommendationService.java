package com.Chese.KACM_Recommendation.Service;

import java.util.*;

import org.springframework.stereotype.Service;
import com.Chese.KACM_Recommendation.model.FoodDetail;
import com.Chese.KACM_Recommendation.entities.Restriction;
import com.Chese.KACM_Recommendation.model.FoodSummary;


class maxheap{
    FoodSummary[] heap;
    int currentsize;
    int capacity;
    HashMap<String, Integer> position;

    maxheap(int capa){
        this.heap=new FoodSummary[capa];
        this.currentsize=0;
        this.capacity=capa;
        this.position = new HashMap<>();
    }

    int parent(int i) {return (i-1)/2;}
    int left(int i) {return 2*i+1;}
    int right(int i) {return 2*i+2;}

    void insert(FoodSummary food){
        if(currentsize==capacity) return;
        heap[currentsize]=food;
        position.put(food.getId(), currentsize);
        int i=currentsize;
        currentsize=currentsize+1;

        while(i!=0&&heap[i].getScore()>heap[parent(i)].getScore()){
            FoodSummary temp = heap[parent(i)];
            heap[parent(i)] = heap[i];
            heap[i] = temp;

            position.put(heap[i].getId(), i);
            position.put(heap[parent(i)].getId(), parent(i));

            i = parent(i);
        }
    }

    void maxHeapify(int i){
        int l=left(i);
        int r=right(i);
        int largest=i;

        if(l<currentsize && heap[l].getScore()>heap[largest].getScore()) largest=l;
        if(r<currentsize && heap[r].getScore()>heap[largest].getScore()) largest=r;

        if(largest!=i){
            FoodSummary temp=heap[largest];
            heap[largest]=heap[i];
            heap[i]=temp;

            position.put(heap[i].getId(), i);
            position.put(heap[largest].getId(), largest);

            maxHeapify(largest);
        }
    }

    void delete(int i){
        if(currentsize==1){
            currentsize=currentsize-1;
            return;
        }

        heap[i]=heap[--currentsize];
        position.put(heap[i].getId(), currentsize);

        while(i!=0&&heap[i].getScore()>heap[parent(i)].getScore()){
            FoodSummary temp = heap[parent(i)];
            heap[parent(i)] = heap[i];
            heap[i] = temp;

            position.put(heap[i].getId(), i);
            position.put(heap[parent(i)].getId(), parent(i));

            i = parent(i);
        }

        maxHeapify(i);
    }

    ArrayList<FoodSummary> sort(){
        FoodSummary[] temp = heap.clone();
        int new_size = currentsize;
        ArrayList<FoodSummary> newSortedList = new ArrayList<>();
        
        while(currentsize>0){
            newSortedList.add(extractMax_sort(temp));
        }
        currentsize=new_size;
        return newSortedList;
    }

    FoodSummary extractMax_sort(FoodSummary[] arr){
        if(currentsize<=0) return null;
        if(currentsize==1) return arr[--currentsize];
        FoodSummary root=arr[0];
        arr[0]=arr[--currentsize];
        maxHeapify_sort(arr, 0);
        return root;
    }

    void maxHeapify_sort(FoodSummary[] arr, int i){
        int l=left(i);
        int r=right(i);
        int largest=i;

        if(l<currentsize && arr[l].getScore()>arr[largest].getScore()) largest=l;
        if(r<currentsize && arr[r].getScore()>arr[largest].getScore()) largest=r;

        if(largest!=i){
            FoodSummary temp=arr[largest];
            arr[largest]=arr[i];
            arr[i]=temp;
            maxHeapify_sort(arr, largest);
        }
    }


}


@Service
public class RecommendationService {

    private final FoodService svc;
    List<FoodDetail> foods;
    maxheap diet_graph;
    maxheap cusine_graph;

    public RecommendationService(FoodService svc) {
        this.svc = svc;
        this.foods = this.svc.getDetail();
        this.diet_graph = new maxheap(this.foods.size());
        this.cusine_graph = new maxheap(this.foods.size());
    }

    public List<FoodSummary> getRecommendationsByDiet(Restriction restriction) {

        if(diet_graph.currentsize>0){
            return diet_graph.sort();
        }

        List<String> hateTastes = restriction.gethate_taste_adjectives();
        List<String> foodAllergys = restriction.getfood_allergy();
        List<String> diet = restriction.getdiet();

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
            diet_graph.insert(food);
        }
        return diet_graph.sort();
    }

    public List<FoodSummary> getRecommendationsByCusine(Restriction restriction) {

        if(cusine_graph.currentsize>0){
            return cusine_graph.sort();
        }

        List<String> hateTastes = restriction.gethate_taste_adjectives();
        List<String> foodAllergys = restriction.getfood_allergy();
        List<String> preferredCuisines = restriction.getPreferred_Cuisines();

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

            for(String tag : food.getTags()){
                if(preferredCuisines.contains(tag)){
                    food.setScore(food.getScore()+1);
                }
            }
            cusine_graph.insert(food);
        }

        return cusine_graph.sort();
    }

    public void delete(String id){
        diet_graph.delete(diet_graph.position.get(id));
        cusine_graph.delete(cusine_graph.position.get(id));
    }

}
