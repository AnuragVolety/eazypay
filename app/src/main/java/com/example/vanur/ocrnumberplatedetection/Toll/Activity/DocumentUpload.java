package com.example.vanur.ocrnumberplatedetection.Toll.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanur.ocrnumberplatedetection.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DocumentUpload extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ImageView document;
    Button button;
    boolean imageSet = false;
    TextView tollId;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String MyPreferences = "MyPreferences";
    Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        android.support.v7.widget.CardView upload = (android.support.v7.widget.CardView) findViewById(R.id.upload);
        button = (Button) findViewById(R.id.button);
        document= (ImageView) findViewById(R.id.document);
        tollId = (TextView) findViewById(R.id.toll_id);
        tollId.setText(""+ preferences.getString("user_id",""));
        Log.e( "onCreate: ","tolID:  "+  preferences.getString("user_id","" ));
        tollId.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DocumentUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DocumentUpload.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(imageSet){
                    final ProgressDialog progressDialog = new ProgressDialog(DocumentUpload.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    StorageReference reference = storageReference.child("tollDocuments/"+ preferences.getString("user_id",""));
                    reference.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(DocumentUpload.this,"Document Successfully Uploaded",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(DocumentUpload.this,"Please wait until the administrator reviews your document.",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DocumentUpload.this,TollConfirmationActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Snackbar snackbar = Snackbar.make(v, "Document upload failed", Snackbar.LENGTH_LONG);
                                    View sb = snackbar.getView();
                                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                                    snackbar.show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }
                else{
                    Snackbar snackbar = Snackbar.make(v, "Please upload your documents to proceed", Snackbar.LENGTH_LONG);
                    View sb = snackbar.getView();
                    sb.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(filePath,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            document.setVisibility(View.VISIBLE);
            document.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            imageSet = true;
        }

    }
}
