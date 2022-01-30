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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chat_mobil_final.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {

    private ProgressDialog mProgress;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //UI
    private LinearLayout mLinearLayout;
    private RelativeLayout mRelativeLayout;
    private Button BtnSignIn;
    private TextInputLayout inputEmail, inputPassword;
    private EditText et_Email, et_Password;
    private String txtEmail, txtPassword;

    void init(){
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //UI
        mLinearLayout = (LinearLayout) findViewById(R.id.SignInLayout);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.GoToRegisterLayoutText);

        inputEmail = (TextInputLayout) findViewById(R.id.inputSignInEmail);
        inputPassword = (TextInputLayout) findViewById(R.id.inputSignInPassword);

        et_Email = (EditText) findViewById(R.id.et_SignInEmail);
        et_Password = (EditText) findViewById(R.id.et_SignInPassword);

        BtnSignIn = (Button) findViewById(R.id.BtnSignIn);

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_SignIn();
            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToRegister();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();

        if(mUser != null){
            finish();
            startActivity(new Intent(SignInActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void btn_SignIn(){
        txtEmail = et_Email.getText().toString();
        txtPassword = et_Password.getText().toString();

        if(!TextUtils.isEmpty(txtEmail)){
            if(!TextUtils.isEmpty(txtPassword)){
                mProgress = new ProgressDialog(this);
                mProgress.setTitle("Giriş yapılıyor...");
                mProgress.show();

                mAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            ProgressSettings();
                            Toast.makeText(SignInActivity.this, "Giriş Başarılı.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        }else{
                            ProgressSettings();
                            Snackbar.make(mLinearLayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                inputPassword.setError("Lütfen geçerli bir şifre giriniz.");
            }
        }else{
            inputEmail.setError("Lütfen geçerli bir E-mail adresi giriniz.");
        }
    }

    private void GoToRegister(){
        startActivity(new Intent(SignInActivity.this, RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void ProgressSettings(){
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
    }
}