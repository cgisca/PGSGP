package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;

import com.godot.game.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class PlayGameServices extends Godot.SingletonBase {

    private Activity appActivity;
    private Godot activity;
    private GodotCallbacksUtils godotCallbacksUtils = new GodotCallbacksUtils();
    private SignInHelper signInHelper;

    private final GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    private GoogleSignInClient googleSignInClient;


    public PlayGameServices(Activity appActivity) {
        this.appActivity = appActivity;
        this.activity = (Godot) appActivity;
        this.signInHelper = new SignInHelper(appActivity, godotCallbacksUtils);
        this.googleSignInClient = GoogleSignIn.getClient(appActivity, signInOptions);

        registerClass("PlayGameServices", new String[]
                {
                        "init",
                        "sign_in",
                        "sign_out"
                });
    }

    static public Godot.SingletonBase initialize(Activity activity) {
        return new PlayGameServices(activity);
    }

    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SignInHelper.RC_SIGN_IN) {
            signInHelper.onSignInActivityResult(data);
        }
    }

    public void init(final int instanceId) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signInHelper.setGodotInstanceId(instanceId);
            }
        });
    }

    public void sign_in() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signInHelper.silentSignIn(signInOptions, googleSignInClient);
            }
        });
    }

    public void sign_out() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signInHelper.signOut(googleSignInClient);
            }
        });
    }
}
