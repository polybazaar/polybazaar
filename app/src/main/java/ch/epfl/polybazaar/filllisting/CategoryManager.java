package ch.epfl.polybazaar.filllisting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.NodeCategory;
import ch.epfl.polybazaar.listing.Listing;


public class CategoryManager {

    private FillListing activity;
    private Spinner categorySelector;
    private LinearLayout linearLayout;
    private Category traversingCategory;
    private List<Spinner> spinnerList;

    public CategoryManager(FillListing activity) {
        this.activity = activity;
        if (activity != null) {
            categorySelector = activity.findViewById(R.id.categorySelector);
            linearLayout = activity.findViewById(R.id.fillListingLinearLayout);
        }
    }

    public void setupSpinner(Spinner spinner, List<Category> categoryList, List<Spinner> spinnerList, Category traversingCategory){
        this.spinnerList = spinnerList;
        this.traversingCategory = traversingCategory;
        List<Category> categories = categoriesWithDefaultText(categoryList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                removeSpinnersBelow(spinner);
                Category selectedCategory = (Category)parent.getItemAtPosition(position);
                popsUpSubCategorySpinner(selectedCategory);
                linearLayout.invalidate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void removeSpinnersBelow(Spinner spinner){
        if(!spinner.equals(spinnerList.get(spinnerList.size()-1))){
            int numbersOfSpinnersToRemove = spinnerList.size() - spinnerList.indexOf(spinner) -1;
            int indexOfFirstSpinnerToRemove = linearLayout.indexOfChild(categorySelector) + spinnerList.indexOf(spinner) + 1;
            linearLayout.removeViews(indexOfFirstSpinnerToRemove, numbersOfSpinnersToRemove);
            this.spinnerList = spinnerList.subList(0, spinnerList.indexOf(spinner)+1);
        }
    }

    private void popsUpSubCategorySpinner(Category selectedCategory){
        if (selectedCategory.hasSubCategories()){
            Spinner subSpinner = new Spinner(activity.getApplicationContext());
            spinnerList.add(subSpinner);
            linearLayout.addView(subSpinner, linearLayout.indexOfChild(categorySelector)+spinnerList.size()-1);
            setupSpinner(subSpinner, categoriesWithDefaultText(selectedCategory.subCategories()), spinnerList, traversingCategory);

            Bundle bundle = activity.getIntent().getExtras();
            if(bundle != null && traversingCategory != null){
                Category editedCategory = new NodeCategory(((Listing)bundle.get("listing")).getCategory());
                subSpinner.setSelection(traversingCategory.indexOf(traversingCategory.getSubCategoryContaining(editedCategory))+1);
                this.traversingCategory = traversingCategory.getSubCategoryContaining(editedCategory);
            }
        }
    }

    public Category getTraversingCategory() {
        return traversingCategory;
    }

    public List<Spinner> getSpinnerList() {
        return spinnerList;
    }

    private List<Category> categoriesWithDefaultText(List<Category> categories){
        List<Category> categoriesWithDefText = new ArrayList<>(categories);
        categoriesWithDefText.add(0, new NodeCategory(activity.getResources().getString(R.string.default_spinner_text)));
        return categoriesWithDefText;
    }
}
