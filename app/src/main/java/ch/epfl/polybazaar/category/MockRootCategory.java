package ch.epfl.polybazaar.category;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

/**
 * The MockRootCategory mocks categories with a single category named "Others"
 */
public class MockRootCategory implements Category {
    private NodeCategory onlyCategory;
    private static final MockRootCategory MOCK_ROOT_CATEGORY = new MockRootCategory();

    /**
     * Get the singleton root category of the mock
     * @return the mock root category
     */
    public static final MockRootCategory getInstance(){
        return MOCK_ROOT_CATEGORY;
    }

    private MockRootCategory(){
        //onlyCategory = new NodeCategory("All");
        NodeCategory subcat = new NodeCategory("Multimedia");
        subcat.addSubCategory(new NodeCategory("Video games"));
        //onlyCategory.addSubCategory(subcat);
        onlyCategory= subcat;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public String treeRepresentation(int depth) {
        return onlyCategory.treeRepresentation(depth);
    }

    @Override
    public void addSubCategory(Category subCategory) {
        //throw new IllegalStateException("Cannot add a category to the mock");
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        //throw new IllegalStateException("Cannot remove a category to the mock");
    }

    @Override
    public List<Category> subCategories() {
        return Arrays.asList(onlyCategory);
    }

    @Override
    public boolean hasSubCategories() {
        return true;
    }

    @Override
    public boolean equals(Category other) {
        return onlyCategory.equals(other);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public int maxDepth() {
        return 2;
    }

    @Override
    public boolean contains(Category contained) {
        return onlyCategory.contains(contained);
    }

    @Override
    public int indexOf(Category searched) {
        if(searched.equals(onlyCategory)){
            return 0;
        }
        else{
            return -1;
        }
    }

    @Override
    public Category getSubCategoryContaining(Category contained) {
        if(contained.equals(onlyCategory)){
            return onlyCategory;
        }
        else{
            return onlyCategory.getSubCategoryContaining(contained);
        }
    }
}
