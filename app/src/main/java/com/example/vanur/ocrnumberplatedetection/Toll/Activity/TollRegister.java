package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.R;
import com.example.vanur.ocrnumberplatedetection.Toll.TollUserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TollRegister extends AppCompatActivity {

    private static final String TAG = "Something ";
    String stateName, tollName;
    String[] namesList;
    ArrayList<String> finalList1 = new ArrayList<>();
    JSONArray tollList1;
    TextView details;
    Button button;
    com.maksim88.passwordedittext.PasswordEditText passwordEditText;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String MyPreferences = "MyPreferences";
    String fName="", fState="", fHighway="", fSection,fDetails="";
    EditText phone;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference ref;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toll_register);
        JSONArray states = null;
        passwordEditText = (com.maksim88.passwordedittext.PasswordEditText) findViewById(R.id.input_password);
        preferences = getSharedPreferences(MyPreferences,Context.MODE_PRIVATE);
        button = (Button) findViewById(R.id.button);
        phone = (EditText)findViewById(R.id.phone);
        mAuth = FirebaseAuth.getInstance();

        try {
            Log.e(TAG, "onCreate: "+"I am here" );
            JSONObject object = new JSONObject(loadJSONFromAsset());
            states = object.getJSONArray("states");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] statesList = {"Andhra Pradesh","Assam","Bihar","Chattisgarh", "Delhi",
                "Gujarat", "Haryana", "Jammu and Kashmir","Jharkhand","Karnataka",
        "Kerala","Madhya Pradesh","Maharashtra","Meghalaya", "Orissa", "Punjab",
                "Rajashtan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "Uttarakhand", "West Bengal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,statesList);
        AutoCompleteTextView stateAutoComplete = (AutoCompleteTextView) findViewById(R.id.stateAutoComplete);
        final AutoCompleteTextView nameAutoComplete = (AutoCompleteTextView) findViewById(R.id.nameAutoComplete);
        details= (TextView) findViewById(R.id.details);
        details.setVisibility(View.GONE);
        stateAutoComplete.setThreshold(1);
        stateAutoComplete.setAdapter(adapter);
        final JSONArray finalStates = states;
        stateAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateName = parent.getItemAtPosition(position).toString();
                Toast.makeText(TollRegister.this, "Chosen State is: \t" + stateName, Toast.LENGTH_SHORT).show();
                for(int i = 0; i< finalStates.length(); i++){
                    try {
                        JSONObject c = finalStates.getJSONObject(i);
                        String state = c.getString("state");
                        if(state.equals(stateName)){
                            Log.e(TAG, "onItemClick: "+state );
                            fState =state;
                            nameAutoComplete.setText("");

                            JSONArray tollList = c.getJSONArray("Toll List");
                            tollList1 = tollList;
                            if(finalList1.size()!=0)
                            {finalList1.clear();}
                            for(int j=0; j<tollList.length();j++){
                                JSONObject d = tollList.getJSONObject(j);
                                String tollName = d.getString("Toll Plaza Name");
                                finalList1.add(tollName);
                            }
                            String[] finalList = new String[finalList1.size()];
                            finalList = finalList1.toArray(finalList);
                            ArrayAdapter<String> tollNamesAdapter = new ArrayAdapter<>(TollRegister.this,android.R.layout.simple_list_item_1,finalList);
                            nameAutoComplete.setAdapter(tollNamesAdapter);
                            details.setVisibility(View.GONE);
                            nameAutoComplete.requestFocus();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        nameAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(TollRegister.this);
                tollName = parent.getItemAtPosition(position).toString();
                Toast.makeText(TollRegister.this, "Your Toll Plaza name is: \t" + tollName, Toast.LENGTH_SHORT).show();
                for(int i =0; i<finalList1.size();i++){
                    if(tollName.equals(finalList1.get(i))){
                        try {
                            fName = tollName;
                            JSONObject d = tollList1.getJSONObject(i);
                            String nhno = d.getString("NHNo");
                            fHighway = nhno;
                            String section = d.getString("Section");
                            fSection = section;
                            fDetails = "National Highway: "+nhno +",\nSection: "+section;
                            details.setText(fDetails);
                            details.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(TollRegister.this);
                progressDialog.setTitle("Creating account, please wait...");
                progressDialog.show();
                hideKeyboard(TollRegister.this);
                if(fName.equals("") ||fState.equals("")||passwordEditText.getText().toString().equals("")){
                    Snackbar snackbar = Snackbar.make(v, "Please fill the fields to proceed", Snackbar.LENGTH_LONG);
                    View sb = snackbar.getView();
                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    snackbar.show();
                }
                else{
                    String user_id = fState.substring(0,2).toUpperCase()+fName.substring(0,2).toUpperCase()+fHighway.split(" ")[0];
                    editor = preferences.edit();
                    editor.putString("user_id",user_id);
                    editor.commit();
                    mAuth.createUserWithEmailAndPassword(user_id+"@toll.com",passwordEditText.getText().toString()).addOnCompleteListener(TollRegister.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            createTollUser();
                            progressDialog.dismiss();
                            Intent intent = new Intent(TollRegister.this,DocumentUpload.class);
                            startActivity(intent);

                        }
                    });
                }
            }
        });
    }

    private void createTollUser() {
        TollUserClass tollUser = new TollUserClass();
        tollUser.setTollId(fState.substring(0,2).toUpperCase()+fName.substring(0,2).toUpperCase()+fHighway.split(" ")[0]);
        tollUser.setState(fState);
        tollUser.setTollPlazaName(fName);
        tollUser.setDetails(fDetails);
        tollUser.setPhone(phone.getText().toString());
        tollUser.setStatus("No");
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Tolls");
        ref.child(fState.substring(0,2).toUpperCase()+fName.substring(0,2).toUpperCase()+fHighway.split(" ")[0]).setValue(tollUser);

    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplication().getAssets().open("tollPlazaList.json");
            Log.e(TAG, "loadJSONFromAsset: "+"InputStream opened" );
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        //Log.e(TAG, "loadJSONFromAsset: "+json );
        return json;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


