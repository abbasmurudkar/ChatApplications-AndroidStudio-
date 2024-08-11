package com.example.project;

import static android.app.Activity.*;
import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.*;

import com.example.project.details.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    UserModel userModel;

    private String mParam1;
    private Uri imgurl;
    private EditText editTextPhone, editTextEmail, editTextName;
    private TextView text;
    private Button buttonUpdate;
    private ImageView img;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;
    UserModel currentUserModel;
    private DatabaseReference userRef;
    private String mParam2;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    public static UserSettingsFragment newInstance(String param1, String param2) {
        UserSettingsFragment fragment = new UserSettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        editTextPhone = view.findViewById(R.id.editTextText3);
//        editTextEmail = view.findViewById(R.id.editTextText2);
        editTextName = view.findViewById(R.id.editTextText);
        buttonUpdate = view.findViewById(R.id.button);
        img = view.findViewById(R.id.imageView3);
        text = view.findViewById(R.id.textView6);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
                getUserData();
            }
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userRef = database.getReference("Users").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String image = snapshot.child("PhotoUrl").getValue(String.class);
                        String Username = snapshot.child("Name").getValue(String.class);
                        if(image !=null){
                            Picasso.get().load(image).resize(200, 200).into(img);
                        }
                        if(Username!=null){
                            text.setText(Username);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(),"DatabaseError"+error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,2);
            }
        });
        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==2 && resultCode == RESULT_OK && data != null){
                imgurl = data.getData();
                img.setImageURI(imgurl);
        }
    }
    void getUserData(){
        FirebaseUtil.getCurrentReferenceStorage().getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri uri = task.getResult();
                            AndroidUtil.setProfilespPic(requireContext(),uri,img);
                        }
                    }
                });
    }
    private void updateDetails() {
        String phone = editTextPhone.getText().toString().trim();
//        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        // Validate phone number
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            return;
        }
        if (phone.length() != 10) {
            editTextPhone.setError("Phone number must be 10 digits");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return;
        }
        if(imgurl != null){
            updateDetailsInDatabase(phone , name, imgurl);
        }else{
            Toast.makeText(requireContext(),"Please Select Image",Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(requireContext(), "Updating details...", Toast.LENGTH_SHORT).show();

    }

    private void updateDetailsInDatabase(String phone , String name, Uri uri) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = FirebaseUtil.getCurrentReferenceStorage().child("Avtar"+"."+getFileExtension(uri,requireContext()));
        userRef = database.getReference("Users").child(currentUser.getUid());

        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());
                        userRef.child("PhotoUrl").setValue(model.getImageURL());
                        userRef.child("Name").setValue(editTextName.getText().toString().trim());
                        userRef.child("PhoneNumber").setValue(editTextPhone.getText().toString().trim());
                        Toast.makeText(requireContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();
//                        userModel.setPhotoUrl(model.getImageURL());
//                        // Update the Firestore document with the updated UserModel object
//                        updateUserDataInFirestore(userModel);
                        editTextName.setText("");
                        editTextPhone.setText("");
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Toast.makeText(requireContext(), "Updating details in database...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"Uploading Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri uri, Context context){
        ContentResolver cr=context.getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(uri));
    }
}