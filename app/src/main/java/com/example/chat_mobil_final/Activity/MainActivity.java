package com.example.chat_mobil_final.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_mobil_final.Adapter.MessageRequestAdapter;
import com.example.chat_mobil_final.Fragment.MessagesFragment;
import com.example.chat_mobil_final.Fragment.ProfileFragment;
import com.example.chat_mobil_final.Fragment.UsersFragment;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.MessageRequest;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //BottomNavigationView
    private BottomNavigationView mBottomView;
    //Fragments
    private UsersFragment usersFragment;
    private MessagesFragment messagesFragment;
    private ProfileFragment profileFragment;
    private FragmentTransaction transaction;

    private Toolbar mToolbar;
    private RelativeLayout mRelativeNotif;
    private TextView txtNotifCount;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private FirebaseUser mUser;
    private MessageRequest messageRequest;
    private ArrayList<MessageRequest> messageRequestList;

    private Dialog messageRequestDialog;
    private ImageView messageRequestClose;
    private RecyclerView messageRequestRecyclerView;
    private MessageRequestAdapter messageRequestAdapter;

    private DocumentReference mRef;
    private User mUserInfo;

    private void init(){
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = mFirestore.collection("Kullanıcılar").document(mUser.getUid());
        mRef.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mUserInfo = documentSnapshot.toObject(User.class);
                }
            }
        });

        messageRequestList = new ArrayList<>();

        mBottomView = (BottomNavigationView) findViewById(R.id.MainActivityBottomView);
        usersFragment = new UsersFragment();
        messagesFragment = new MessagesFragment();
        profileFragment = new ProfileFragment();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRelativeNotif = (RelativeLayout) findViewById(R.id.bar_layout_relativeNotif);
        txtNotifCount = (TextView) findViewById(R.id.bar_layout_txtNotifCount);

        FragmentSettings(usersFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        mQuery = mFirestore.collection("Mesajİstekleri").document(mUser.getUid())
                .collection("İstekler");
        mQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value != null){
                    txtNotifCount.setText(String.valueOf(value.getDocuments().size()));
                    messageRequestList.clear();

                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        messageRequest = snapshot.toObject(MessageRequest.class);
                        messageRequestList.add(messageRequest);
                    }
                }
            }
        });

        mRelativeNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageRequestDialog();
            }
        });

        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_nav_ic_people:
                        mRelativeNotif.setVisibility(View.INVISIBLE);
                        FragmentSettings(usersFragment);
                        return true;
                    case R.id.bottom_nav_ic_message:
                        mRelativeNotif.setVisibility(View.VISIBLE);
                        FragmentSettings(messagesFragment);
                        return true;
                    case R.id.bottom_nav_ic_profile:
                        mRelativeNotif.setVisibility(View.INVISIBLE);
                        FragmentSettings(profileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void FragmentSettings(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.MainActivityFrameLayout, fragment);
        transaction.commit();
    }

    private void MessageRequestDialog(){
        messageRequestDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        messageRequestDialog.setContentView(R.layout.dialog_recive_message_request);

        messageRequestClose = messageRequestDialog.findViewById(R.id.ReciveMessageRequest_imgClose);
        messageRequestRecyclerView = messageRequestDialog.findViewById(R.id.ReciveMessageRequestRecyclerView);

        messageRequestClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageRequestDialog.dismiss();
            }
        });

        messageRequestRecyclerView.setHasFixedSize(true);
        messageRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(messageRequestList.size() > 0){
            messageRequestAdapter = new MessageRequestAdapter(messageRequestList, this, mUserInfo.getUserID(), mUserInfo.getUserName(), mUserInfo.getUserProfile());
            messageRequestRecyclerView.setAdapter(messageRequestAdapter);
        }

        messageRequestDialog.show();
    }
}