package com.example.lattice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {

    private List<ChatModel> chatModelList;

    public ChatAdapter(List<ChatModel> chatModelList) {
        this.chatModelList = chatModelList;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        holder.setData(chatModelList.get(position).getMessage(), chatModelList.get(position).getSendReceive());
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView mesg;

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mesg = itemView.findViewById(R.id.chat_text);
        }

        @SuppressLint("SetTextI18n")
        public void setData(String message, int sendReceive) {

            if (sendReceive == ChatModel.SEND) {
                mesg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                mesg.setText(message);
            } else if (sendReceive == ChatModel.RECEIVE) {
                mesg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                mesg.setText(message);
            }


        }
    }
}
