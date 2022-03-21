package com.learntodroid.androidqrcodescanner;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dashboard extends AppCompatActivity {

    FirebaseDatabase fbd;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dbref = FirebaseDatabase.getInstance("https://androidstudiopfe-default-rtdb.firebaseio.com/").getReference();





    }



}