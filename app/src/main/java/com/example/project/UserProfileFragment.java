package com.example.project;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class UserProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth auth;
    FirebaseDatabase database;
    TextView names,email,phonenumber,timestamps;
    ImageView img;
    private String mParam1;
    private String mParam2;
    DatabaseReference userRef;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        names = view.findViewById(R.id.textView6);
        img = view.findViewById(R.id.imageView3);
        email = view.findViewById(R.id.textView17);
        phonenumber = view.findViewById(R.id.textview14);
        timestamps = view.findViewById(R.id.textView11);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userRef = database.getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("SignupActivity", "Snapshot exists: " + snapshot.exists());
                    Log.d("SignupActivity", "Snapshot value: " + snapshot.getValue());

                    if (snapshot.exists()) {
                        String name = snapshot.child("Name").getValue(String.class);
                        String Email = snapshot.child("Email").getValue(String.class);
                        String image = snapshot.child("PhotoUrl").getValue(String.class);
                        String timestamp = snapshot.child("Timestamp").getValue(String.class); // Retrieve as String
                        String ph = snapshot.child("PhoneNumber").getValue(String.class);
                        if (name != null) {
                            names.setText(name);
                        }
                        if (email != null) {
                            email.setText(Email);
                        }
                        if(image !=null){
                            Picasso.get().load(image).into(img);
                        }
                        if(timestamp!=null){
                            timestamps.setText(timestamp);
                        }else{
                            timestamps.setText("null");
                        }
                        if(ph!=null){
                            phonenumber.setText(ph);
                        }else{
                            phonenumber.setText("null");
                        }
                    } else {
                        // Handle case when user data is not found
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error

                }
            });
        }

        return view;
    }
}