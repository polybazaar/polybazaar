package ch.epfl.polybazaar.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewRecyclerAdapter;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {


    private ConversationOverviewRecyclerAdapter.OnItemClickListener listener;
    private Context context;
    private Category rootCategory;
    private List<Category> categories;

    public CategoryRecyclerAdapter(Context context) {
        this.context = context;
        RootCategoryFactory.useJSONCategory(context);
        rootCategory = RootCategoryFactory.getDependency();
        categories = rootCategory.subCategories();

    }




    @NonNull
    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerAdapter.ViewHolder holder, int position) {

        Category category = categories.get(position);
        holder.categoryName.setText(category.toString());
        holder.categoryName.setTag(position);
        //Toast.makeText(context, category.toString(), Toast.LENGTH_SHORT).show() ;

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);

        }
    }
}
