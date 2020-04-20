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

public class JSONRootCategory implements Category {
    private static JSONRootCategory root = null;
    private List<Category> nodes;

    /**
     * Get the singleton root category. As the categories are read from a JSON file, it needs a context to access it
     * @param context : application context
     * @return the root category, if JSON couldn't be parsed, return null
     */
    public static JSONRootCategory getInstance(Context context) {
        if(root == null){
            try {
                root = new JSONRootCategory(context);
            }
            catch (Exception e){
                System.out.println("BUUUUUUUUUUUUUUUUUG");
                root = null;
            }
        }
        return root;
    }

    private JSONRootCategory(Context context) throws IOException, JSONException {
        nodes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("categories.json")));

        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
                jsonBuilder.append(line).append("\n");
            }
        String json = jsonBuilder.toString();
        JSONObject jsonObject = new JSONObject(new JSONTokener(json));
        JSONToCategory(jsonObject, this);
    }


    private void JSONToCategory(Object json, Category currentLevelCategory) throws JSONException {
        if(json instanceof JSONObject){
            JSONObject jsonObject = (JSONObject)json;
            Iterator<String> keys = jsonObject.keys();
            while(keys.hasNext()){
                String name = keys.next();
                NodeCategory node = new NodeCategory(name);
                currentLevelCategory.addSubCategory(node);
                JSONArray arr = new JSONArray(jsonObject.getJSONArray(name).toString());
                JSONToCategory(arr, node);
            }
        }
        else if(json instanceof JSONArray){
            JSONArray jsonArray = (JSONArray)json;
            for(int i = 0 ; i<jsonArray.length() ; i++){
                if(!jsonArray.get(i).toString().substring(0,1).equals("{")){
                    NodeCategory node = new NodeCategory(jsonArray.get(i).toString());
                    currentLevelCategory.addSubCategory(node);
                    JSONToCategory(jsonArray.get(i), node);
                }
                else{
                    JSONToCategory(jsonArray.get(i), currentLevelCategory);
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
