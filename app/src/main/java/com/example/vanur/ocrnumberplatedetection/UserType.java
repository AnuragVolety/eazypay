package com.example.vanur.ocrnumberplatedetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.vanur.ocrnumberplatedetection.Toll.Activity.TollRegister;
import com.example.vanur.ocrnumberplatedetection.User.Activity.NewUserActivity;

public class UserType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        android.support.v7.widget.CardView user = (android.support.v7.widget.CardView) findViewById(R.id.user);
        android.support.v7.widget.CardView toll = (android.support.v7.widget.CardView) findViewById(R.id.toll);
        toll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserType.this, TollRegister.class));
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserType.this, NewUserActivity.class));
            }
        });
    }
}
