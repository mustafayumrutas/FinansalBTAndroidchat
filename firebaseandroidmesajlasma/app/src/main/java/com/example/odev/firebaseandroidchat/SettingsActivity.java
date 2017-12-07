package com.example.odev.firebaseandroidchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference sUserDatabase;
    private FirebaseUser sCurrentUser;
    private Button mstatus;
    private Button mImage;

    //Layout degisiklikleri

    private CircleImageView sDisplayImage;
    private TextView sName;
    private TextView sStatus;

    //firebaseauth
    private FirebaseAuth mAuth;

    //Database
    private DatabaseReference mDatabase;

    //private static final int Gallery_pick=1;
    //storage
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        sName=(TextView)findViewById(R.id.settings_display_name);
        sStatus=(TextView)findViewById(R.id.settings_status);
        mstatus=(Button)findViewById(R.id.settings_status_btn);
        mImage=(Button)findViewById(R.id.settings_image_btn);
        mAuth = FirebaseAuth.getInstance();


        sCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        sUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(sCurrentUser.getUid());

        mImageStorage= FirebaseStorage.getInstance().getReference();
        sUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name =dataSnapshot.child("name").getValue().toString();
                String image =dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thump_image=dataSnapshot.child("thump_image").getValue().toString();

                sName.setText(name);
                sStatus.setText(status);

                Picasso.with(SettingsActivity.this).load(image).into(sDisplayImage);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        mstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value=sStatus.getText().toString();
                Intent statusintent=new Intent(SettingsActivity.this,StatusActivity.class);
                statusintent.putExtra("status_value",status_value);
                startActivity(statusintent);
            }
        });
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent galleryIntent=new Intent();
                galleryIntent.setAction("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select image"),Gallery_pick);*/
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mProgressDialog=new ProgressDialog(SettingsActivity.this);
            mProgressDialog.setTitle("Yükleniyor");
            mProgressDialog.setMessage("Lütfen Bekleyiniz");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                String profile=mAuth.getCurrentUser().getUid();

                StorageReference filepath=mImageStorage.child("profile_images").child(profile+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String download_url=task.getResult().getDownloadUrl().toString();
                            String profile=mAuth.getCurrentUser().getUid();
                            mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(profile).child("image");
                            mDatabase.setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     mProgressDialog.dismiss();
                                     Toast.makeText(SettingsActivity.this,"calisiyor",Toast.LENGTH_LONG).show();

                                 }


                                }
                            });


                        }else{
                            Toast.makeText(SettingsActivity.this,"calismiyor",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}