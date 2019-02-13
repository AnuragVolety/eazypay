package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.LoginActivity;
import com.example.vanur.ocrnumberplatedetection.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TollConfirmationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    EditText userId;
    com.maksim88.passwordedittext.PasswordEditText passwordEditText;
    Button signIn;
    String email;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String MyPreferences = "MyPreferences";
    DatabaseReference database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toll_confirmation);
        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userId = (EditText) findViewById(R.id.user_id);
        userId.setText(""+ preferences.getString("user_id",""));
        passwordEditText = (com.maksim88.passwordedittext.PasswordEditText) findViewById(R.id.input_password);
        signIn = (Button) findViewById(R.id.sign_in_btn);
        final boolean[] check = {false};
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        Query queryToll = database.child("Tolls");
        queryToll.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key, value;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        key = snapshot1.getKey();
                        value= String.valueOf(snapshot1.getValue());
                        if(key.equals("status") && value.equals("No")){
                            check[0] = false;
                        }
                        else if(key.equals("status") && value.equals("Yes")){
                            check[0] = true;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(TollConfirmationActivity.this);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Verifying Account");
                progressDialog.show();
                email = userId.getText().toString();
                email +="@toll.com";
                if(!check[0]){
                    Toast.makeText(TollConfirmationActivity.this, "Please wait until your account is verified.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else{
                    progressDialog.setMessage("Account Verfied, signing in...");
                    auth.signInWithEmailAndPassword(email, passwordEditText.getText().toString())
                            .addOnCompleteListener(TollConfirmationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(TollConfirmationActivity.this, "There was an error, please try again", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(TollConfirmationActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(TollConfirmationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });





    }
}
