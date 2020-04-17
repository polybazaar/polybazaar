package ch.epfl.polybazaar;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryRepository;
import ch.epfl.polybazaar.category.NodeCategory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CategoryRepositoryTest{

    @BeforeClass
    public static void setupCategoryRepository(){
        Category books = new NodeCategory("Book");
        books.addSubCategory(new NodeCategory("Engineering"));
        books.addSubCategory(new NodeCategory("Cooking"));

        Category multimedia = new NodeCategory("Multimedia");
        multimedia.addSubCategory(new NodeCategory("Television"));
        multimedia.addSubCategory(new NodeCategory("Computer"));

        Category videoGames = new NodeCategory("Video games");
        videoGames.addSubCategory(new NodeCategory("Nintendo Switch"));
        videoGames.addSubCategory(new NodeCategory("Playstation 4"));
        videoGames.addSubCategory(new NodeCategory("PC"));
        videoGames.addSubCategory(new NodeCategory("XBOX One"));

        multimedia.addSubCategory(videoGames);

        CategoryRepository.categories = Arrays.asList(new NodeCategory("Furniture"), books, multimedia);
    }

    @Test
    public void testSizeIsCorrect(){
        assertThat(CategoryRepository.size(), is(12));
    }

    @Test
    public void testMaxDepthIsCorrect(){
        assertThat(CategoryRepository.maxDepth(), is(3));
    }

    @Test
    public void testContainsIsCorrect(){
        assertTrue(CategoryRepository.contains(new NodeCategory("Playstation 4")));
        assertFalse(CategoryRepository.contains(new NodeCategory("Coronavirus")));
    }
}