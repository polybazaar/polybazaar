package ch.epfl.polybazaar.category;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.max;

public class NodeCategory implements Category {
    private List<Category> subCategories;
    private String name;

    public NodeCategory(String name){
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

    @Override
    public Category getSubCategoryContaining(Category contained) {
        for(Category subCategory : subCategories){
            if(subCategory.contains(contained)){
                return subCategory;
            }
        }
        return null;
    }

    @Override
    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        subCategories.remove(subCategory);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
        return name;
    }
}
