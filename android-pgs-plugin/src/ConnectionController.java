package org.godotengine.godot;

import android.app.Activity;
import android.util.Pair;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ConnectionController {

    private Activity activity;
    private GoogleSignInOptions signInOptions;
    private GodotCallbacksUtils godotCallbacksUtils;

    public ConnectionController(Activity activity, GoogleSignInOptions signInOptions, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.signInOptions = signInOptions;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public Pair<Boolean, String> isConnected() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        
        String accId = "";
        if (googleSignInAccount != null && googleSignInAccount.getId() != null) {
            accId = googleSignInAccount.getId();
        }
        return new Pair<>(GoogleSignIn.hasPermissions(googleSignInAccount, signInOptions.getScopeArray()), accId);
    }

    public void checkIsConnected() {
        Pair<Boolean, String> pair = isConnected();
        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_CONNECTED, new Object[]{pair.first, pair.second});
    }
}