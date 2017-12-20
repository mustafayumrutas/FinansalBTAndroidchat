package com.chatfire.odev.firebaseandroidchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    //Firebase Giris
    private FirebaseAuth mAuth;
    //toolbar
    private Toolbar mToolbar;
    //bekleme kısmı
    private ProgressDialog mRegProgress;
    //database
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Android alanlari
        mDisplayName =(TextInputLayout) findViewById(R.id.reg_display_name);
        mCreateBtn =(Button) findViewById(R.id.reg_create_btn);
        mEmail =(TextInputLayout) findViewById(R.id.reg_email);
        mPassword=(TextInputLayout) findViewById(R.id.reg_password);

        //Toolbar
        mToolbar=(Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hesap olustur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bekleme kısmı
        mRegProgress = new ProgressDialog(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name =mDisplayName.getEditText().getText().toString();
                String email =mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle("Uyelik olusturuluyor");
                    mRegProgress.setMessage("Lütfen üyelik oluşturulurken bekleyin");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();


                    register_user(display_name,email,password);

                }

            }
        });
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void register_user(final String display_name, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user =FirebaseAuth.getInstance().getCurrentUser();
                    String uid =current_user.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> userMap =new HashMap<>();
                    userMap.put("name",display_name);
                    userMap.put("status","Merhaba Firebasechatappi kullanıyorum");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("device_token",deviceToken);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();

                                Intent mainIntent =new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });



                }
                else{

                    mRegProgress.hide();
                    String TAG = "FIREBASE_EXCEPTION";
                    FirebaseException e = (FirebaseException)task.getException();
                    Log.d(TAG, "Reason: " +  e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Hata",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
