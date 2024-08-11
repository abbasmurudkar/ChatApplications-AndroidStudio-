package com.example.project;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;


public class FirebaseUtil {
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static  String currentUserId(){
        return  FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }
public static DocumentReference currentUserDetails(){
        return  FirebaseFirestore.getInstance().collection("Users").document(currentUserId());
}
public static CollectionReference allUaerCollecionReference(){
        return FirebaseFirestore.getInstance().collection("Users");
}

public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
}

public static String getChatRoomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+""+userId2;
        }else{
            return userId2+""+userId1;
        }
}
public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
}
public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
}
public static DocumentReference getOtherUserForChatRow(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
           return allUaerCollecionReference().document(userIds.get(1));
        }else{
            return allUaerCollecionReference().document(userIds.get(0));
        }
}
public static StorageReference getCurrentReferenceStorage(){
        return FirebaseStorage.getInstance().getReference().child("UsersProfile").
                child(FirebaseUtil.currentUserId());
}
    public static StorageReference getotherReferenceStorage(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("UsersProfile").
                child(otherUserId);
    }

}
