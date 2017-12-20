package com.chatfire.odev.firebaseandroidchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
// Giris Yapılan Aktivite

public class LoginActivity extends AppCompatActivity {
    private Toolbar lToolbar;
    private TextInputLayout lLoginEmail;
    private TextInputLayout lLoginPassword;

    private Button lLoginbtn;

    private ProgressDialog lLoginprogress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lToolbar=(Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Giris Yap");

        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");

        lLoginEmail=(TextInputLayout) findViewById(R.id.login_email);
        lLoginPassword =(TextInputLayout) findViewById(R.id.login_password);
        lLoginbtn=(Button)findViewById(R.id.login_create_btn);

        lLoginprogress= new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        lLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lLoginEmail.getEditText().getText().toString();
                String password = lLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    lLoginprogress.setTitle("Giris Yapılıyor");
                    lLoginprogress.setMessage("Lütfen bekleyiniz");
                    lLoginprogress.setCanceledOnTouchOutside(false);
                    lLoginprogress.show();
                    loginUser(email,password);
                }
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    lLoginprogress.dismiss();

                    String current_user_id =mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    });




                }else{
                    lLoginprogress.hide();
                    String TAG = "FIREBASE_EXCEPTION";
                    FirebaseException e = (FirebaseException)task.getException();
                    Log.d(TAG, "Reason: " +  e.getMessage());
                    Toast.makeText(LoginActivity.this, "Hata",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
