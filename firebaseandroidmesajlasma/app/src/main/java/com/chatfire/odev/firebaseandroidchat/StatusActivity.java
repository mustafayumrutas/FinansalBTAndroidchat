package com.chatfire.odev.firebaseandroidchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSavebtn;
    private Button mImagebtn;
    //Firebase
    private DatabaseReference mstatusDatabase;
    private FirebaseUser mCurrentUser;

    //Yukleme bari
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //settingsten gelen string
        String status_value=getIntent().getStringExtra("status_value");

        //firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid().toString();
        mstatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("status");

        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Durum Degistir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus =(TextInputLayout)findViewById(R.id.status_input);
        mSavebtn =(Button) findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress =new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Yükleniyor");
                mProgress.setMessage("Lütfen Bekleyiniz");
                mProgress.show();
                String status = mStatus.getEditText().getText().toString();
                mstatusDatabase.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           mProgress.dismiss();
                           onSupportNavigateUp();
                       }
                       else{
                           mProgress.hide();
                           String TAG = "FIREBASE_EXCEPTION";
                           FirebaseException e = (FirebaseException)task.getException();
                           Log.d(TAG, "Reason: " +  e.getMessage());
                           Toast.makeText(StatusActivity.this, "Hata",Toast.LENGTH_LONG).show();


                       }
                    }

                });

            }
        });
    }
}
