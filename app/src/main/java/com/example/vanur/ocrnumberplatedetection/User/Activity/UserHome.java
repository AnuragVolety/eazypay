package com.example.vanur.ocrnumberplatedetection.User.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vanur.ocrnumberplatedetection.LoginActivity;
import com.example.vanur.ocrnumberplatedetection.R;
import com.example.vanur.ocrnumberplatedetection.Toll.Activity.TollProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<FireModel> list;
    RecyclerView recycle;
    ProgressBar progressBar;
    FirebaseUser user;
    Button button;
    TextView check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Log.e("REG_TOKEN", FirebaseInstanceId.getInstance().getToken());
        recycle = (RecyclerView)  findViewById(R.id.list);
        check = (TextView) findViewById(R.id.check);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Payments");
        Log.e( "onCreate: ", ""+myRef);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.e( "onCreate: ",""+user.getEmail() );
        }
        progressBar = (ProgressBar) findViewById(R.id.progress);
        button = (Button) findViewById(R.id.button123);
        progressBar.setVisibility(View.VISIBLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<FireModel>();
                boolean search =false;
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    FireModel value = dataSnapshot1.getValue(FireModel.class);
                    FireModel fire = new FireModel();
                    String tollName = null;
                    String transId = value.getTransId();
                    fire.setTransId(transId);
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                        Log.e( "onDataChange: ", dataSnapshot2.getKey());
                        if(search){
                            for (DataSnapshot dataSnapshot3: dataSnapshot1.getChildren()){
                                if(dataSnapshot3.getKey().equals("toll")){
                                    fire.setTollName((String) dataSnapshot3.getValue());
                                }
                                else if(dataSnapshot3.getKey().equals("date")){
                                    fire.setDate((String) dataSnapshot3.getValue());
                                }
                                else if(dataSnapshot3.getKey().equals("xAmount")){
                                    fire.setxAmount((String) dataSnapshot3.getValue());
                                }
                            }
                            list.add(fire);
                            button.performClick();
                            Log.e("onDataChange: ",""+fire.getTollName()+" "+ fire.getTransId() );
                            search=false;
                            break;
                        }
                        if(dataSnapshot2.getKey().equals("commuter")){
                            if(user==null && !((String) dataSnapshot2.getValue()).equals(NewUserActivity.email_of_user ))
                            {
                                search = false;
                                break;
                            }
                            else{
                                search=true;
                            }
                            if(user!=null && !((String) dataSnapshot2.getValue()).equals(user.getEmail() )){
                                search = false;
                                break;
                            }
                            else{
                                search=true;
                            }
                        }

                    }
                    if(dataSnapshot1.getChildrenCount()==0){
                        recycle.setVisibility(View.GONE);
                        check.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("onCancelled: ", "Failed to read value");
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,UserHome.this);
                RecyclerView.LayoutManager recycleManager = new LinearLayoutManager(UserHome.this);
                recycle.setLayoutManager(recycleManager);
                recycle.setItemAnimator(new DefaultItemAnimator());
                progressBar.setVisibility(View.GONE);
                Log.e( "onCreate: ","12"+recyclerAdapter );
                recycle.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                recycle.setAdapter(recyclerAdapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_comm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserHome.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if(id == R.id.profile){
            Intent intent =new Intent(UserHome.this,CommuterProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
