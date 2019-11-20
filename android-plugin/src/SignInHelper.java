package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class SignInHelper {
    public final static int RC_SIGN_IN = 77;

    private Activity activity;
    private GodotCallbacksUtils godotCallbacksUtils;
    private int godotInstanceId;

    public SignInHelper(Activity activity, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public void setGodotInstanceId(int godotInstanceId) {
        this.godotInstanceId = godotInstanceId;
    }

    public void silentSignIn(GoogleSignInOptions signInOptions, final GoogleSignInClient googleSignInClient) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        boolean hasPermissionsGranted = GoogleSignIn.hasPermissions(googleSignInAccount, signInOptions.getScopeArray());

        if (hasPermissionsGranted) {
            GoogleSignInAccount signedInAccount = googleSignInAccount;
            godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
        } else {
            googleSignInClient
                    .silentSignIn()
                    .addOnCompleteListener(activity, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                GoogleSignInAccount signedInAccount = task.getResult();
                                godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
                            } else {
                                Intent intent = googleSignInClient.getSignInIntent();
                                activity.startActivityForResult(intent, RC_SIGN_IN);
                            }
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGNIN_FAILED, new Object[]{e.toString()});
                        }
                    });
        }
    }

    public void onSignInActivityResult(Intent data) {
        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount signedInAccount = googleSignInResult.getSignInAccount();
            godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
        } else {
            godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGNIN_FAILED, new Object[]{googleSignInResult.getStatus().getStatusCode()});
        }
    }

    public void signOut(GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGN_OUT_SUCCESS, new Object[]{});
                } else {
                    godotCallbacksUtils.invokeGodotCallback(godotInstanceId, GodotCallbacksUtils.SIGN_OUT_FAILED, new Object[]{});
                }
            }
        });
    }
}