package ch.epfl.polybazaar.category;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.max;

public class RootCategory implements Category {
    private static RootCategory root = null;
    private List<Category> nodes;

    /**
     * Get the singleton root category. As the categories are read from a JSON file, it needs a context to access it
     * @param context : application context
     * @return the root category, if JSON couldn't be parsed, return null
     */
    public static RootCategory getInstance(Context context) {
        if(root == null){
            try {
                root = new RootCategory(context);
            }
            catch (Exception e){
                root = null;
            }
        }
        return root;
    }

    private RootCategory(Context context) throws IOException, JSONException {
        nodes = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("categories.json")));
            String line = reader.readLine();
            StringBuilder jsonBuilder = new StringBuilder();

            while(line != null){
                jsonBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            String json = jsonBuilder.toString();
            JSONObject jsonObject = new JSONObject(new JSONTokener(json));
            traverse(jsonObject, this);
    }


    private void traverse(Object json, Category cat) throws JSONException {
        if(json instanceof JSONObject){
            JSONObject jsonObject = (JSONObject)json;
            Iterator<String> keys = jsonObject.keys();
            while(keys.hasNext()){
                String name = keys.next();
                NodeCategory node = new NodeCategory(name);
                cat.addSubCategory(node);

                JSONArray arr = new JSONArray(jsonObject.getJSONArray(name).toString());
                traverse(arr, node);
            }
        }

        else if(json instanceof JSONArray){
            JSONArray arr = (JSONArray)json;
            for(int i = 0 ; i<arr.length() ; i++){
                if(!arr.get(i).toString().substring(0,1).equals("{")){
                    NodeCategory node = new NodeCategory(arr.get(i).toString());
                    cat.addSubCategory(node);
                    traverse(arr.get(i), node);
                }
                else{
                    traverse(arr.get(i), cat);
                }
            }
        }
    }

    @Override
    public void addSubCategory(Category subCategory) {
        nodes.add(subCategory);
    }

    @Override
    public void removeSubCategory(Category subCategory) {
        nodes.remove(subCategory);
    }

    @Override
    public List<Category> subCategories() {
        return nodes;
    }

    @Override
    public boolean hasSubCategories() {
        return !nodes.isEmpty();
    }

    @Override
    public boolean equals(Category other) {
        return false;
    }

    @Override
    public int size(){
        int size = 0;
        for(Category category : nodes){
            size += category.size();
        }
        return size;
    }

    @Override
    public int maxDepth(){
        int maxDepth = 0;
        for(Category category : nodes){
            maxDepth = max(maxDepth, category.maxDepth());
        }
        return maxDepth;
    }

    @Override
    public boolean contains(Category contained){
        for(Category category : nodes){
            if(category.contains(contained)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Category getSubCategoryContaining(Category contained) {
        for(Category category : nodes){
            if(category.contains(contained)){
                return category;
            }
        }
        return null;
    }

    @Override
    public int indexOf(Category searched){
        return nodes.indexOf(searched);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String treeRepresentation(int depth){
        StringBuilder repr = new StringBuilder();
        nodes.forEach(category -> repr.append(category.treeRepresentation(depth+1) + "\n"));
        return repr.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
        return "Root category";
    }
}
