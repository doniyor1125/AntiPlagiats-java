package com.xorazmapp.sayhundomladastur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button kirish, sign;
    String verificationID;
    EditText phoneNumber, veriCode;

    ProgressDialog dialog;


    FirebaseAuth mAuth;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        kirish = findViewById(R.id.btn);
        sign = findViewById(R.id.sign);
        veriCode = findViewById(R.id.pass1);
        phoneNumber = findViewById(R.id.phone);
        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();



        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(veriCode.getText().toString()))
                {
                    Toast.makeText(RegActivity.this, "Tasdiqlash kodi noto'g'ri", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    verifycode(veriCode.getText().toString());
                }

            }
        });

        kirish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(phoneNumber.getText().toString()))
                {
                    Toast.makeText(RegActivity.this, "Raqam to'g'ri yozilmagan", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    dialog.setTitle("Telefon raqam tekshirilmoqda...");
                    dialog.show();
                    sentverificationcode(phoneNumber.getText().toString());
                    kirish.setEnabled(false);
                    sign.setVisibility(View.VISIBLE);
                    phoneNumber.setEnabled(false);
                    veriCode.setVisibility(View.VISIBLE);

                }

            }
        });


    }

    private void sentverificationcode(String phoneNumber) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+998"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.i("DriverActivity", "Nomer tekshirilmoqda");
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    Log.i("VerificationCompleted", "Verification completed");
                    final String code = phoneAuthCredential.getSmsCode();
                    Log.e("onVerificationCompleted: ", code);
                    if (code != null) {
                        verifycode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e("VerificationFailed", "Verification failed", e);
                    startActivity(new Intent(RegActivity.this, RegActivity.class));
                    Toast.makeText(RegActivity.this, "Tasdiqlash kodi jo'natilmadi", Toast.LENGTH_SHORT).show();
                    Log.i("DriverActivity", "Tasdiqlash kodi jo'natilmadi shekilli");
                    dialog.dismiss();
                }

                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(verificationId, token);
                    verificationID = verificationId;

                    Log.i("DriverActivity", "Tasdiqlash kodi jo'natildi");
                    dialog.dismiss();
                }
            };

    private void verifycode(String Codes) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Codes);
        signinByCredental(credential);

    }

    private void signinByCredental(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegActivity.this, "Kirish tasdiqlandi:", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegActivity.this,Welcome.class));
                            Log.i("DriverActivity", "MapsActivity ochishga start berildi");
                        }

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        progressBar = findViewById(R.id.idPBLoading);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            progressBar.setVisibility(View.GONE);
            Log.i("DriverActivity", "User tekshirildi");
            startActivity(new Intent(RegActivity.this,Welcome.class));
            finish();
        } else{
            progressBar.setVisibility(View.GONE);
        }
    }

}