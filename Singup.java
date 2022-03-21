package com.learntodroid.androidqrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Singup extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "";
    Button enregistrer, login;
    EditText email, pass, nom;
    FirebaseDatabase firebaseDatabase; // entry point
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        nom = findViewById(R.id.nom);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        enregistrer = (Button) findViewById(R.id.enregistrer);
        login = (Button) findViewById(R.id.login);


     enregistrer.setOnClickListener(this);
     login.setOnClickListener(this);


    }






    void registerUser(){
       // Toast.makeText(Singup.this,"Start Register User",Toast.LENGTH_LONG).show();
        String Email = email.getText().toString().trim();
        String Pass = pass.getText().toString().trim();
        String Nom = nom.getText().toString().trim();
        if (Email.isEmpty()){
            email.setError("email is required!");
            email.requestFocus();
            return;

        }
        if(Pass.isEmpty()){
            pass.setError("password is required!");
            pass.requestFocus();
            return;
        }
        if(Nom.isEmpty()){
            nom.setError("name is required!");
            nom.requestFocus();
            return;
        }
        auth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    User user = new User(Nom,Email,Pass);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Singup.this,"SUCCES",Toast.LENGTH_LONG).show();
                            }else if (!task.isSuccessful()) {
                                Toast.makeText(Singup.this, "NO SUCCES", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else if (!task.isSuccessful()){
                    Toast.makeText(Singup.this,"Failed",Toast.LENGTH_LONG).show();

                }
            }
        });
  return;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.enregistrer:
                registerUser();
                break;
            case R.id.login:
              //  Toast.makeText(Singup.this,"LOGIN START",Toast.LENGTH_LONG).show();
                Intent f = new Intent(Singup.this,LogintTo.class);
                startActivity(f);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
