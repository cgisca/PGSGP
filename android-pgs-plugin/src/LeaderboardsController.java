package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class LeaderboardsController {

    public static final int RC_LEADERBOARD_UI = 9004;

    private Activity activity;
    private GodotCallbacksUtils godotCallbacksUtils;
    private ConnectionController connectionController;

    public LeaderboardsController(Activity activity, GodotCallbacksUtils godotCallbacksUtils, ConnectionController connectionController) {
        this.activity = activity;
        this.godotCallbacksUtils = godotCallbacksUtils;
        this.connectionController = connectionController;
    }

    public void submitScore(String leaderboardId, int score) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount).submitScore(leaderboardId, score);
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.LEADERBOARD_SCORE_SUBMITTED, new Object[]{leaderboardId});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.LEADERBOARD_SCORE_SUBMITTED_FAILED, new Object[]{leaderboardId});
        }
    }

    public void showLeaderboard(String leaderboardId) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount)
                    .getLeaderboardIntent(leaderboardId)
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            activity.startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }
    }

    public void showAllLeaderboards() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount)
                    .getAllLeaderboardsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            activity.startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }
    }
}
