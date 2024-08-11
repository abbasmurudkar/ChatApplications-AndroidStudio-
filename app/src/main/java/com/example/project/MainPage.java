package com.example.project;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class MainPage extends AppCompatActivity {

    ImageView img;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    FirebaseDatabase database;
    DatabaseReference userRef;
    View Header;
    private boolean isLoggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        img = findViewById(R.id.imageMenu);
        ImageButton search = (ImageButton) findViewById(R.id.icon_Search);

        drawerLayout = findViewById(R.id.drawerlayout);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this,SearchUserActivity.class));
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setItemIconSize(40);

        NavController navController = Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        Header = navigationView.getHeaderView(0);
        TextView Username = Header.findViewById(R.id.username);
        TextView Email = Header.findViewById(R.id.email);
        ImageView User = Header.findViewById(R.id.imageView2);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); // Initialize FirebaseDatabase instance

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userRef = database.getReference("Users").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("SignupActivity", "Snapshot exists: " + snapshot.exists());
                    Log.d("SignupActivity", "Snapshot value: " + snapshot.getValue());

                    if (snapshot.exists()) {
                        String name = snapshot.child("Name").getValue(String.class);
                        String email = snapshot.child("Email").getValue(String.class);
                        String image = snapshot.child("PhotoUrl").getValue(String.class);
                        if (name != null) {
                            Username.setText(name);
                        }
                        if (email != null) {
                            Email.setText(email);
                        }
                        if(image !=null){
                            Picasso.get().load(image).resize(200, 200).into(User);
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
        Menu menu = navigationView.getMenu();
        MenuItem logoutMenuItem = menu.findItem(R.id.logout);
        MenuItem share = menu.findItem(R.id.communication);

        share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

                return false;
            }
        });
        logoutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(MainPage.this, Signup.class));
                Toast.makeText(MainPage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
        });
    }
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        isLoggedIn = currentUser != null;
    }
    @Override
    public void onBackPressed() {
        if (isLoggedIn) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Toast.makeText(this, "Press logout to exit", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}