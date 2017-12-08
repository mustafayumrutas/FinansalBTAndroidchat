package com.example.odev.firebaseandroidchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


public class UsersActivity extends AppCompatActivity {

    public Toolbar mToolbar;

    private RecyclerView mUserslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar= (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tüm Kullanıcılar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mUserslist = (RecyclerView) findViewById(R.id.users_list);



    }
}
