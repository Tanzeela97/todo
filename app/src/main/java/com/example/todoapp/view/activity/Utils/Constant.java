package com.example.todoapp.view.activity.Utils;

import android.content.Context;

import com.example.todoapp.view.activity.GoogleSignIn.GSignInActivity;
import com.example.todoapp.view.activity.MainActivity.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

public class Constant {

    public static GoogleApiClient GoogleApiMethod(Context context) {
        GoogleApiClient googleApiClient;

        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(context)
                //.enableAutoManage(context.getApplicationContext().get,context)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        return googleApiClient;
    }
}
