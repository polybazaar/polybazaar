package ch.epfl.polybazaar.listing;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class StringCategory implements Category {
    private List<Category> subCategories;
    private String name;

    public StringCategory(String name){
        this.name = name;
        this.subCategories = new ArrayList<>();
    }

    @Override
    public List<Category> subCategories() {
        return subCategories;
    }

    @Override
    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }

    @Override
    public boolean equals(Category other) {
        return name.equals(other.toString());
    }

    @Override
    public int size() {
       int size = 0;
       for(Category subCategory : subCategories){
           size += subCategory.size();
       }
        return 1 + size;
    }

    @Override
    public int maxDepth() {
        int maxDepth = 1;
        for(Category subCategory : subCategories){
            maxDepth = max(maxDepth, 1+subCategory.maxDepth());
        }
        return maxDepth;
    }

    @Override
    public boolean contains(Category contained) {
        if(!equals(contained)){
            for(Category subCategory : subCategories){
                if(subCategory.contains(contained)){
                    return true;
                }
            }
            return false;
        }
        else{
            return true;
        }
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }

    @Override
    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        subCategories.remove(subCategory);
    }
}
