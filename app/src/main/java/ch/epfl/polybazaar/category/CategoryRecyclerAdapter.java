package ch.epfl.polybazaar.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewRecyclerAdapter;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {


    private Context context;
    private Category category;
    private List<Category> categories;
    private ConversationOverviewRecyclerAdapter.OnItemClickListener listener;

    public CategoryRecyclerAdapter(Context context,Category category) {
        this.context = context;
        this.category = category;
        RootCategoryFactory.useJSONCategory(context);
        categories = category.subCategories();

    }
    public void setOnItemClickListener(ConversationOverviewRecyclerAdapter.OnItemClickListener listener) {
        this.listener = listener;
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
        holder.item.setTag(position);
        holder.categoryName.setTag(position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView categoryName;
        public LinearLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            item = itemView.findViewById(R.id.categoryItem);
            item.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

        }
    }
}
