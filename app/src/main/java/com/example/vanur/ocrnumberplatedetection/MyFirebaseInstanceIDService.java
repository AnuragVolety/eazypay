package com.example.vanur.ocrnumberplatedetection;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    public static String recentToken;
    private static final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        recentToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(REG_TOKEN, recentToken);
        storeToken(recentToken);
    }

    private void storeToken(String token) {
        //saving the token on shared preferences
      // SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
