package ch.epfl.polybazaar.category;

import java.util.List;

public class RootCategory implements Category {

    private String name = "All";

    @Override
    public void addSubCategory(Category subCategory) {
        CategoryRepository.categories.add(subCategory);
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        CategoryRepository.categories.remove(subCategory);
    }

    @Override
    public List<Category> subCategories() {
        return CategoryRepository.categories;
    }

    @Override
    public boolean hasSubCategories() {
        return CategoryRepository.categories.size() > 0;
    }

    @Override
    public boolean equals(Category other) {
        return name.equals(other.toString());
    }

    @Override
    public int size() {
        int size = 0;
        for(Category category : CategoryRepository.categories){
            size +=category.size();
        }
        return size;
    }

    @Override
    public int maxDepth() {
        int maxDepth = 0;
        for(Category category : CategoryRepository.categories){
           maxDepth = (category.maxDepth() > maxDepth)? category.maxDepth(): maxDepth;
        }
        return maxDepth;
    }

    @Override
    public boolean contains(Category contained) {
        return true;
    }

    @Override
    public int indexOf(Category searched) {
        return CategoryRepository.categories.indexOf(searched);
    }

    @Override
    public Category getSubCategoryContaining(Category contained) {
        for(Category subCategory : CategoryRepository.categories){
            if(subCategory.contains(contained)){
                return subCategory;
            }
        }
        return null;
    }
    @Override
    public String toString(){
        return name;
    }

}
