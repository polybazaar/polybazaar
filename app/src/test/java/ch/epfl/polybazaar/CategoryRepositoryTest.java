package ch.epfl.polybazaar;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.listing.Category;
import ch.epfl.polybazaar.listing.CategoryRepository;
import ch.epfl.polybazaar.listing.StringCategory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CategoryRepositoryTest{

    @BeforeClass
    public static void setupCategoryRepository(){
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

        CategoryRepository.categories = Arrays.asList(new StringCategory("Furniture"), books, multimedia);
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
        assertTrue(CategoryRepository.contains(new StringCategory("Playstation 4")));
        assertFalse(CategoryRepository.contains(new StringCategory("Coronavirus")));
    }
}