package com.example.todoapp.view.activity.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.todoapp.R;
import com.example.todoapp.databinding.ActivityLoginBinding;
import com.example.todoapp.databinding.ActivityMapsBinding;
import com.example.todoapp.databinding.ActivityRetrievedLayoutBinding;
import com.example.todoapp.view.activity.GoogleMap.MapActivity;
import com.example.todoapp.view.activity.HomePage.HomeActivity;
import com.example.todoapp.view.activity.MainActivity.MainActivity;
import com.example.todoapp.view.activity.Register.RegisterActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Toolbar toolbar;
    private EditText LoginEmail, LoginPwd;
    private Button LoginBtn;
    private TextView LoginTextQes;
    private ProgressDialog Loader;
    private TextView text_email;
    private TextView txt_pass;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private FirebaseAuth.AuthStateListener authStateListener;

    private LoginButton FbLoginButton;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

       ActivityLoginBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //setContentView(R.layout.activity_login);
        Loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        toolbar = bind.loginToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");


        //
        //bind.setHandler(this);

//integrate fb
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        bind.fbLoginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        ;
        bind.fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");

                                  // bind.fbName.setText("User ID: " +name );
                                    bind.fbEmail.setText("User Email: " + email);
                                    // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {

                bind.fbEmail.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                bind.fbEmail.setText("Login attempt failed.");
            }
        });


        bind.loginPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //map

        bind.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = bind.loginEmail.getText().toString().trim();
                String password = bind.loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    bind.loginEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    bind.loginPassword.setError("Password is required");
                    return;
                } else {
                    Loader.setMessage("Login in progress");
                    Loader.setCancelable(false);
                    // Loader.setCanceledOnTouchOutside(false);
                    Loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Login failed" + error, Toast.LENGTH_SHORT).show();

                            }
                            Loader.dismiss();
                        }

                    });


                }


            }
        });

//Map
        bind.idMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }




    //



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }




}