package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText username, fullName, email, password, reenter_password;
    Button register;
    TextView txt_login;
    CheckBox checked;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        reenter_password = findViewById(R.id.reenter_password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        checked = findViewById(R.id.checked);
        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(Register.this);
                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                String str_username = username.getText().toString();
                String str_fullName = fullName.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username)) {
                    Toast.makeText(getApplicationContext(), "Enter User Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(str_fullName)) {
                    Toast.makeText(getApplicationContext(), "Enter Full Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (str_password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must contain 6 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    register(str_username, str_fullName, str_email, str_password);
                }

            }
        });


        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reenter_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reenter_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }

    private void register(final String username, final String fullName, String email, String password) {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        progressDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(Register.this, error, Toast.LENGTH_SHORT).show();

                    } else {

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullName", fullName);
                            hashMap.put("bio", "");
                            hashMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagram-55419.appspot.com/o/user.jpg?" +
                                    "alt=media&token=341dec80-34de-4150-b075-1bec846fb575");

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | (Intent.FLAG_ACTIVITY_NEW_TASK));
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                    }

            });
            
    }
}