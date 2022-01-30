package com.example.chat_mobil_final.Fragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat_mobil_final.Adapter.UserAdapter;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class UsersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private View v;
    private UserAdapter mAdapter;
    private ArrayList<User> mUserList;
    private User mUserInfo, mUserInfo2;

    private FirebaseUser firebaseUser;
    private Query mQuery;
    private FirebaseFirestore mFirestore;

    private DocumentReference mRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_users, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mRef = mFirestore.collection("Kullanıcılar").document(firebaseUser.getUid());

        mUserList = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.Users_Fragment_RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));


        mRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mUserInfo2 = documentSnapshot.toObject(User.class);

                    mQuery = mFirestore.collection("Kullanıcılar");
                    mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            //QuerySnapshot döküman döndürüyor
                            if(error != null){
                                Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(value != null){
                                mUserList.clear();
                                //DocumentSnapshot QuerySnapshotdan gelen dökümanın içindekilere ulaşabilir

                                for (DocumentSnapshot snapshot : value.getDocuments()){
                                    mUserInfo = snapshot.toObject(User.class);

                                    //gelen verileri kontrol etmemiz lazım
                                    assert mUserInfo != null;
                                    if(!mUserInfo.getUserID().equals(firebaseUser.getUid())){
                                        mUserList.add(mUserInfo);
                                    }
                                }

                                mRecyclerView.addItemDecoration(new LinearDecoration(20, mUserList.size()));
                                mAdapter = new UserAdapter(mUserList, v.getContext(), mUserInfo2.getUserID(), mUserInfo2.getUserName(), mUserInfo2.getUserProfile());
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                    });
                }

            }
        });




        return v;
    }
    //son kullanıcıya kadar altta boşluk olsun ki yapışık olmasınlar
    class LinearDecoration extends RecyclerView.ItemDecoration{
        private int spacing;
        private int dataCount;

        public LinearDecoration(int spacing, int dataCount) {
            this.spacing = spacing;
            this.dataCount = dataCount;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);

            if(pos != dataCount-1){
                outRect.bottom = spacing;
            }
        }
    }
}