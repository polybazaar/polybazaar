package ch.epfl.polybazaar.category;

import androidx.annotation.NonNull;

import java.util.List;

public interface Category {
    @NonNull
    /**
     * @return the String representation of a category
     */
    String toString();

    /**
     * Add a new sub-category to the current category
     * @param subCategory : the sub-category to add
     */
    void addSubCategory(Category subCategory);

    /**
     * Remove a sub-category from the current category
     * @param subCategory : the sub-category to remove
     */
    void removeSubCategory(Category subCategory);

    /**
     * @return the list of all sub-categories of the current category
     */
    List<Category> subCategories();

    /**
     * Indicates if the current category has sub-categories or if it is a "leaf" category
     * @return true if the current category has sub-categories
     */
    boolean hasSubCategories();

    /**
     * Compares two categories
     * @param other : The other category to compare the current one to
     * @return true if the two categories are considered equal
     */
    boolean equals(Category other);

    /**
     * Computes the size of the category. The size of a category is 1 + the size of all of its sub-categories
     * @return : the size of the current category
     */
    int size();

    /**
     * Computes The maximum depth of a category is the size of longest path from the current category to a leaf one
     * If there are no sub-categories, the maximum depth is 1
     * @return the maximum depth of the current category
     */
    int maxDepth();

    /**
     * Checks whether the given Category is anywhere in a sub-category or the current category itself
     * @param contained : The element we search to be contained
     * @return true if the given category is contained in the current one
     */
    boolean contains(Category contained);

    /**
     * @param searched the category we want the index of
     * @return the index of the position of the searched category in the repository. If not found returns -1
     */
    int indexOf(Category searched);


    Category getSubCategoryContaining(Category contained);
}
