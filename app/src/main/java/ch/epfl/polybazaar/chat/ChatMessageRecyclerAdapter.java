package ch.epfl.polybazaar.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.UI.SubmitOffer.processOffer;
import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_ACCEPTED;
import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_MADE;
import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_PROCESSED;
import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_REFUSED;
import static ch.epfl.polybazaar.user.User.fetch;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_OFFER_PROCESSED = 3;
    private static final int VIEW_TYPE_OFFER_RECEIVED = 4;
    private static final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final int START_YEAR = 1900;

    private Context context;
    private List<ChatMessage> messages;

    public ChatMessageRecyclerAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        Account user = AuthenticatorFactory.getDependency().getCurrentUser();

        if (user == null || message.getSender().equals(user.getEmail())) {
            // If the current user is the sender of the message
            if (message.getMessage().startsWith(OFFER_PROCESSED)) {
                return VIEW_TYPE_OFFER_PROCESSED;
            }
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            if (message.getMessage().startsWith(OFFER_MADE)) {
                return VIEW_TYPE_OFFER_RECEIVED;
            }
            if (message.getMessage().startsWith(OFFER_PROCESSED)) {
                return VIEW_TYPE_OFFER_PROCESSED;
            }
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_OFFER_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_offer_received, parent, false);
            return new ReceivedOfferHolder(view);
        } else if (viewType == VIEW_TYPE_OFFER_PROCESSED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_offer_processed, parent, false);
            return new ProcessedOfferHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);

        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            ((SentMessageHolder) holder).bind(chatMessage);
        } else if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
            ((ReceivedMessageHolder) holder).bind(chatMessage);
        } else if(holder.getItemViewType() == VIEW_TYPE_OFFER_RECEIVED) {
            ((ReceivedOfferHolder) holder).bind(chatMessage);
        } else if(holder.getItemViewType() == VIEW_TYPE_OFFER_PROCESSED) {
            ((ProcessedOfferHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateSent;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            dateSent = itemView.findViewById(R.id.date_sent);
        }

        @SuppressLint("DefaultLocale")
        void bind(ChatMessage message) {
            String messageContent;
            if (message.getMessage().startsWith(OFFER_MADE)) {
                // if the message is an offer:
                messageContent = context.getString(R.string.you_made_an_offer) +
                        context.getString(R.string.currency) +
                        " " +
                        message.getMessage().replace(OFFER_MADE, "");
            } else {
                messageContent = message.getMessage();
            }
            messageText.setText(messageContent);
            setHourMessage(timeText, message);
            setDateMessage(dateSent, messages.indexOf(message));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateReceived;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            dateReceived = itemView.findViewById(R.id.date_received);
        }

        @SuppressLint("DefaultLocale")
        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            setHourMessage(timeText, message);
            setDateMessage(dateReceived, messages.indexOf(message));
            fetch(message.getSender()).addOnSuccessListener(result -> {
                if(result != null) {
                    nameText.setText(result.getNickName());
                }
            });
        }
    }

    private class ReceivedOfferHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateReceived;
        Button acceptOffer, refuseOffer;

        ReceivedOfferHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            dateReceived = itemView.findViewById(R.id.date_received);
            acceptOffer = itemView.findViewById(R.id.accept_offer_button);
            refuseOffer = itemView.findViewById(R.id.refuse_offer_button);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bind(ChatMessage message) {
            messageText.setText(
                    context.getString(R.string.purchase_offer) +
                    context.getString(R.string.currency) +
                    " " +
                    message.getMessage().replace(OFFER_MADE, ""));
            setHourMessage(timeText, message);
            setDateMessage(dateReceived, messages.indexOf(message));
            fetch(message.getSender()).addOnSuccessListener(result -> {
                if(result != null) {
                    nameText.setText(result.getNickName());
                }
            });
            acceptOffer.setOnClickListener(v -> {
                double offer = Double.parseDouble(message.getMessage().replace(OFFER_MADE, ""));
                processOffer(offer, message, OFFER_ACCEPTED);
                acceptOffer.setVisibility(View.GONE);
                refuseOffer.setVisibility(View.GONE);
            });
            refuseOffer.setOnClickListener(v -> {
                double offer = Double.parseDouble(message.getMessage().replace(OFFER_MADE, ""));
                processOffer(offer, message, OFFER_REFUSED);
                acceptOffer.setVisibility(View.GONE);
                refuseOffer.setVisibility(View.GONE);
            });
        }
    }

    private class ProcessedOfferHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateReceived;

        ProcessedOfferHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            dateReceived = itemView.findViewById(R.id.date_received);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bind(ChatMessage message) {
            String offer = message.getMessage().replace(OFFER_PROCESSED, "");
            String offerStatus = (offer.substring(0, 3).equals(OFFER_ACCEPTED)) ? "accepted" : "refused";
            String offerAmount = offer.substring(3);
            messageText.setText(context.getString(R.string.offer_for) +
                    offerAmount +
                    " was " +
                    offerStatus
                    );
            setHourMessage(timeText, message);
            setDateMessage(dateReceived, messages.indexOf(message));
            fetch(message.getSender()).addOnSuccessListener(result -> {
                if(result != null) {
                    nameText.setText(result.getNickName());
                }
            });
        }
    }

    @SuppressLint("DefaultLocale")
    private void setHourMessage(TextView timeTxt, ChatMessage message) {
        Date date = message.getTime().toDate();
        timeTxt.setText(String.format("%02dh%02d", date.getHours(), date.getMinutes()));
    }

    @SuppressLint("DefaultLocale")
    private void setDateMessage(TextView txt, int index) {
        Date date = messages.get(index).getTime().toDate();

        if(index == 0 || messages.get(index -1).getTime().toDate().getMonth() != date.getMonth() || messages.get(index -1).getTime().toDate().getDate() != date.getDate()) {
            txt.setVisibility(View.VISIBLE);
            txt.setText(String.format("%d %s %d", date.getDate(), monthNames[date.getMonth()], date.getYear() + START_YEAR));
        }
    }
}



