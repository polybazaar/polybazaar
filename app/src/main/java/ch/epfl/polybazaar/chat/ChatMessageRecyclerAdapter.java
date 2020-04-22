package ch.epfl.polybazaar.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polybazaar.R;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<ChatMessage> messages;

    public ChatMessageRecyclerAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
        return new ChatMessageRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageRecyclerAdapter.ViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        holder.messageSender.setText(chatMessage.getSender() + " : ");
        holder.messageText.setText(chatMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageSender;
        public TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.messageSender);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}



