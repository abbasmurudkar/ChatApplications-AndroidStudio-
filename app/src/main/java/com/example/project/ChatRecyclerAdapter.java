package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.details.ChatMessageModels;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

    public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModels, ChatRecyclerAdapter.ChatModelViewHolder> {

        Context context;

        public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModels> options, Context context) {
            super(options);
            this.context = context;
        }

        @NonNull
        @Override
        public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
            return new com.example.project.ChatRecyclerAdapter.ChatModelViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull com.example.project.ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModels model) {
           if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
               holder.leftChatLayout.setVisibility(View.GONE);
               holder.rightChatLayout.setVisibility(View.VISIBLE);
               holder.rightChat.setText(model.getMessage());
           }else{
               holder.leftChatLayout.setVisibility(View.VISIBLE);
               holder.rightChatLayout.setVisibility(View.GONE);
               holder.leftChat.setText(model.getMessage());
           }
        }

        class ChatModelViewHolder extends RecyclerView.ViewHolder {

            LinearLayout leftChatLayout,rightChatLayout;
            TextView leftChat,rightChat;

            public ChatModelViewHolder(@NonNull View itemView) {
                super(itemView);
                leftChatLayout=itemView.findViewById(R.id.left_chat_layout);
                rightChatLayout=itemView.findViewById(R.id.right_chat_layout);
                leftChat = itemView.findViewById(R.id.left_chat_textview);
                rightChat = itemView.findViewById(R.id.right_chat_textview);
               
            }
        }
    }

