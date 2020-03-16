package ch.epfl.polybazaar.listing;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

public abstract class CategoryRepository {
    /**
     * List containing all the categories of the repository
     */
    public static List<Category> categories = init();

    /**
     * Computes the number of all categories and sub-categories contained in the repository
     * @return the size of the repository
     */
    int size(){
        int size = 0;
        for(Category category : categories){
            size += category.size();
        }
        return size;
    }

    /**
     * Computes The maximum depth of a category. The maximum depth is the size of longest path from the current category to a leaf one
     * If the repository is empty, the depth is 0
     * @return the maximum depth of the repository
     */
    int maxDepth(){
        int maxDepth = 0;
        for(Category category : categories){
            maxDepth = max(maxDepth, category.maxDepth());
        }
        return maxDepth;
    }

    /**
     * Checks whether the given Category is anywhere in the repository
     * @param contained : The element we search to be contained
     * @return true if the given category is contained in the repository
     */
    boolean contains(Category contained){
        for(Category category : categories){
            if(category.contains(contained)){
                return true;
            }
        }
        return false;
    }

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

}
