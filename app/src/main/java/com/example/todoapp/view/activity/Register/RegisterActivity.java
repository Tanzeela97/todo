package com.example.todoapp.view.activity.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.R;
import com.example.todoapp.databinding.ActivityHomeBinding;
import com.example.todoapp.databinding.ActivityRegisterBinding;
import com.example.todoapp.view.activity.HomePage.HomeActivity;
import com.example.todoapp.view.activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {





    private Toolbar toolbar;

    private EditText RegEmail, RegPwd;
    private Button RegBtn;
    private TextView RegnQn;
    private FirebaseAuth mAuth;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_register);


        ActivityRegisterBinding bindReg = DataBindingUtil.setContentView(this, R.layout.activity_register);
        toolbar = bindReg.RegistrationToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");

        mAuth =FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);



        bindReg.registerPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        bindReg.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = bindReg.registrationEmail.getText().toString().trim();
                String password = bindReg.registerPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    bindReg.registrationEmail.setError("email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    bindReg.registerPwd.setError("Password required");
                    return;
                }else {
                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else {
                                String error = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Registration failed" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }

                        }
                    });
                }



            }
        });
    }

    }
