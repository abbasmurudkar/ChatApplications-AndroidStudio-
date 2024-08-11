package com.example.project;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.details.ChatRoomModel;
import com.example.project.details.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;


public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {

        FirebaseUtil.getOtherUserForChatRow(model.getUserIds()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    boolean lastMessageSentByme = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                    UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getotherReferenceStorage(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            Uri uri = task.getResult();
                                            AndroidUtil.setProfilespPic(context,uri,holder.userPhotoImageView);
                                        }
                                    }
                                });


                    holder.usernameText.setText(otherUserModel.getName());
                    if(lastMessageSentByme){
                        holder.lastmessageText.setText("You: "+model.getLastMessage());
                    }else{
                        holder.lastmessageText.setText(model.getLastMessage());
                    }
                    holder.lastmessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle item click event
                            Intent intent = new Intent(context,ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastmessageText,lastmessageTime;
        ImageView userPhotoImageView;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastmessageText = itemView.findViewById(R.id.last_message_text);
            lastmessageTime = itemView.findViewById(R.id.last_message_time_text);
            userPhotoImageView = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
