package ch.epfl.polybazaar.conversationOverview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;

public class ConversationOverviewRecyclerAdapter extends RecyclerView.Adapter<ConversationOverviewRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<ConversationOverview> conversationOverviews;
    private OnItemClickListener listener;
    private Map<Integer, String> viewIdToListingID;
    private Map<Integer, String> viewIdToReceiverEmail;

    public interface OnItemClickListener {
        void onItemClick(View itemView);
    }

    public ConversationOverviewRecyclerAdapter(Context context, List<ConversationOverview> conversationOverviews) {
        this.context = context;
        this.conversationOverviews = conversationOverviews;
        this.viewIdToListingID = new TreeMap<>();
        this.viewIdToReceiverEmail = new TreeMap<>();
    }

    public void setOnItemClickListener(ConversationOverviewRecyclerAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ConversationOverviewRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ConversationOverviewRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationOverviewRecyclerAdapter.ViewHolder holder, int position) {
        ConversationOverview conversationOverview = conversationOverviews.get(position);

        LiteListing.fetch(conversationOverview.getListingID()).addOnSuccessListener(result -> {
            holder.title.setText(result.getTitle());
            holder.thumbnail.setImageBitmap(convertStringToBitmap(result.getStringThumbnail()));
        });

        holder.otherUser.setText(String.format("Seller : %s", conversationOverview.getOtherUser()));
        holder.item.setTag(position);
    }

    @Override
    public int getItemCount() {
        return conversationOverviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView title, otherUser;
        public LinearLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.liteListingThumbnail);
            title = itemView.findViewById(R.id.title_conversation);
            otherUser = itemView.findViewById(R.id.seller_email);
            item = itemView.findViewById(R.id.conversationItem);

            item.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public String getListingID(Integer viewID) {
        return viewIdToListingID.get(viewID);
    }

    public String getOtherUser(Integer viewID) {
        return viewIdToReceiverEmail.get(viewID);
    }

}
