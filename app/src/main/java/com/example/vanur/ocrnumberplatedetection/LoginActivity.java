package com.example.vanur.ocrnumberplatedetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.Toll.Activity.MainActivity;
import com.example.vanur.ocrnumberplatedetection.Toll.Activity.TollRegister;
import com.example.vanur.ocrnumberplatedetection.User.Activity.UserHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.maksim88.passwordedittext.PasswordEditText;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText user_id;
        final PasswordEditText input_password;
        TextView txt_sign_up;
        final CardView toll, user;
        final boolean[] toll_select = {false};
        final boolean[] user_select = {false};
        final Button btn_sign_in;
        auth = FirebaseAuth.getInstance();


        user_id = (EditText) findViewById(R.id.user_id);
        input_password = (PasswordEditText) findViewById(R.id.input_password);
        txt_sign_up = (TextView) findViewById(R.id.txt_sign_up);
        toll = (CardView) findViewById(R.id.toll);
        user = (CardView) findViewById(R.id.user);
        btn_sign_in = (Button) findViewById(R.id.sign_in_btn);

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserType.class));
            }
        });
        toll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toll_select[0] = true;
                toll.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                user.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                btn_sign_in.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_select[0] = true;
                user.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                toll.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                btn_sign_in.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_id.getText().toString().equals("")||input_password.getText().toString().equals("") || toll_select[0]==false&&user_select[0]==false){
                    Snackbar snackbar = Snackbar.make(v, "Please fill all the fields to proceed", Snackbar.LENGTH_LONG);
                    View sb = snackbar.getView();
                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    snackbar.show();
                }
                else{
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Signing in");
                    progressDialog.show();
                    String email=user_id.getText().toString();
                    if(toll_select[0]==true)
                    {
                        email +="@toll.com";
                        auth.signInWithEmailAndPassword(email, input_password.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        progressDialog.dismiss();
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "There was an error, please try again", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                    }
                    else if(user_select[0]==true) {
                        //email += "@user.com";
                        auth.signInWithEmailAndPassword(email, input_password.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "There was an error, please try again", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, UserHome.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }

                }
                }

        });

    }
}
