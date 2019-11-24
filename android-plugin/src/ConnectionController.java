package org.godotengine.godot;

import android.app.Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ConnectionController {

    private Activity activity;
    private GoogleSignInOptions signInOptions;

    public ConnectionController(Activity activity, GoogleSignInOptions signInOptions) {
        this.activity = activity;
        this.signInOptions = signInOptions;
    }

    public boolean isConnected() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        return GoogleSignIn.hasPermissions(googleSignInAccount, signInOptions.getScopeArray());
    }
}
