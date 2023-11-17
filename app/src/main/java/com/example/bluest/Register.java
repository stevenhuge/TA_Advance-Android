package com.example.bluest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private EditText etUsername, etNumberphone, etPassword;
    private Button btnRegister, btnKembali;
    private ProgressBar bar;
    private  EditText etCodeOTP;
    private Button btnVeriffication;
    private LinearLayout layoutOTP;
    private FirebaseAuth mAuth;
    private String verifikasiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etNumberphone = findViewById(R.id.etNumberphone);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnKembali = findViewById(R.id.btnKembali);

//        Verifikasi OTP disini
        btnVeriffication = findViewById(R.id.btnVeriffication);
        bar = findViewById(R.id.bar);
        etCodeOTP = findViewById(R.id.etCodeOTP);

//       Linear Layout OTP disini
        layoutOTP = findViewById(R.id.layoutOTP);

//       Implementasi Firebase
        mAuth = FirebaseAuth.getInstance();

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String numberPhone = etNumberphone.getText().toString();
                String password = etPassword.getText().toString();

                if (!(username.isEmpty() || numberPhone.isEmpty() || password.isEmpty())){
                    kodeOTP(numberPhone);
                    bar.setVisibility(View.VISIBLE);
                    layoutOTP.setVisibility(View.VISIBLE);
                    reset();
                } else {
                    Toast.makeText(getApplicationContext(), "Ada data yang masih kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVeriffication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = etCodeOTP.getText().toString();

                if(code.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Kode OTP Salah", Toast.LENGTH_SHORT).show();
                } else {
                    verifikasiOTP(code);
                }
            }
        });

    }

    private void verifikasiOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifikasiId, code);
        loginByCredential(credential);
    }


    private void loginByCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                    }
                });
    }

    private void kodeOTP(String numberPhone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+62" + numberPhone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verifikasi Gagal!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);

            verifikasiId = s;
            Toast.makeText(getApplicationContext(), "Mengirim Kode OTP", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.VISIBLE);
        }
    };
    private void reset() {
        etUsername.setText("");
        etNumberphone.setText("");
        etPassword.setText("");
    }
}