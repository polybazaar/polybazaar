package ch.epfl.polybazaar.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.TreeMap;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.litelisting.LiteListing;


/**
 * Create the basic adapter extending from RecyclerView.Adapter
 */
public class LiteListingAdapter extends
        RecyclerView.Adapter<LiteListingAdapter.ViewHolder> {

    // Store a member variable for the litelistings and for the map <view ID, listing ID>
    private List<LiteListing> liteListingList;
    private TreeMap<Integer, String> viewIDtoListingIDMap;

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Provide a direct reference to each of the views within a data item
     * Used to cache the views within the item layout for fast access
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView priceView;


        // Constructor that accepts the entire item row and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleView = itemView.findViewById(R.id.liteListingTitle);
            priceView = itemView.findViewById(R.id.liteListingPrice);

            // Setup the click listener
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        listener.onItemClick(titleView);
                    }
                }
            });

        }
    }

    // Pass in the litelisting array into the constructor
    public LiteListingAdapter(List<LiteListing> liteListingList) {
        this.liteListingList = liteListingList;
        viewIDtoListingIDMap = new TreeMap<>();
    }


    // Inflating a layout from XML and returning the holder
    public LiteListingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View liteListingView = inflater.inflate(R.layout.item_litelisting, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(liteListingView);
        return viewHolder;
    }


    // Populating data into the item through holder
    public void onBindViewHolder(LiteListingAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        LiteListing liteListing = liteListingList.get(position);

        // Set item views based on views and data model
        TextView textView = viewHolder.titleView;
        textView.setText(liteListing.getTitle());
        textView.setId(View.generateViewId());
        viewIDtoListingIDMap.put(textView.getId(), liteListing.getListingID()); // update map <view ID, listing ID>



        TextView priceView = viewHolder.priceView;
        priceView.setText(liteListing.getPrice());
        priceView.setId(View.generateViewId());
    }

    // Returns the total count of items in the list
    public int getItemCount() {
        return liteListingList.size();
    }

    /**
     * Returns the listing ID corresponding to the view ID that displays the liteListing title
     * @param viewID ID of the view containing the litelisting title in graphical display
     * @return the corresponding liteListing ID
     */
    public String getListingID(Integer viewID) {
        return viewIDtoListingIDMap.get(viewID);
    }

}