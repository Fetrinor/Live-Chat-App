package com.example.chat_mobil_final.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_mobil_final.Activity.ChatActivity;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.MessageRequest;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private ArrayList<User> mUserList;
    private Context mContext;
    private View v;
    private User mUserInfo;
    private int kPos;

    private Dialog messageDialog;
    private ImageView imgClose;
    private LinearLayout linearSend;
    private CircleImageView imgProfile;
    private EditText et_message;
    private String txtMessage;
    private Window messageWindow;
    private TextView txtUserName;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRef;
    private String mUID, mUserName, mUserProfileUrl, chanelID, messageDocID;
    private MessageRequest messageRequest;
    private HashMap<String, Object> mData;

    private Intent chatIntent;


    public UserAdapter(ArrayList<User> mUserList, Context mContext, String mUID, String mUserName, String mUserProfileUrl) {
        this.mUserList = mUserList;
        this.mContext = mContext;
        mFirestore = FirebaseFirestore.getInstance();
        this.mUID = mUID;
        this.mUserName = mUserName;
        this.mUserProfileUrl = mUserProfileUrl;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        mUserInfo = mUserList.get(position);
        holder.userName.setText(mUserInfo.getUserName());

        if(mUserInfo.getUserProfile().equals("default")){
            holder.userProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Picasso.get().load(mUserInfo.getUserProfile()).resize(66, 66).into(holder.userProfile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kPos = holder.getAdapterPosition();
                if(kPos != RecyclerView.NO_POSITION){
                    mRef = mFirestore.collection("Kullanıcılar").document(mUID)
                            .collection("Kanal").document(mUserList.get(kPos).getUserID());
                    mRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                chatIntent = new Intent(mContext, ChatActivity.class);
                                chatIntent.putExtra("hedefID", mUserList.get(kPos).getUserID());
                                chatIntent.putExtra("hedefProfil", mUserList.get(kPos).getUserProfile());
                                chatIntent.putExtra("chanelID", documentSnapshot.getData().get("chanelID").toString());
                                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(chatIntent);

                            }else{
                                sendMessageDialog(mUserList.get(kPos));
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendMessageDialog(User user) {
        messageDialog = new Dialog(mContext);
        messageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        messageWindow = messageDialog.getWindow();
        messageWindow.setGravity(Gravity.CENTER);
        messageDialog.setContentView(R.layout.send_message_dialog);

        imgClose = messageDialog.findViewById(R.id.sendMessageDialogSendClose);
        linearSend = messageDialog.findViewById(R.id.sendMessageDialogSendMessageLinear);
        imgProfile = messageDialog.findViewById(R.id.sendMessageDialogSendProfile);
        et_message = messageDialog.findViewById(R.id.sendMessageDialogEditText);
        txtUserName = messageDialog.findViewById(R.id.sendMessageDialogUserName);

        txtUserName.setText(user.getUserName());

        if(user.getUserProfile().equals("default")){
            imgProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Picasso.get().load(user.getUserProfile()).resize(126, 126).into(imgProfile);
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });

        linearSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtMessage = et_message.getText().toString();

                if(!TextUtils.isEmpty(txtMessage)){
                    chanelID = UUID.randomUUID().toString();

                    messageRequest = new MessageRequest(chanelID, mUID, mUserName, mUserProfileUrl);
                    mFirestore.collection("Mesajİstekleri").document(user.getUserID())
                            .collection("İstekler").document(mUID)
                            .set(messageRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                messageDocID = UUID.randomUUID().toString();
                                mData = new HashMap<>();
                                mData.put("messageContent", txtMessage);
                                mData.put("sendUser", mUID);
                                mData.put("reciveUser", user.getUserID());
                                mData.put("messageType", "text"); //foto da yollanabilmesi için şimdilik text kalsın
                                mData.put("messageDate", FieldValue.serverTimestamp());
                                mData.put("docID", messageDocID);

                                mFirestore.collection("ChatKanalları").document(chanelID)
                                        .collection("Mesajlar").document(messageDocID)
                                        .set(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(mContext, "Mesaj isteğiniz başarılı.", Toast.LENGTH_SHORT).show();
                                            if(messageDialog.isShowing()){
                                                messageDialog.dismiss();
                                            }
                                        }else{
                                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(mContext, "Boş mesaj gönderemezsiniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        messageWindow.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        messageDialog.show();
    }


    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{

        TextView userName;
        CircleImageView userProfile;

        public UserHolder(@NonNull View itemView){
            super(itemView);

            userName = itemView.findViewById(R.id.user_item_txtUserName);
            userProfile = itemView.findViewById(R.id.user_item_imgUserProfile);
        }

    }
}
