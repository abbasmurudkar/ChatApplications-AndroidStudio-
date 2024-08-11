package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Handler;
import android.text.*;
import android.text.style.*;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

TextView txt;
private static int SPLASH_SCREEN = 5000;
ImageView image;
TextView logo,slogan;

Animation topanim,bottomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
image = findViewById(R.id.log_app);
logo = findViewById(R.id.logo);
slogan = findViewById(R.id.slogan);

image.setAnimation(topanim);
logo.setAnimation(bottomanim);
slogan.setAnimation(bottomanim);

new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        Intent intent = new Intent(MainActivity.this,Signup.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(image,"logo_image");
        pairs[1] = new Pair<View,String>(logo,"logo_text");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
        startActivity(intent,options.toBundle());
    }
},SPLASH_SCREEN);

    }

}
