package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.vanur.ocrnumberplatedetection.MyFirebaseInstanceIDService;
import com.example.vanur.ocrnumberplatedetection.MyVolley;
import com.example.vanur.ocrnumberplatedetection.R;
import com.example.vanur.ocrnumberplatedetection.User.Activity.UserClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GetUser extends AppCompatActivity {


    public static final String URL_SEND_SINGLE_PUSH = "http://192.168.43.208/firebasemesaging/sendSinglePush.php";
    OkHttpClient mClient = new OkHttpClient();
    public final String[] emailCommuter = new String[1];
    public final String[] phoneToll = new String[1];
    public final String[] tollPlazaName = new String[1];
    public final String[] vehicleType = new String[1];
    ProgressBar progressBar;
    Button scan;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    TextView vehicleXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        scan = (Button) findViewById(R.id.scan);
        vehicleXml = (TextView) findViewById(R.id.vehiclexml);
        Bundle bundle = getIntent().getExtras();
        final String vehicleNo = bundle.getString("VehicleNo");

        vehicleXml.setText(vehicleNo);
        String uid = MainActivity.user.getEmail().split("@")[0].toUpperCase();
        Query queryToll = reference.child("Tolls").child(uid);
        Log.e( "onCreate: ", uid );
        queryToll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    String value1 = (String) snapshot.getValue();
                    if(key.equals("phone")){
                        phoneToll[0] = (String) snapshot.getValue();
                        Log.e( "onDataChange: ", "phone: "+snapshot.getValue());
                    }
                    if(key.equals("tollPlazaName")) {
                        tollPlazaName[0] = (String) snapshot.getValue();
                        Log.e( "onDataChange: ", "Toll Name: "+snapshot.getValue());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Toast.makeText(this, ""+vehicleNo, Toast.LENGTH_SHORT).show();

        Query query1 = reference.child("Users").child(vehicleNo);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    String value1 = (String) snapshot.getValue();
                    //Log.e("onDataChange: ", key + ": " + value1);
                    if(key.equals("email")){
                        emailCommuter[0] = (String) snapshot.getValue();
                        Log.e("onDataChange: ", "Email: " + emailCommuter[0]);
                    }
                    if(key.equals("vehicleType") ) {
                        vehicleType[0] = (String) snapshot.getValue();
                        Log.e("onDataChange: ", "Vehicle Type: " + vehicleType[0]);
                        sendToServer();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressBar.getVisibility()==View.VISIBLE){
                    Snackbar snackbar = Snackbar.make(v, "Please wait until notification is being sent", Snackbar.LENGTH_LONG);
                    View sb = snackbar.getView();
                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    snackbar.show();
                }
                else if(progressBar.getVisibility() == View.GONE){
                    Intent intent = new Intent(GetUser.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String getCost() {
        Log.e("getCost:",vehicleType[0] );
        if(vehicleType[0].equals("Car/Jeep/Van")){
            return "65";
        }
        else if(vehicleType[0].equals("LCV")){
            return  "100";
        }
        else if(vehicleType[0].equals("Bus/Truck")){
            return "210";
        }
        else if(vehicleType[0].equals("Upto 3 Axle Vehicle")){
            return  "230";
        }
        else if(vehicleType[0].equals("4 to 6 axle")){
            return "335";
        }
        else if(vehicleType[0].equals("HCM/EME")){
            return "335";
        }
        else{
            return "405";
        }
    }


    private void sendToServer() {
        final String token = MyFirebaseInstanceIDService.recentToken;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL_SEND_SINGLE_PUSH,
                new com.android.volley.Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(GetUser.this, "Notification to commuter successfully sent",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        scan.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        scan.setText("SCAN ANOTHER VEHICLE");
                    }
                },
                new com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "EAZYPAY");
                params.put("message", "Hello, You have got a new Payment Notification");
                Log.e( "getParams: ", emailCommuter[0]+" "+phoneToll[0]+" "
                        +tollPlazaName[0]);
                params.put("email", emailCommuter[0]);
                params.put("number", phoneToll[0]);
                params.put("name", tollPlazaName[0]);
                params.put("xAmount", getCost());
                Log.e( "getParams: ", "Params are set");
                return params;
            }
        };
        Log.e( "sendToServer: ", "Ready to go to my Volley");
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
        Log.e( "sendToServer: ", "Came from my Volley");
    }
}