package com.example.project.details;

import com.google.firebase.Timestamp;

public class UserModel {
    private String Name;
    private String Phonenumber;
    private String Email;
    private String PhotoUrl,UserId;
    private Timestamp createdat;


    // Default constructor (required by Firestore)
    public UserModel() {
    }

    public UserModel(String phonenumber,String name,Timestamp createdat,String Userid,String email, String photoUrl ) {
        Name = name;
        Phonenumber = phonenumber;
        Email = email;
        PhotoUrl = photoUrl;
        this.createdat = createdat;
        this.UserId = Userid;
        if (Phonenumber == null || Phonenumber.isEmpty()) {
            Phonenumber = email;
        }
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public Timestamp getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Timestamp createdat) {
        this.createdat = createdat;
    }
}
