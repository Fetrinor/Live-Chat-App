package com.example.chat_mobil_final.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.MessageRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRequestAdapter extends RecyclerView.Adapter<MessageRequestAdapter.MessageRequestHolder> {

    private ArrayList<MessageRequest> mMessageRequestList;
    private Context mcontext;
    private MessageRequest mMessageRequest, newMessageRequest;
    private View v;
    private int mPos;

    private String mUserUID, mUserName, mUserProfileUrl;

    private FirebaseFirestore mFirestore;

    public MessageRequestAdapter(ArrayList<MessageRequest> mMessageRequestList, Context mcontext, String mUserUID, String mUserName, String mUserProfileUrl) {
        this.mMessageRequestList = mMessageRequestList;
        this.mcontext = mcontext;
        mFirestore = FirebaseFirestore.getInstance();
        this.mUserUID = mUserUID;
        this.mUserName = mUserName;
        this.mUserProfileUrl = mUserProfileUrl;
    }

    @NonNull
    @Override
    public MessageRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(mcontext).inflate(R.layout.recive_message_request_item, parent, false);
        return new MessageRequestHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRequestHolder holder, int position) {
        mMessageRequest = mMessageRequestList.get(position);
        holder.txtMessage.setText(mMessageRequest.getUserName() + " kullanıcısı sana mesaj göndermek istiyor.");
        if(mMessageRequest.getUserProfile().equals("default")){
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Picasso.get().load(mMessageRequest.getUserProfile()).resize(77,77).into(holder.imgProfile);
        }

        holder.imgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPos = holder.getAdapterPosition();

                if(mPos != RecyclerView.NO_POSITION){
                    newMessageRequest = new MessageRequest(mMessageRequestList.get(mPos).getChanelID(),
                            mMessageRequestList.get(mPos).getTargetID(), mMessageRequestList.get(mPos).getUserName(),
                            mMessageRequestList.get(mPos).getUserProfile());
                    mFirestore.collection("Kullanıcılar").document(mUserUID)
                            .collection("Kanal").document(mMessageRequestList.get(mPos).getTargetID())
                            .set(newMessageRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                newMessageRequest = new MessageRequest(mMessageRequestList.get(mPos).getChanelID(),
                                        mUserUID, mUserName, mUserProfileUrl);

                                mFirestore.collection("Kullanıcılar").document(mMessageRequestList.get(mPos).getTargetID())
                                        .collection("Kanal").document(mUserUID).set(newMessageRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    MessageRequestDelete(mMessageRequestList.get(mPos).getTargetID(), "Mesaj isteği kabul edildi");
                                                }
                                                else{
                                                    Toast.makeText(mcontext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(mcontext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        holder.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPos = holder.getAdapterPosition();
                if(mPos != RecyclerView.NO_POSITION){
                    MessageRequestDelete(mMessageRequestList.get(mPos).getTargetID(), "Mesaj isteği reddedildi.");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageRequestList.size();
    }

    class MessageRequestHolder extends RecyclerView.ViewHolder{

        CircleImageView imgProfile;
        TextView txtMessage;
        ImageView imgCancel, imgConfirm;

        public MessageRequestHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.ReciveMessageRequestItem_imgProfile);
            txtMessage = itemView.findViewById(R.id.ReciveMessageRequestItem_txtMessage);
            imgCancel = itemView.findViewById(R.id.ReciveMessageRequestItem_imgCancel);
            imgConfirm = itemView.findViewById(R.id.ReciveMessageRequestItem_imgConfirm);
        }
    }
    
    private void MessageRequestDelete(String targetUID, final String messageContent){
        mFirestore.collection("Mesajİstekleri").document(mUserUID)
                .collection("İstekler").document(targetUID).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            notifyDataSetChanged(); //ilgili içerik değiştiğinde kontrol edilmesini sağlıyor.
                            Toast.makeText(mcontext, messageContent, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mcontext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
