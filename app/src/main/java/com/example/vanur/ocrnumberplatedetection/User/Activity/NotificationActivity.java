package com.example.vanur.ocrnumberplatedetection.User.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {
    TextView text;
    Button button;
    String transId, xAmount;
    FirebaseUser user;
    String name;

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Log.e("UPI RESULT REQUEST CODE-->",""+requestCode);
            Log.e("UPI RESULT RESULT CODE-->",""+resultCode);
            Log.e("UPI RESULT DATA-->",""+data);
            String message = data.getStringExtra("response");
            transId = message.substring(message.lastIndexOf('=')+1);
            if(resultCode == -1 && !transId.equals(""))
            {
                // 200 Success
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Payments");

                Log.e("onActivityResult: ","Success" );
                Toast.makeText(this, "Payment Successful! Have a Safe Journey", Toast.LENGTH_SHORT).show();


                PaymentClass payment = new PaymentClass();
                payment.setTransId(transId);
                payment.setCommuter(user.getEmail());
                payment.setToll(name);
                payment.setDate(now());
                payment.setxAmount(xAmount);

                Log.e( "onActivityResult: ", ""+ data );
                ref.child(transId).setValue(payment);
                text.setText("Your Payment has been recorded with transaction Id "+ transId+". Have a safe journey. Please wear seat-belts and ensure you reach destination safely");
                button.setVisibility(View.GONE);

            }
            else
            {
                // 400 Failed
                Log.e("onActivityResult: ", "Failed" );
                text.setText("Your Payment has failed. Try Again");
            }


            //NotificationActivity.this.finish();
        }
        catch(Exception e){
            Log.e("Error in UPI onActivityResult->",""+e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification2);
        final String number = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");
        final String tollname = getIntent().getStringExtra("tollname");
        xAmount = getIntent().getStringExtra("xAmount");
        user = FirebaseAuth.getInstance().getCurrentUser();
        text = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.pay);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("upi://pay?pa="+number+
                        "@upi&pn="+ name+
                        "&tn="+tollname+
                        " " + name+
                        "&am="+xAmount+
                        "&cu=INR&url=https://eazypay.co"));
                Intent chooser = Intent.createChooser(intent, "Pay with...");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivityForResult(chooser, 1, null);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this,UserHome.class));
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}
