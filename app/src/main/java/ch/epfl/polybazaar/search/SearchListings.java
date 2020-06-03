package ch.epfl.polybazaar.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.NodeCategory;
import ch.epfl.polybazaar.category.RootCategoryFactory;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.UI.SalesOverview.displaySavedListings;

public class SearchListings extends AppCompatActivity {

    public final String SEARCH_QUERY = "searchQuery";
    Map<String, String> searchListingTitleMap;
    ArrayList<String> sortedIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
        else {
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                Category searchCategory = findCategory(extras.getString("category"),getApplicationContext());
                performCategorySearch(searchCategory);
            }else{
                startActivity(new Intent(SearchListings.this, SalesOverview.class));
            }

        }
    }

    public void performSearch(String query) {
        searchListingTitleMap = DataHolder.getInstance().getDataMap();
        sortedIDs = new ArrayList<>();
        String mQuery = query.toLowerCase();

        for (Map.Entry<String, String> entry: searchListingTitleMap.entrySet()) {
            if(entry.getValue().contains(mQuery)) {
                sortedIDs.add(entry.getKey());
            }
        }

        if(sortedIDs.isEmpty()) {
            Intent intent = new Intent(SearchListings.this, NoSearchResults.class);
            intent.putExtra(SEARCH_QUERY, query);
            startActivity(intent);
        }
        else {
            displaySavedListings(this, sortedIDs, R.string.no_match_found);
        }
    }
    public void performCategorySearch(Category category){

        List<Category> allCategories = getContainedCategories(category);
        List<Task<List<LiteListing>>> queryList = new ArrayList<>();;
        for(Category cat: allCategories){
            queryList.add(LiteListing.fetchFieldEquality("category",cat.toString()));
        }
        Tasks.<List<LiteListing>>whenAllSuccess(queryList).addOnSuccessListener(result->{
            ArrayList<String> flatList = new ArrayList<>();
            for(List<LiteListing> l : result){
                for(LiteListing listing : l){
                    flatList.add(listing.getId());
                }
            }
            //onFetchSuccess(flatList);

            displaySavedListings(this, flatList, R.string.no_match_found);
        });

    }
    // get all categories contained in the category (the category is also contained in itself)
    public static  List<Category> getContainedCategories(Category category) {
        List<Category> subcategories = new ArrayList<>();
        subcategories.add(category);
        if (category.hasSubCategories()) {
            for (Category cat : category.subCategories()) {
                subcategories.addAll(getContainedCategories(cat));
            }
        }
        return subcategories;
    }
    public static Category findCategory(String categoryName, Context context){
        RootCategoryFactory.useJSONCategory(context);
        Category cat = new NodeCategory(categoryName);
        Category foundCategory = RootCategoryFactory.getDependency();
        List<Category> catList = getContainedCategories(RootCategoryFactory.getDependency());
        for(Category c:catList){
            if (cat.equals(c)){foundCategory = c;}
        }
        return foundCategory;
    }

}
