package com.example.project;

import android.view.Display;

public class Model {
    private String imageURL;
    public Model(){

    }
    public Model(String imageURL){
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
