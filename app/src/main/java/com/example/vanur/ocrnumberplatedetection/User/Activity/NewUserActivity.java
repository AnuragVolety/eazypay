package com.example.vanur.ocrnumberplatedetection.User.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vanur.ocrnumberplatedetection.LoginActivity;
import com.example.vanur.ocrnumberplatedetection.MyFirebaseInstanceIDService;
import com.example.vanur.ocrnumberplatedetection.R;
import com.example.vanur.ocrnumberplatedetection.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.vanur.ocrnumberplatedetection.Toll.Activity.TollRegister.hideKeyboard;

public class NewUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private EditText name, regNo, phone, email;
    public static String email_of_user;
    private Button go;
    String vehicle_type="";
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    com.maksim88.passwordedittext.PasswordEditText password;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String MyPreferences = "MyPreferences";

    private static final String URL_REGISTER_DEVICE = "http://192.168.43.208/firebasemesaging/RegisterDevice.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        mAuth = FirebaseAuth.getInstance();
        findId();
        spinner.setOnItemSelectedListener(this);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(NewUserActivity.this);
                if(name.getText().toString().equals("") ||regNo.getText().toString().equals("")||password.getText().toString().equals("")){
                    Snackbar snackbar = Snackbar.make(v, "Please fill the fields to proceed", Snackbar.LENGTH_LONG);
                    View sb = snackbar.getView();
                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    snackbar.show();
                }
                else{
                    final ProgressDialog progressDialog = new ProgressDialog(NewUserActivity.this);
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Signing in");
                    progressDialog.show();
                    String user_id = email.getText().toString();
//                    editor = preferences.edit();
//                    editor.putString("user_id",user_id);
//                    editor.commit();
                    mAuth.createUserWithEmailAndPassword(user_id,password.getText().toString()).addOnCompleteListener(NewUserActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            createUser();

                            Toast.makeText(NewUserActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewUserActivity.this,UserHome.class);
                            progressDialog.dismiss();
                            //intent.putExtra("user",firebaseUser);
                            startActivity(intent);

                        }
                    });
                }
            }
        });
    }

    private void sendTokenToServer() {


        final String email_server = email.getText().toString();
        new MyFirebaseInstanceIDService().onTokenRefresh();
        final String token = MyFirebaseInstanceIDService.recentToken;
        Log.e( "sendTokenToServer: ", token);
        final String name_server = name.getText().toString();
        final String phone_server = phone.getText().toString();
        final String veh_type_server = vehicle_type;
        final String reg_no_server = regNo.getText().toString();


        if (token == null) {
            Toast.makeText(this, "Token not generated, please try again", Toast.LENGTH_LONG).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(NewUserActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e( "onResponse: ", "Connected");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewUserActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e( "onResponse: ", "Not Connected");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email_server);
                params.put("token", token);
                params.put("name", name_server);
                params.put("phone", phone_server);
                params.put("vehicle", veh_type_server);
                params.put("registration", reg_no_server);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void createUser() {
        UserClass user = new UserClass();
        user.setUserId(regNo.getText().toString());
        user.setName(name.getText().toString());
        user.setMobile(phone.getText().toString());
        user.setEmail(email.getText().toString());
        email_of_user = email.getText().toString();
        user.setVehicleType(vehicle_type);
        user.setToken(SharedPrefManager.getInstance(this).getDeviceToken());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        //ref.child(firebaseUser.getUid()).child("Info").push().setValue(user);
        ref.child(regNo.getText().toString()).setValue(user);
        sendTokenToServer();

    }

    private void findId() {
        spinner = (Spinner) findViewById(R.id.spinner);
        name = (EditText) findViewById(R.id.name);
        regNo = (EditText) findViewById(R.id.regNo);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        go = (Button) findViewById(R.id.button2);
        password = (com.maksim88.passwordedittext.PasswordEditText) findViewById(R.id.password);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                vehicle_type = "Car/Jeep/Van";
                break;
            case 1:
                vehicle_type = "LCV";
                break;
            case 2:
                vehicle_type = "Bus/Truck";
                break;
            case 3:
                vehicle_type = "Upto 3 axle Vehicle";
                break;
            case 4:
                vehicle_type = "4 to 6 axle";
                break;
            case 5:
                vehicle_type = "HCM/EME";
                break;
            case 6:
                vehicle_type = "7 or more Axle";
                break;

        }
        Toast.makeText(NewUserActivity.this, "Selected Vehicle Type is " + vehicle_type, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
