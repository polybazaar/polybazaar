package ch.epfl.polybazaar;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.StringCategory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StringCategoryTest{
    private static Category furniture;
    private static Category books;
    private static Category multimedia;
    private static Category toRemove;
    private static Category doNotUse;
    private static Category videoGames;
    @BeforeClass
    public static void setupCategories(){
        furniture = new StringCategory("Furniture");
        toRemove = new StringCategory("To remove");
        doNotUse = new StringCategory("Do not use");
        doNotUse.addSubCategory(toRemove);

        books = new StringCategory("Book");
        books.addSubCategory(new StringCategory("Engineering"));
        books.addSubCategory(new StringCategory("Cooking"));

        multimedia = new StringCategory("Multimedia");
        multimedia.addSubCategory(new StringCategory("Television"));
        multimedia.addSubCategory(new StringCategory("Computer"));

        videoGames = new StringCategory("Video games");
        videoGames.addSubCategory(new StringCategory("Nintendo Switch"));
        videoGames.addSubCategory(new StringCategory("Playstation 4"));
        videoGames.addSubCategory(new StringCategory("PC"));
        videoGames.addSubCategory(new StringCategory("XBOX One"));

        multimedia.addSubCategory(videoGames);
    }
    @Test
    public void testToStringIsCorrect(){
        assertThat(books.toString(), is("Book"));
    }

    @Test
    public void testHasSubCategoriesIsCorrect(){
        assertFalse(furniture.hasSubCategories());
        assertTrue(multimedia.hasSubCategories());
    }

    @Test
    public void testAddSubCategoryIsCorrect(){
        Category nes = new StringCategory("Nintendo NES");
        doNotUse.addSubCategory(nes);
        assertTrue(doNotUse.subCategories().contains(nes));;
    }

    @Test
    public void testDeleteSubCategoryIsCorrect(){
        doNotUse.removeSubCategory(toRemove);
        assertFalse(doNotUse.subCategories().contains(toRemove));
    }

    @Test
    public void testEqualsIsCorrect(){
        assertTrue(books.equals(new StringCategory("Book")));
        assertFalse(books.equals(new StringCategory("Phone")));
    }

    @Test
    public void testSizeIsCorrect(){
        assertThat(furniture.size(), is(1));
        assertThat(multimedia.size(), is(8));
    }

    @Test
    public void testMaxDepthIsCorrect(){
        assertThat(furniture.maxDepth(), is(1));
        assertThat(multimedia.maxDepth(), is(3));
    }

    @Test
    public void testContainsIsCorrect(){
        assertTrue(multimedia.contains(new StringCategory("Nintendo Switch")));
        assertTrue(multimedia.contains(multimedia));
        assertFalse(multimedia.contains(books));
    }
}