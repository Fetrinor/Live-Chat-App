package com.example.chat_mobil_final.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chat_mobil_final.Activity.SignInActivity;
import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final int PERMISSION = 0;
    private static final int PERMISSION_GRANTED = 1;

    private EditText et_UserName, et_UserEmail;
    private CircleImageView imgUserProfile;
    private View v;
    private ImageView imgNewProfile;
    private Button btn_SignOut;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRef;
    private FirebaseUser mUser;
    private User UserInfo;

    private Intent galeryIntent;
    private Uri mUri;
    private Bitmap reciveImage;
    private ImageDecoder.Source imgSource;
    private ByteArrayOutputStream outputStream;
    private byte[] imgByte;

    private StorageReference storageRef, newstorageRef, sRef;
    private String saveUrl, downloadUrl;
    private HashMap<String, Object> mData;

    private Query mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        et_UserName = v.findViewById(R.id.profileFragmentEditTextUserName);
        et_UserEmail = v.findViewById(R.id.profileFragmentEditTextUserEmail);
        imgUserProfile = v.findViewById(R.id.profileFragmentUserProfileImg);
        imgNewProfile = v.findViewById(R.id.profileFragment_newImg);
        btn_SignOut = v.findViewById(R.id.BtnsignOut);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        mRef = mFirestore.collection("Kullanıcılar").document(mUser.getUid());
        mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (value != null && value.exists()) {
                    UserInfo = value.toObject(User.class);
                    if (UserInfo != null) {
                        et_UserName.setText(UserInfo.getUserName());
                        et_UserEmail.setText(UserInfo.getEmail());
                        if (UserInfo.getUserProfile().equals("default")) {
                            imgUserProfile.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Picasso.get().load(UserInfo.getUserProfile())
                                    .resize(156, 156).into(imgUserProfile);
                        }
                    }
                }
            }
        });

        imgNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
                } else {
                    StartGaleryIntent();
                }
            }
        });

        btn_SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                requireActivity().finish();
                startActivity(new Intent(v.getContext(), SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        return v;
    }

    private void StartGaleryIntent() {
        galeryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeryIntent, PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartGaleryIntent();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PERMISSION_GRANTED) {
            if (data != null && data.getData() != null) {
                mUri = data.getData();

                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        imgSource = ImageDecoder.createSource(v.getContext().getContentResolver(), mUri);
                        reciveImage = ImageDecoder.decodeBitmap(imgSource);
                    } else {
                        reciveImage = MediaStore.Images.Media.getBitmap(v.getContext().getContentResolver(), mUri);
                    }

                    outputStream = new ByteArrayOutputStream();
                    reciveImage.compress(Bitmap.CompressFormat.PNG, 75, outputStream);
                    imgByte = outputStream.toByteArray();

                    saveUrl = "Kullanıcılar/" + mUser.getEmail() + "/profil.png";
                    sRef = storageRef.child(saveUrl);
                    sRef.putBytes(imgByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            newstorageRef = FirebaseStorage.getInstance().getReference(saveUrl);
                            newstorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    mData = new HashMap<>();
                                    mData.put("userProfile", downloadUrl);

                                    mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                                            .update(mData).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ProfileUpdate(downloadUrl);
                                                    } else {
                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ProfileUpdate(final String url) {
        mQuery = mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                .collection("Kanal");
        mQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot snp : queryDocumentSnapshots.getDocuments()) {
                        mData = new HashMap<>();
                        mData.put("UserProfile", url);

                        mFirestore.collection("Kullanıcılar").document(snp.getData().get("UserID").toString())
                                .collection("Kanal").document(mUser.getUid()).update(mData);
                    }

                    Toast.makeText(v.getContext(), "Profil resminiz değiştirildi.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}