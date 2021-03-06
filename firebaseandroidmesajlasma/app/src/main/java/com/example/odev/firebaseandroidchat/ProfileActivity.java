package com.example.odev.firebaseandroidchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendReqBtn,mDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;


    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onStart() {
        super.onStart();
        mRootRef.child("Users").child(mCurrent_user.getUid()).child("online").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRootRef.child("Users").child(mCurrent_user.getUid()).child("online").setValue(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef =FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase= FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileSendReqBtn =(Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn= (Button) findViewById(R.id.profile_dec_friend_req);

        mCurrent_state="not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Yükleniyor");
        mProgressDialog.setMessage("Lütfen bekleyiniz");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();







        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name= dataSnapshot.child("name").getValue().toString();
                String status= dataSnapshot.child("status").getValue().toString();
                String image= dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultavatar).into(mProfileImage);


                //--------------Friends List / Request Features -------------

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                mCurrent_state="req_received";
                                mProfileSendReqBtn.setText("Arkadaslik istegini kabul et");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                mCurrent_state="req_sent";
                                mProfileSendReqBtn.setText("Arkadaslik istegini reddet");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                            mProgressDialog.dismiss();

                        }else {

                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state="friends";
                                        mProfileSendReqBtn.setText("Arkadasliktan cikar");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }
                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {




            }


        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                //------------Not friends Statement ----------

                if(mCurrent_state.equals("not_friends"))
                {
                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId= newNotificationref.getKey();

                    HashMap <String,String> notificationData =new HashMap<>();
                    notificationData.put("from",mCurrent_user.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid()+"/request_type","received");
                    requestMap.put("Requests/"+user_id+"/"+mCurrent_user.getUid()+"/request_type","true");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError!=null){

                                Toast.makeText(ProfileActivity.this,"Gönderirken bir hata ile karşılaşıldı",Toast.LENGTH_SHORT).show();

                            }

                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state="req_sent";
                            mProfileSendReqBtn.setText("Arkadaslik istegini reddet");



                        }
                    });


                }

                // -----------------------Cancel Request Statement------

                if(mCurrent_state.equals("req_sent")){

                    Map cancelreqMap = new HashMap();

                    cancelreqMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id,null);
                    cancelreqMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid(),null);

                    mRootRef.updateChildren(cancelreqMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="not_friends";
                                mProfileSendReqBtn.setText("Arkadaslik istegi gönder");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }else{

                                String error= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                //// ---------------- REQ RECEIVED STATE ---------------
                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid()+"/date",currentDate);

                    friendsMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid(),null);
                    friendsMap.put("Requests/"+mCurrent_user.getUid()+"/"+user_id,null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="friends";
                                mProfileSendReqBtn.setText("Arkadasliktan cikar");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }else{

                                String error= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

                /////// ---------------------- delete Friend State --------------------

                if(mCurrent_state.equals("friends")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid(),null);
                    unfriendMap.put("messages/"+user_id+"/"+mCurrent_user.getUid(),null);
                    unfriendMap.put("messages/"+mCurrent_user.getUid()+"/"+user_id,null);
                    unfriendMap.put("Chat/"+user_id+"/"+mCurrent_user.getUid(),null);
                    unfriendMap.put("Chat/"+mCurrent_user.getUid()+"/"+user_id,null);
                    unfriendMap.put("Chat/"+user_id+"/"+mCurrent_user.getUid(),null);
                    unfriendMap.put("Chat/"+mCurrent_user.getUid()+"/"+user_id,null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){

                                mCurrent_state="not_friends";
                                mProfileSendReqBtn.setText("Arkadaslik istegi gönder");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }else{

                                String error= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }



            }

        });
        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map decMap = new HashMap();
                decMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id,null);
                decMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid(),null);
                decMap.put("Requests/"+mCurrent_user.getUid()+"/"+user_id,null);
                mRootRef.updateChildren(decMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError==null){
                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state="not_friends";
                            mProfileSendReqBtn.setText("Arkadaslik isteği gönder");

                            mDeclineBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setEnabled(false);

                        }else{

                            String error= databaseError.getMessage();
                            Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                        }
                    }
                });




            }
        });




    }
}
