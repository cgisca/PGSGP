package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInController {
    public final static int RC_SIGN_IN = 77;

    private Activity activity;
    private GodotCallbacksUtils godotCallbacksUtils;
    private ConnectionController connectionController;

    public SignInController(Activity activity, GodotCallbacksUtils godotCallbacksUtils, ConnectionController connectionController) {
        this.activity = activity;
        this.godotCallbacksUtils = godotCallbacksUtils;
        this.connectionController = connectionController;
    }

    public void signIn(final GoogleSignInClient googleSignInClient) {
        if (connectionController.isConnected()) {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
        } else {
            googleSignInClient
                    .silentSignIn()
                    .addOnCompleteListener(activity, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
                            } else {
                                Intent intent = googleSignInClient.getSignInIntent();
                                activity.startActivityForResult(intent, RC_SIGN_IN);
                            }
                        }
                    });
        }
    }

    public void onSignInActivityResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_FAILED, new Object[]{googleSignInResult.getStatus().getStatusCode()});
        }
    }

    public void signOut(GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGN_OUT_SUCCESS, new Object[]{});
                } else {
                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGN_OUT_FAILED, new Object[]{});
                }
            }
        });
    }
}
