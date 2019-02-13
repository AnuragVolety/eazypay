package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanur.ocrnumberplatedetection.LoginActivity;
import com.example.vanur.ocrnumberplatedetection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TollProfileActivity extends AppCompatActivity {

    public static FirebaseUser user;
    TextView tollName, phone, details;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
    String key,value;
    Button scan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toll_profile);
        tollName = (TextView) findViewById(R.id.tollName);
        phone = (TextView) findViewById(R.id.phone);
        details = (TextView) findViewById(R.id.details);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getEmail().split("@")[0];
        scan = (Button) findViewById(R.id.scan);

        Log.e( "onCreate: ",userid);
        final String[] details1 = new String[1];
        final String[] phone1 = new String[1];
        Query queryToll = reference.child("Tolls").child(userid.toUpperCase());
        queryToll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    key = snapshot.getKey();
                    value = (String) snapshot.getValue();
                    Log.e("onDataChange: 1233", key + ": " + value);

                    if(key.equals("details")){
                        details1[0] = (String) snapshot.getValue();
                    }
                    if(key.equals("phone")) {
                        phone1[0] = (String) snapshot.getValue();
                        Log.e("onDataChange: 1233", key + ": " + phone1[0]);
                    }
                    if(key.equals("state")){
                        details1[0]+="\n"+(String) snapshot.getValue();
                    }
                    if(key.equals("tollPlazaName")){
                        tollName.setText((String) snapshot.getValue());
                    }
                }
                details.setText(details1[0]);
                phone.setText(phone1[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TollProfileActivity.this, MainActivity.class));
            }
        });
    }

}
