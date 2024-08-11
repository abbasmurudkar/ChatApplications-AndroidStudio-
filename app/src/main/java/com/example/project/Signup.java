package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.project.details.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import android.net.Uri;

public class Signup extends AppCompatActivity {

    FirebaseAuth auth;
    int RC_Sigin = 10;
    FirebaseDatabase db;
    GoogleSignInClient Google;
    LinearLayout layout;
    EditText User_email, psd;
    TextView txt;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        User_email = findViewById(R.id.email);
        psd = findViewById(R.id.password);
        login = findViewById(R.id.login);
        layout = findViewById(R.id.GoogleBtn);
    txt = findViewById(R.id.hello);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().
                build();
// will be done when user in main page
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Google = GoogleSignIn.getClient(this,gso);

        layout.setOnClickListener(v -> googleSignIn());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = User_email.getText().toString();
                String password = psd.getText().toString();
                if(TextUtils.isEmpty(email)){
                    User_email.setError("Full Name Is Required");
                    User_email.requestFocus();
                    return;
                }else if(password.length()<=8||TextUtils.isEmpty(password)){
                    psd.setError("Password Should Be 8 Characters Required");
                    return;
                }  else if(!isValidEmail(email)){
                    User_email.setError("Enter a valid email address");
                    User_email.requestFocus();
                }else {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if(user!=null && user.isEmailVerified()) {
                                    Toast.makeText(Signup.this, "User Logged In Successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Signup.this, MainPage.class));
                                }else{
                                    Toast.makeText(Signup.this, "Please verify your email to login.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Signup.this, "LOGIN_FAILED" + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            private boolean isValidEmail(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

    }

    private void googleSignIn(){
        Intent intent = Google.getSignInIntent();
        startActivityForResult(intent,RC_Sigin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==RC_Sigin){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }
            catch(Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    Date time = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    String formattedDate = dateFormat.format(time);
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Signup.this);
                    if (acct != null) {
                        Uri personPhoto = acct.getPhotoUrl();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Users").child(user.getUid());
                        Timestamp timestamp = new Timestamp(time);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id", user.getUid());
                        map.put("PhNo",user.getPhoneNumber());
                        map.put("Name", user.getDisplayName());
                        map.put("Email", user.getEmail());
                        map.put("Timestamp", formattedDate);
                        map.put("PhotoUrl", personPhoto.toString());
                        UserModel userModel = new UserModel(null,user.getDisplayName(),timestamp, user.getUid(), user.getEmail(), personPhoto.toString()); // PhotoUrl is empty for now
                        saveUserToFirestore(userModel);
                        myRef.setValue(map);
                    }

                    startActivity( new Intent(Signup.this, MainPage.class));
                }
                else{
                    Toast.makeText(Signup.this,"ERROR_PLEASE_PROVIDE_CORRECT_DETIALS",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void onBackPressed() {
        // Navigate to the login page
    }
    private void saveUserToFirestore(UserModel userModel) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(auth.getCurrentUser().getUid())
                .set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("hello", "User data added to Firestore successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error", "Error adding user data to Firestore", e);
                    }
                });
    }
}