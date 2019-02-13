package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vanur.ocrnumberplatedetection.LoginActivity;
import com.example.vanur.ocrnumberplatedetection.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity.java";
    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    private int requestPermissionID = 1000;
    private TextView mTextView;
    private FloatingActionButton fab;
    boolean toggle = true;
    private String textCaptured;
    public static FirebaseUser user;
    ProgressBar progressBar;

    TextView tollId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.e( "onCreate: ","The user id of the toll is "+ user.getEmail().split("@")[0]);
        tollId = (TextView) findViewById(R.id.tolIId);
        tollId.setText(user.getEmail().split("@")[0]);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCameraView = (SurfaceView) findViewById(R.id.surfaceView);
        mTextView = (TextView) findViewById(R.id.text_view);
        startCameraSource();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle) {
                    mCameraSource.stop();
                    toggle = false;
                    Log.e(TAG, "onClick: "+toggle );
                }
                if (!toggle) {
                    startCameraSource();
                    toggle = true;
                }

            }
        });
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            /**
             * Detect all the text from camera using TextBlock and the save values into a stringBuilder
             * which will then be set to the textView.
             * */
            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            // set the stringBuilder value to textView
                            mTextView.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });
    }

    private void startCameraSource() {
        Log.e(TAG, "startCameraSource: " + "Starting Camera");
        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();

        if (!textRecognizer.isOperational()) {
            Log.e(TAG, "Detector dependencies not loaded yet");
        } else {
            Log.e(TAG, "startCameraSource: " + "In else");
            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(MainActivity.this, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
            Log.e(TAG, "startCameraSource: " + "In else");

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    textCaptured = item.getValue().toString().replaceAll("\\s+","");
                                    textCaptured=textCaptured.replaceAll("/[^0-9a-z]/", "");
                                    //mTextView.setText(textCaptured);

                                    if(textCaptured.matches("^[A-Z]{2}[\\s]?[0-9]{1,2}[\\s]?[A-Z]{1,2}[\\s]?[0-9]{4}")||textCaptured.matches("^[A-Z]{2}[\\s]?[0-9][\\s]?[A-Z]{3}[\\s]?[0-9]{4}")){
                                        progressBar.setVisibility(View.VISIBLE);
                                        mCameraSource.stop();
                                        fab.performClick();
                                        mTextView.setText(textCaptured);
                                        Intent intent = new Intent(MainActivity.this, GetUser.class);
                                        intent.putExtra("VehicleNo", textCaptured);
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(intent);
                                        break;
                                    }
                                }

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_toll, menu);
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if(id == R.id.profile){
            Intent intent =new Intent(MainActivity.this,TollProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
