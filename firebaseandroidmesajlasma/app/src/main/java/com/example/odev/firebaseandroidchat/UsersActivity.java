package com.example.odev.firebaseandroidchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    public Toolbar mToolbar;

    private RecyclerView mUserslist;
    private FirebaseAuth Auth;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar= (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tüm Kullanıcılar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users"); // Burada databasede Kullanicilar nasil kayitli olduklarina bakilmali
        Auth=FirebaseAuth.getInstance();


        mUserslist = (RecyclerView) findViewById(R.id.users_list);
        mUserslist.setHasFixedSize(true);
        mUserslist.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();
        Auth = FirebaseAuth.getInstance();
        mOthers = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase


        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {


                    usersViewHolder.setDisplayName(users.getName());
                    usersViewHolder.setUserStatus(users.getStatus());
                    usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                    final String user_id = getRef(position).getKey();
                    if(!Auth.getCurrentUser().getUid().equals(mOthers.child(user_id).getKey()))
                    {
                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("user_id", user_id);
                            startActivity(profileIntent);

                        }
                    });

                    }
            }
        };

        mUserslist.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setDisplayName( String name){
            TextView userNameView =(TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserStatus( String status){
            TextView userStatusView =(TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void  setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView =(CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defaultavatar).into(userImageView);


        }

    }

}
