package ch.epfl.polybazaar.listing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

public abstract class CategoryRepository {
    public static List<Category> categories = init();

    private static List<Category> init(){

        Category books = new StringCategory("Book");
        books.addSubCategory(new StringCategory("Engineering"));
        books.addSubCategory(new StringCategory("Cooking"));

        Category multimedia = new StringCategory("Multimedia");
        multimedia.addSubCategory(new StringCategory("Television"));
        multimedia.addSubCategory(new StringCategory("Computer"));

        Category videoGames = new StringCategory("Video games");
        videoGames.addSubCategory(new StringCategory("Nintendo Switch"));
        videoGames.addSubCategory(new StringCategory("Playstation 4"));
        videoGames.addSubCategory(new StringCategory("PC"));
        videoGames.addSubCategory(new StringCategory("XBOX One"));

        multimedia.addSubCategory(videoGames);

        return Arrays.asList(new StringCategory("Furniture"), books, multimedia);
    }

    public static void addCategory(Category category){
        categories.add(category);
    }
    void deleteCategory(Category category){
        categories.remove(category);
    }
    boolean isEmpty(){
        return categories.isEmpty();
    }
    int size(){
        int size = 0;
        for(Category category : categories){
            size += category.size();
        }
        return size;
    }
    int maxDepth(){
        int maxDepth = 0;
        for(Category category : categories){
            maxDepth = max(maxDepth, category.maxDepth());
        }
        return maxDepth;
    }
}
