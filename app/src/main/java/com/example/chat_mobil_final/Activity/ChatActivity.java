package com.example.chat_mobil_final.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_mobil_final.Adapter.ChatAdapter;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.Chat;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText et_Message;
    private String txtMessage, docID;
    private ImageButton btn_SendMessage;
    private CircleImageView targetProfile;
    private TextView targetName;
    private ImageView chatClose;

    private Intent getIntent;
    private DocumentReference targetRef;
    private User targetUser;
    private String targetID, channelID, targetProfileUrl;
    private FirebaseFirestore mFireStore;
    private FirebaseUser mUser;

    private Query chatQuery; //mesajları almak için
    private ArrayList<Chat> mChatList;
    private Chat mChat;
    private ChatAdapter chatAdapter;
    private HashMap<String, Object> mData;

    private void init(){
        mRecyclerView = (RecyclerView) findViewById(R.id.chatActivityRecyclerView);
        et_Message = (EditText) findViewById(R.id.chatActivityEditMessage);
        btn_SendMessage = (ImageButton) findViewById(R.id.chatActivityImgSendMessage);
        targetProfile = (CircleImageView) findViewById(R.id.chatActivityImgTargetProfile);
        targetName = (TextView) findViewById(R.id.chatActivityTxtTargetName);
        chatClose = (ImageView) findViewById(R.id.chatActivityImgClose);
        mChatList = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        getIntent = getIntent();
        targetID = getIntent.getStringExtra("hedefID");
        channelID = getIntent.getStringExtra("chanelID");
        targetProfileUrl = getIntent.getStringExtra("hedefProfil");

        btn_SendMessage.setOnClickListener(v -> SendMessage());

        chatClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        targetRef = mFireStore.collection("Kullanıcılar").document(targetID);
        targetRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //tek bi kişi geleceği için exists kullandık ve for içinde yazmadık
                if(value != null && value.exists()){
                    targetUser = value.toObject(User.class);

                    if(targetUser != null){
                        targetName.setText(targetUser.getUserName());

                        if(targetUser.getUserProfile().equals("default")){
                            targetProfile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Picasso.get().load(targetUser.getUserProfile())
                                    .resize(76, 76).into(targetProfile);
                        }
                    }
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        chatQuery = mFireStore.collection("ChatKanalları").document(channelID)
                .collection("Mesajlar").orderBy("messageDate", Query.Direction.ASCENDING);
        chatQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value != null){
                    mChatList.clear();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        mChat = snapshot.toObject(Chat.class);
                        if(mChat!=null) {
                            mChatList.add(mChat);
                        }
                    }

                    chatAdapter = new ChatAdapter(mChatList, ChatActivity.this, mUser.getUid(), targetProfileUrl);
                    mRecyclerView.setAdapter(chatAdapter);
                }
            }
        });
    }

    private void SendMessage(){
        txtMessage = et_Message.getText().toString();

        if(!TextUtils.isEmpty(txtMessage)){
            docID = UUID.randomUUID().toString();
            mData = new HashMap<>();
            mData.put("messageContent", txtMessage);
            mData.put("sendUser", mUser.getUid());
            mData.put("reciveUser", targetID);
            mData.put("messagetype", "text");
            mData.put("messageDate", FieldValue.serverTimestamp());
            mData.put("docID", docID);

            mFireStore.collection("ChatKanalları").document(channelID)
                    .collection("Mesajlar").document(docID)
                    .set(mData).addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        et_Message.setText("");
                    }else{
                        Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(ChatActivity.this, "Boş mesaj gönderemezsiniz.", Toast.LENGTH_SHORT).show();
        }
    }
}