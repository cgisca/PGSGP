package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class AchievementsController {

    public static final int RC_ACHIEVEMENT_UI = 9003;

    private Activity activity;
    private ConnectionController connectionController;
    private GodotCallbacksUtils godotCallbacksUtils;

    public AchievementsController(Activity activity, ConnectionController connectionController, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.connectionController = connectionController;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public void unlockAchievement(String achievementName) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).unlock(achievementName);
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_UNLOCKED, new Object[]{achievementName});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_UNLOCKED_FAILED, new Object[]{achievementName});
        }
    }

    public void revealAchievement(String achievementName) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).reveal(achievementName);
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_REVEALED, new Object[]{achievementName});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_REVEALED_FAILED, new Object[]{achievementName});
        }
    }

    public void incrementAchievement(String achievementName, int step) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).increment(achievementName, step);
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_INCREMENTED, new Object[]{achievementName});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.ACHIEVEMENT_INCREMENTED_FAILED, new Object[]{achievementName});
        }
    }

    public void showAchievements() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount)
                    .getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            activity.startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                        }
                    });
        }
    }
}
