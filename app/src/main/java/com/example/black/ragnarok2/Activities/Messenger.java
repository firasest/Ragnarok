package com.example.black.ragnarok2.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.black.ragnarok2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class Messenger extends AppCompatActivity {
    private ImageView loginImage;
    FirebaseAuth fAuth;
    TextView name,email;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_with_drawer);
        FirebaseUser user = fAuth.getCurrentUser();
        loginImage = findViewById(R.id.imageView);
        loginImage.setImageURI(user.getPhotoUrl());
        name=findViewById(R.id.nav_header_title);
        email=findViewById(R.id.textView);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

    }
}
