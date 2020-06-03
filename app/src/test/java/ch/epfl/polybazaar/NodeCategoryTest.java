package ch.epfl.polybazaar;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.NodeCategory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class NodeCategoryTest {
    private static Category furniture;
    private static Category books;
    private static Category multimedia;
    private static Category toRemove;
    private static Category doNotUse;
    private static Category videoGames;
    @BeforeClass
    public static void setupCategories(){
        furniture = new NodeCategory("Furniture");
        toRemove = new NodeCategory("To remove");
        doNotUse = new NodeCategory("Do not use");
        doNotUse.addSubCategory(toRemove);

        books = new NodeCategory("Book");
        books.addSubCategory(new NodeCategory("Engineering"));
        books.addSubCategory(new NodeCategory("Cooking"));

        multimedia = new NodeCategory("Multimedia");
        multimedia.addSubCategory(new NodeCategory("Television"));
        multimedia.addSubCategory(new NodeCategory("Computer"));

        videoGames = new NodeCategory("Video games");
        videoGames.addSubCategory(new NodeCategory("Nintendo Switch"));
        videoGames.addSubCategory(new NodeCategory("Playstation 4"));
        videoGames.addSubCategory(new NodeCategory("PC"));
        videoGames.addSubCategory(new NodeCategory("XBOX One"));

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
        Category nes = new NodeCategory("Nintendo NES");
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
        assertTrue(books.equals(new NodeCategory("Book")));
        assertFalse(books.equals(new NodeCategory("Phone")));
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
        assertTrue(multimedia.contains(new NodeCategory("Nintendo Switch")));
        assertTrue(multimedia.contains(multimedia));
        assertFalse(multimedia.contains(books));
    }

    @Test
    public void testDoesntContainsTheSubCategory(){
        assertNull(multimedia.getSubCategoryContaining(new NodeCategory("blablabla")));
    }
}