package com.example.odev.firebaseandroidchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference sUserDatabase;
    private FirebaseUser sCurrentUser;
    private Button mstatus;

    //Layout degisiklikleri

    private CircleImageView sDisplayImage;
    private TextView sName;
    private TextView sStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        sName=(TextView)findViewById(R.id.settings_display_name);
        sStatus=(TextView)findViewById(R.id.settings_status);
        mstatus=(Button)findViewById(R.id.settings_status_btn);


        sCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        sUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(sCurrentUser.getUid());

        sUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name =dataSnapshot.child("name").getValue().toString();
                String image =dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thump_image=dataSnapshot.child("thump_image").getValue().toString();

                sName.setText(name);
                sStatus.setText(status);
                mstatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent statusintent=new Intent(SettingsActivity.this,StatusActivity.class);
                        startActivity(statusintent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
