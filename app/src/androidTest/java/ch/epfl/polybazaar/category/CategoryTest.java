package ch.epfl.polybazaar.category;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryTest {

    @Test
    public void RootCategoryContainsAllCategories(){
        Category root = new RootCategory();
        List<Category> categories = root.subCategories();
        assertEquals(categories,CategoryRepository.categories);
    }

    @Test
    public void getAllSubCategoriesOnLeafReturnEmpty(){
        Category category = new StringCategory("leafCategory");
        assertEquals(CategoryRepository.getAllSubCategories(category),new ArrayList<>());

    }
    @Test
    public void getAllSubCategoriesReturnALlContainedCategories(){
        Category mainCategory = new StringCategory("mainCategory");
        Category firstSubCategory = new StringCategory("firstSubCategory");
        Category secondSubcategory = new StringCategory("secondSubCategory");
        firstSubCategory.addSubCategory(secondSubcategory);
        mainCategory.addSubCategory(firstSubCategory);
        List<Category> subCategories = new ArrayList<Category>();
        subCategories.add(firstSubCategory);
        subCategories.add(secondSubcategory);
        assertEquals(CategoryRepository.getAllSubCategories(mainCategory),subCategories);
    }
    @Test
    public void coherentWithSizeFromRepo(){
        Category root = new RootCategory();
        assertEquals(CategoryRepository.getAllSubCategories(root).size(),CategoryRepository.size());
        assertEquals(root.size(),CategoryRepository.size());
    }

    @Test
    public void addAndDeleteCategoryInRootWorks(){

        Category root = new RootCategory();
        Category subCategory = new StringCategory("subCategory");
        root.addSubCategory(subCategory);
        assert(root.contains(subCategory));
        root.removeSubCategory(subCategory);
        assert(!root.contains(subCategory));
    }
}
