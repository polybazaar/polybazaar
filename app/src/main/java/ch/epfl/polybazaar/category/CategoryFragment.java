package ch.epfl.polybazaar.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ch.epfl.polybazaar.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {


    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private RecyclerView categoryRecycler;
    private View fragmentView;


    private static Category category;

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
    public static CategoryFragment newInstance(Category cat) {
        category = cat;
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_category, container, false);
        categoryRecyclerAdapter = new CategoryRecyclerAdapter(fragmentView.getContext(),category);
        categoryRecyclerAdapter.setOnItemClickListener(view->{
            int viewPosition = (int)view.getTag();
            Category nextCategory = category.subCategories().get(viewPosition);
            CategoryFragment nextFrag=  CategoryFragment.newInstance(nextCategory);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.salesOverview_fragment_container, nextFrag)
                  .commit();
        });

        categoryRecycler = fragmentView.findViewById(R.id.categoriesRecycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));
        categoryRecycler.setAdapter(categoryRecyclerAdapter);


        return fragmentView;
    }
}
