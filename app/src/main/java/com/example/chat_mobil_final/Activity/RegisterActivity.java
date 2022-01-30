package com.example.chat_mobil_final.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chat_mobil_final.R;
import com.example.chat_mobil_final.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private User mUserInfo;
    private ProgressDialog mProgress;

    //Firebase
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //UI
    private LinearLayout RegisterLayout;
    private Button BtnRegister;
    private TextInputLayout inputUsername, inputEmail, inputPassword, inputPasswordConfirm;
    private EditText et_Username, et_Email, et_Password, et_PasswordConfirm;
    private String txtUserName, txtEmail, txtPassword, txtPasswordConfirm;


    void init(){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        RegisterLayout = (LinearLayout) findViewById(R.id.RegisterLayout);
        inputUsername = (TextInputLayout) findViewById(R.id.inputRegisterUserName);
        inputEmail = (TextInputLayout) findViewById(R.id.inputRegisterEmail);
        inputPassword = (TextInputLayout) findViewById(R.id.inputRegisterPassword);
        inputPasswordConfirm = (TextInputLayout) findViewById(R.id.inputRegisterPasswordConfirm);

        et_Username = (EditText) findViewById(R.id.et_RegisterUserName);
        et_Email = (EditText) findViewById(R.id.et_RegisterEmail);
        et_Password = (EditText) findViewById(R.id.et_RegisterPassword);
        et_PasswordConfirm = (EditText) findViewById(R.id.et_RegisterPasswordConfirm);

        BtnRegister = (Button) findViewById(R.id.BtnRegister);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Register();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void btn_Register() {

        txtUserName = et_Username.getText().toString();
        txtEmail = et_Email.getText().toString();
        txtPassword = et_Password.getText().toString();
        txtPasswordConfirm = et_PasswordConfirm.getText().toString();

        if(!TextUtils.isEmpty(txtUserName)){
            if(!TextUtils.isEmpty(txtEmail)){
                if(!TextUtils.isEmpty(txtPassword)){
                    if(!TextUtils.isEmpty(txtPasswordConfirm)){
                        if(txtPassword.equals(txtPasswordConfirm)){
                            mProgress = new ProgressDialog(RegisterActivity.this);
                            mProgress.setTitle("Kayıt Olunuyor...");
                            mProgress.show();

                            mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mUser = mAuth.getCurrentUser();
                                        if(mUser != null){
                                            mUserInfo = new User(txtUserName, txtEmail, mUser.getUid(), "default");

                                            mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                                                    .set(mUserInfo).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        ProgressSettings();
                                                        Toast.makeText(RegisterActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    }else{
                                                        ProgressSettings();
                                                        Snackbar.make(RegisterLayout, Objects.requireNonNull(task.getException().getMessage()), Snackbar.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }

                                    }else{
                                        ProgressSettings();
                                        Snackbar.make(RegisterLayout, Objects.requireNonNull(task.getException().getMessage()), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Snackbar.make(RegisterLayout, "Şifreler uyuşmuyor.", Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        inputUsername.setError("Lütfen geçerli bir şifre giriniz.");
                    }
                }else{
                    inputUsername.setError("Lütfen geçerli bir şifre giriniz.");
                }
            }else{
                inputUsername.setError("Lütfen geçerli bir e-mail giriniz.");
            }
        }else{
            inputUsername.setError("Lütfen geçerli bir isim giriniz.");
        }
    }

    private void ProgressSettings(){
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
    }
}