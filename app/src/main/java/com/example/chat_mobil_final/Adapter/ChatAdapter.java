package com.example.chat_mobil_final.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.Chat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private static final int MESSAGE_LEFT = 1;
    private static final int MESSAGE_RIGHT = 0;

    private ArrayList<Chat> mChatList;
    private Context mContext;
    private String mUID;
    private String targetProfile;
    private View v;
    private Chat mChat;

    public ChatAdapter(ArrayList<Chat> mChatList, Context mContext, String mUID, String targetProfile) {
        this.mChatList = mChatList;
        this.mContext = mContext;
        this.mUID = mUID;
        this.targetProfile = targetProfile;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_LEFT)
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        else if (viewType == MESSAGE_RIGHT)
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatHolder holder, int position) {
        mChat = mChatList.get(position);
        holder.txtMessage.setText(mChat.getMessageContent());

        if (!mChat.getSendUser().equals(mUID)){
            if (targetProfile.equals("default"))
                holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
            else
                Picasso.get().load(targetProfile).resize(56, 56).into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        CircleImageView imgProfile;
        TextView txtMessage;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.chatItemUserProfile);
            txtMessage = itemView.findViewById(R.id.chatItemTextMessage);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatList.get(position).getSendUser().equals(mUID))
            return MESSAGE_RIGHT;
        else
            return MESSAGE_LEFT;
    }
}
