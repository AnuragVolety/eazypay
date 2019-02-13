package com.example.vanur.ocrnumberplatedetection;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.Toll.Activity.MainActivity;
import com.example.vanur.ocrnumberplatedetection.Toll.Activity.TollConfirmationActivity;
import com.example.vanur.ocrnumberplatedetection.User.Activity.NewUserActivity;
import com.example.vanur.ocrnumberplatedetection.User.Activity.NotificationActivity;
import com.example.vanur.ocrnumberplatedetection.User.Activity.UserHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {

    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new MyFirebaseInstanceIDService().onTokenRefresh();
        //Log.e("onCreate: ",FirebaseInstanceId.getInstance().getToken() );
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        //FirebaseAuth.getInstance().signOut();
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2 * 1000);
                    if(getIntent().getExtras()!=null && getIntent().getExtras().size()==11){
                        Log.e( "run: ", "I got Intent" + getIntent().getExtras().size());
                        Intent intent =new Intent(SplashActivity.this, NotificationActivity.class);
                        for(String key : getIntent().getExtras().keySet()){
                            if(key.equals("number")){
                                intent.putExtra("number",getIntent().getExtras().getString(key));
                            }
                            else if(key.equals("name")){
                                intent.putExtra("name",getIntent().getExtras().getString(key));
                            }
                            else if(key.equals("tollname")){
                                intent.putExtra("tollname",getIntent().getExtras().getString(key));
                            }
                            else if(key.equals("xAmount")){
                                intent.putExtra("xAmount",getIntent().getExtras().getString(key));
                            }
                        }
                        startActivity(intent);
                        finish();
                    }

                    else{
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userid = firebaseUser.getUid();
                        String emailid = firebaseUser.getEmail();
                        Log.e( "run: ", "The userid is: " + userid+", The email id is" + firebaseUser.getEmail());
                        if(emailid.substring(emailid.lastIndexOf("@")+1).equals("toll.com"))
                        {

                            /*Query queryToll = database.child("Tolls");
                            queryToll.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String key, value;
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                                            key = snapshot1.getKey();
                                            value= String.valueOf(snapshot1.getValue());
                                            if(key.equals("status") && value.equals("No")){
                                                check = false;
                                            }
                                            else if(key.equals("status") && value.equals("Yes")){
                                                check = true;
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                            /*if(check){
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(SplashActivity.this, "Your is being verified.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SplashActivity.this, TollConfirmationActivity.class));
                                finish();
                            }*/

                        }
                        else{
                            startActivity(new Intent(SplashActivity.this, UserHome.class));
                            finish();
                        }
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    }

                } catch (Exception e) {
                }
            }
        };
        background.start();

    }
}
