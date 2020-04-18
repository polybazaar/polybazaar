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

    public static final MockRootCategory getInstance(){
        return MOCK_ROOT_CATEGORY;
    }

    private MockRootCategory(){
        onlyCategory = new NodeCategory("Others");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public String treeRepresentation(int depth) {
        return onlyCategory.toString();
    }

    @Override
    public void addSubCategory(Category subCategory) {
        throw new IllegalStateException("Cannot add a category to the mock");
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        throw new IllegalStateException("Cannot remove a category to the mock");
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
        return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int maxDepth() {
        return 1;
    }

    @Override
    public boolean contains(Category contained) {
        return contained.equals(onlyCategory);
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
            return null;
        }
    }
}
