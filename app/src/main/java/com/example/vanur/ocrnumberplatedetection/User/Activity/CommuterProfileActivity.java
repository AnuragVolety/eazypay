package com.example.vanur.ocrnumberplatedetection.User.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanur.ocrnumberplatedetection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CommuterProfileActivity extends AppCompatActivity {

    TextView name, email, phone, vehicleNo, vehicleType;
    Button transaction;

    public static FirebaseUser user;
    DatabaseReference reference;
    DatabaseReference myRef;
    private String TAG = "Kaushal Army";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuter_register);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        vehicleNo = (TextView) findViewById(R.id.vehicleNo);
        vehicleType = (TextView) findViewById(R.id.vehicleType);
        transaction = (Button) findViewById(R.id.view);

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        email.setText(user.getEmail());


        Query queryUser = reference.child("Users");
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key, value;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        key = snapshot1.getKey();
                        value= String.valueOf(snapshot1.getValue());
                        if(key.equals("email") && value.equals(user.getEmail())){
                            for(DataSnapshot snapshot2: snapshot.getChildren()){
                                key = snapshot2.getKey();
                                value = String.valueOf(snapshot2.getValue());
                                if(key.equals("mobile")){
                                    phone.setText(value);
                                }
                                else if(key.equals("name")){
                                    name.setText(value);
                                }
                                else if(key.equals("userId")){
                                    vehicleNo.setText(value);
                                }
                                else if(key.equals("vehicleType")){
                                    vehicleType.setText(value);
                                }
                            }
                        }
                        else{
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommuterProfileActivity.this, UserHome.class));
            }
        });
    }
}
