package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Bundle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.project.details.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button btn1;
    private static final String TAG ="Tag";
    EditText username,ph_no,psd,cp_psd;
    FirebaseAuth auth;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn1 = findViewById(R.id.GoSign_in);
        username = findViewById(R.id.User_name);
        ph_no = findViewById(R.id.ph_No);
        psd = findViewById(R.id.psd);
        cp_psd = findViewById(R.id.cp_psd);
        auth = FirebaseAuth.getInstance();


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString();
                String phone_number = ph_no.getText().toString();
                String password = psd.getText().toString();
                String Name = cp_psd.getText().toString();
                if(TextUtils.isEmpty(email)){
                    username.setError(" Email Is Required");
                    username.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(phone_number)){
                    ph_no.setError(" PhoneNumber Is Of 10 Digits");
                    ph_no.requestFocus();
                    return;
                }
                if(password.length()<=8||TextUtils.isEmpty(password)){
                    psd.setError("Password Should Be 8 Characters Required");
                    psd.requestFocus();
                    return;
                }
                if (!isValidEmail(email)) {
                    username.setError("Enter a valid email address");
                    username.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(Name)){
                    cp_psd.setError("Name is Required!");
                    cp_psd.requestFocus();
                    return;
                }

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"USER_CREATED",Toast.LENGTH_LONG).show();
                            Date time = new Date();
                            Timestamp timestamp = new Timestamp(time);
                            FirebaseUser user = auth.getCurrentUser();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            String formattedDate = dateFormat.format(time);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(user.getUid());
                            HashMap<String ,Object> map = new HashMap<>();
                            String hashedPassword = hashPassword(password); // Hash the password
                            map.put("PhoneNumber",phone_number);
                            map.put("Email",email);
                            map.put("Password",hashedPassword);
                            map.put("Timestamp",formattedDate);
                            Uri personPhoto = Uri.parse("https://t3.ftcdn.net/jpg/05/53/79/60/360_F_553796090_XHrE6R9jwmBJUMo9HKl41hyHJ5gqt9oz.jpg");
                            map.put("Name",Name);
                            map.put("PhotoUrl", personPhoto.toString());
                            myRef.setValue(map);
                            FirebaseUser fuser = auth.getCurrentUser();
                            UserModel userModel = new UserModel(phone_number,Name,timestamp, fuser.getUid(), email, ""); // PhotoUrl is empty for now
                            saveUserToFirestore(userModel);
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verification_Email Has Been Sent",Toast.LENGTH_LONG).show();
                                    startSecondActivity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"ON FAILURE EMAIL NOT SEND"+e.getMessage());
                                }
                            });
                        }else{
                            Toast.makeText(Register.this,"USER_NOT_CREATED"+task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }


                });
            }

            private boolean isValidEmail(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
    }

    private void saveUserToFirestore(UserModel userModel) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(auth.getCurrentUser().getUid())
                .set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User data added to Firestore successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding user data to Firestore", e);
                    }
                });
    }


    private void startSecondActivity(){
        Intent intent = new Intent(Register.this,Signup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }
    public void onBackPressed() {
        // Navigate to the login page
        startActivity(new Intent(this, Signup.class));
        finish();
    }
    }
