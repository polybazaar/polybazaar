package ch.epfl.polybazaar.category;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

public abstract class CategoryRepository {
    /**
     * List containing all the categories of the repository
     */
    public static List<Category> categories = init();



    private static List<Category> init(){

        Category books = new NodeCategory("Book");
        books.addSubCategory(new NodeCategory("Engineering"));
        books.addSubCategory(new NodeCategory("Cooking"));

        Category multimedia = new NodeCategory("Multimedia");
        multimedia.addSubCategory(new NodeCategory("Television"));
        multimedia.addSubCategory(new NodeCategory("Computer"));

        Category videoGames = new NodeCategory("Video games");
        Category nintendoSwitch = new NodeCategory("Nintendo Switch");
        nintendoSwitch.addSubCategory(new NodeCategory("Games"));
        nintendoSwitch.addSubCategory(new NodeCategory("Console"));
        nintendoSwitch.addSubCategory(new NodeCategory("Controllers"));
        nintendoSwitch.addSubCategory(new NodeCategory("Docks"));
        nintendoSwitch.addSubCategory(new NodeCategory("Accessories"));
        videoGames.addSubCategory(nintendoSwitch);
        videoGames.addSubCategory(new NodeCategory("Playstation 4"));
        videoGames.addSubCategory(new NodeCategory("PC"));
        videoGames.addSubCategory(new NodeCategory("XBOX One"));

        multimedia.addSubCategory(videoGames);

        return Arrays.asList(new NodeCategory("Furniture"), books, multimedia);
    }

}
