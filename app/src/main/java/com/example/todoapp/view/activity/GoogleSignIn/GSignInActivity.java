package com.example.todoapp.view.activity.GoogleSignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todoapp.R;
import com.example.todoapp.databinding.ActivityHomeBinding;
import com.example.todoapp.databinding.ActivityLoginBinding;
import com.example.todoapp.databinding.GoogleSigninBinding;
import com.example.todoapp.view.activity.Login.LoginActivity;
import com.example.todoapp.view.activity.MainActivity.MainActivity;
import com.example.todoapp.view.activity.Utils.Constant;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.jetbrains.annotations.NotNull;
 public  class GSignInActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {

    ImageView profileImage;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
     Context context;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.google_signin);
        GoogleSigninBinding bindGoogle = DataBindingUtil.setContentView(this, R.layout.google_signin);

        profileImage = bindGoogle.profileImage;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
//
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Constant.GoogleApiMethod(context);
        bindGoogle.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    gotoMainActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result){

        GoogleSigninBinding bindGoogle=DataBindingUtil.setContentView(this, R.layout.google_signin);
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
           bindGoogle.name.setText(account.getDisplayName());
            bindGoogle.email.setText(account.getEmail());
            bindGoogle.userId.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(profileImage);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"image not found", Toast.LENGTH_LONG).show();
            }

        }else{
            gotoMainActivity();
        }
    }

    private void gotoMainActivity(){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }
}