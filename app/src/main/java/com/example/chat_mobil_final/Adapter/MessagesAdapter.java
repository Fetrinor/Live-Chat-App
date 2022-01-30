package com.example.chat_mobil_final.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_mobil_final.Activity.ChatActivity;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.MessageRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {

    private ArrayList<MessageRequest> mArrayList;
    private ArrayList<String> mLastMessageList;
    private Context mContext;
    private MessageRequest messageRequest;
    private View v;
    private int kPos;
    private Intent chatIntent;

    public MessagesAdapter(ArrayList<MessageRequest> mArrayList, Context mContext, ArrayList<String> mLastMessageList) {
        this.mArrayList = mArrayList;
        this.mContext = mContext;
        this.mLastMessageList = mLastMessageList;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v= LayoutInflater.from(mContext).inflate(R.layout.messages_item, parent, false);
        return new MessagesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        messageRequest = mArrayList.get(position);
        holder.userName.setText(messageRequest.getUserName());
        holder.lastMessage.setText(mLastMessageList.get(position));

        if(messageRequest.getUserProfile().equals("default")){
            holder.userProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Picasso.get().load(messageRequest.getUserProfile()).resize(66,66)
                    .into(holder.userProfile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kPos = holder.getAdapterPosition();

                if(kPos != RecyclerView.NO_POSITION){
                    chatIntent = new Intent(mContext, ChatActivity.class);
                    chatIntent.putExtra("hedefID", mArrayList.get(kPos).getTargetID());
                    chatIntent.putExtra("hedefProfil", mArrayList.get(kPos).getUserProfile());
                    chatIntent.putExtra("chanelID", mArrayList.get(kPos).getChanelID());
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(chatIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MessagesHolder extends RecyclerView.ViewHolder{

        TextView userName, lastMessage;
        CircleImageView userProfile;

        public MessagesHolder(@NonNull View itemView){
            super(itemView);

            userName = itemView.findViewById(R.id.messages_item_txtUserName);
            userProfile = itemView.findViewById(R.id.messages_item_imgUserProfile);
            lastMessage = itemView.findViewById(R.id.messages_item_txtLastMessage);
        }
    }
}
