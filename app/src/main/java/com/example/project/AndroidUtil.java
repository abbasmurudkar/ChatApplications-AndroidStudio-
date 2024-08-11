package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.example.project.details.UserModel;
import com.squareup.picasso.Picasso;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("username",userModel.getName());
        intent.putExtra("phone",userModel.getPhonenumber());
        intent.putExtra("userId",userModel.getUserId());

    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setName(intent.getStringExtra("username"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setPhonenumber(intent.getStringExtra("phone"));
        return userModel;
    }
    public static void setProfilespPic(Context context, Uri imageUri, ImageView imageView){
        Picasso.get().load(imageUri).into(imageView);
    }
}
