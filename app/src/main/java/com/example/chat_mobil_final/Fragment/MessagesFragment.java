package com.example.chat_mobil_final.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat_mobil_final.Adapter.MessagesAdapter;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.MessageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private View v;
    private RecyclerView mRecyclerView;

    private FirebaseFirestore mFirestore;
    private Query mQuery, lastMessageQuerry;
    private ArrayList<MessageRequest> mArrayList;
    private ArrayList<String> mLastMessageList;
    private MessageRequest messageRequest;
    private FirebaseUser mUser;

    private MessagesAdapter messagesAdapter;
    private int lastMessageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_messages, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mArrayList = new ArrayList<>();
        mLastMessageList = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.MessagesFragmentRecyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        mQuery = mFirestore.collection("Kullanıcılar").document(mUser.getUid()).collection("Kanal");
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }else if(value != null){
                    mArrayList.clear();
                    lastMessageIndex = 0;

                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        messageRequest = snapshot.toObject(MessageRequest.class);
                        if(messageRequest != null){
                            mArrayList.add(messageRequest);

                            lastMessageQuerry = mFirestore.collection("ChatKanalları").document(messageRequest.getChanelID())
                                    .collection("Mesajlar").orderBy("messageDate", Query.Direction.DESCENDING)
                                    .limit(1);
                            lastMessageQuerry.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value2, @Nullable FirebaseFirestoreException error) {
                                    if(error == null && value2 != null){
                                        mLastMessageList.clear();

                                        for(DocumentSnapshot snp : value2.getDocuments()){
                                            mLastMessageList.add(snp.getData().get("messageContent").toString());
                                            lastMessageIndex++;

                                            if(lastMessageIndex == value.getDocuments().size()){
                                                messagesAdapter = new MessagesAdapter(mArrayList, v.getContext(), mLastMessageList);
                                                mRecyclerView.setAdapter(messagesAdapter);
                                                lastMessageIndex = 0;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }



                }
            }
        });

        return v;
    }
}