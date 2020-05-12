package ch.epfl.polybazaar.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {


    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private RecyclerView categoryRecycler;
    private View fragmentView;
    private CategoryFragmentListener listener;

    private static Category category;
    private static Category selectedCategory;
    private static int backstackIndex;
    private static int containerId;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cat containing the categories to show
     * @return a list of the subcategories
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(Category cat,int containerId,int backstackIndex) {
        category = cat;
        CategoryFragment.backstackIndex = backstackIndex;
        CategoryFragment.containerId = containerId;

        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity() instanceof CategoryFragmentListener){
            listener = (CategoryFragmentListener)getActivity();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // avoid access to static variables  which causes bugs
        // when switching between fragments
        final Category currentCategory = category;
        final int containerId = CategoryFragment.containerId;
        final int backstackIndex = CategoryFragment.backstackIndex;

        fragmentView = inflater.inflate(R.layout.fragment_category, container, false);
        Button categoryButton  = fragmentView.findViewById(R.id.categoryButton);
        categoryButton.setText(currentCategory.toString());
        selectedCategory = currentCategory;

        categoryRecyclerAdapter = new CategoryRecyclerAdapter(fragmentView.getContext(),currentCategory);
        categoryRecyclerAdapter.setOnItemClickListener(view->{
            int viewPosition = (int)view.getTag();
            Category nextCategory = currentCategory.subCategories().get(viewPosition);
            //select category if leaf, show subcategories otherwise
            if(nextCategory.hasSubCategories()){

                CategoryFragment nextFrag=  CategoryFragment.newInstance(nextCategory,containerId,backstackIndex);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(containerId, nextFrag)
                        .commit();
            }else{
                //categoryButton.setText(nextCategory.toString());

            }
            categoryButton.setText(nextCategory.toString());
            selectedCategory = nextCategory;

        });

        categoryButton.setOnClickListener(view->{
            listener.onCategoryFragmentInteraction(selectedCategory);
            popBackStackTillEntry(backstackIndex);

        });

        categoryRecycler = fragmentView.findViewById(R.id.categoriesRecycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));
        categoryRecycler.setAdapter(categoryRecyclerAdapter);


        return fragmentView;
    }

    //method taken from https://stackoverflow.com/questions/41549112/android-close-all-fragments-onbackpressed
    //to remove all fragments in backstack
    public void popBackStackTillEntry(int entryIndex) {

        if (getActivity().getSupportFragmentManager() == null) {
            return;
        }
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() <= entryIndex) {
            return;
        }
        FragmentManager.BackStackEntry entry = getActivity().getSupportFragmentManager().getBackStackEntryAt(
                entryIndex);
        if (entry != null) {
            getActivity().getSupportFragmentManager().popBackStackImmediate(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }

    public interface CategoryFragmentListener{
        void onCategoryFragmentInteraction(Category category);
    }
}
