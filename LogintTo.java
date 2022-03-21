package com.learntodroid.androidqrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.BackgroundServiceStartNotAllowedException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LogintTo extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "";
    Button connexion, singup;
    EditText editTextTextPersonName, editTextTextPersonName2;

    FirebaseDatabase firebaseDatabase; // entry point
    DatabaseReference databaseReference;


    // firebaseDatabase = FirebaseDatabase.getInstance("https://androidstudiopfe-default-rtdb.firebaseio.com/").getReference().getDatabase();
    //  databaseReference = firebaseDatabase.getReference("login:").push();
    //  db = firebaseDatabase.getReference("cafe:").push();
    FirebaseAuth fauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logint_to);
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextTextPersonName2 = (EditText) findViewById(R.id.editTextTextPersonName2);

        fauth = FirebaseAuth.getInstance();
        singup =  findViewById(R.id.signup);
        connexion =  findViewById(R.id.connexion);
        connexion.setOnClickListener(this);
        singup.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signup:
                startActivity(new Intent(this,Singup.class));
                break;
            case R.id.connexion:
                login();
                break;
        }

    }


    void login(){
        String Email = editTextTextPersonName.getText().toString().trim();
        String Pass = editTextTextPersonName2.getText().toString().trim();
        if (Email.isEmpty()){
            editTextTextPersonName.setError("enter an email please!");
            editTextTextPersonName.requestFocus();
            return;
        }
        if(Pass.isEmpty()){
            editTextTextPersonName2.setError("enter password please!");
            editTextTextPersonName2.requestFocus();
            return;
        }

        fauth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(LogintTo.this,MainActivity.class));

                }else{
                    Toast.makeText(LogintTo.this,"Failed to Login",Toast.LENGTH_LONG).show();
                    editTextTextPersonName.requestFocus();
                    editTextTextPersonName2.requestFocus();
                }
            }
        });
    }
}




/*

        editTextTextPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String.valueOf(d.child("username").setValue(editTextTextPersonName.getText().toString()));
            }
        });
        editTextTextPersonName2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               String.valueOf( d.child("password").setValue(editTextTextPersonName2.getText().toString()));
            }
        });

*/

/*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query queryByUsername;
                queryByUsername= d.orderByChild("username").equalTo(d.child("user").toString());
                queryByUsername.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Intent intent = new Intent(LogintTo.this,MainActivity.class);
                        startActivity(intent);
                    }
                });

*/







          //      if(d.child("username").get().toString().equals(userBuffer[0])){ Intent intent = new Intent(LogintTo.this,MainActivity.class);
            //    startActivity(intent);}
              //  else{
                //    String err = "llll";
                  //  Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                    //Log.i(MainActivity.class.getSimpleName(), err+d.child("username").get().toString());




