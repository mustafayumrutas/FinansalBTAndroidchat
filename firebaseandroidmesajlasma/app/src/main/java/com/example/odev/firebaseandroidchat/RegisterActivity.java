package com.example.odev.firebaseandroidchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName =(TextInputLayout) findViewById(R.id.reg_display_name);
        mCreateBtn =(Button) findViewById(R.id.reg_create_btn);
        mEmail =(TextInputLayout) findViewById(R.id.reg_email);
        mPassword=(TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name =mDisplayName.getEditText().getText().toString();
                String email =mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();

                register_user(display_name,email,password);
            }
        });
    }
    private void register_user(String display_name,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent mainIntent =new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
                else{
                    String TAG = "FIREBASE_EXCEPTION";
                    FirebaseException e = (FirebaseException)task.getException();
                    Log.d(TAG, "Reason: " +  e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Hata",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
