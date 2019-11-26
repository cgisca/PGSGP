package org.godotengine.godot;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.stats.PlayerStats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PlayerStatsController {

    private Activity activity;
    private ConnectionController connectionController;
    private GodotCallbacksUtils godotCallbacksUtils;

    public PlayerStatsController(Activity activity, ConnectionController connectionController, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.connectionController = connectionController;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public void checkPlayerStats(boolean forceRefresh) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected() && googleSignInAccount != null) {
            Games.getPlayerStatsClient(activity, googleSignInAccount)
                    .loadPlayerStats(forceRefresh)
                    .addOnCompleteListener(new OnCompleteListener<AnnotatedData<PlayerStats>>() {
                        @Override
                        public void onComplete(@NonNull Task<AnnotatedData<PlayerStats>> task) {
                            AnnotatedData<PlayerStats> result = task.getResult();
                            if (task.isSuccessful() && result != null && result.get() != null) {
                                PlayerStats stats = result.get();
                                float averageSessionLength = stats.getAverageSessionLength();
                                float daysSinceLastPlayed = stats.getDaysSinceLastPlayed();
                                float numberOfPurchases = stats.getNumberOfPurchases();
                                float numberOfSessions = stats.getNumberOfSessions();
                                float sessionPercentile = stats.getSessionPercentile();
                                float spendPercentile = stats.getSpendPercentile();
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_STATS_LOADED,
                                        new Object[]{
                                                new float[]{
                                                        averageSessionLength,
                                                        daysSinceLastPlayed,
                                                        numberOfPurchases,
                                                        numberOfSessions,
                                                        sessionPercentile,
                                                        spendPercentile
                                                }
                                        });
                            } else {
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_STATS_LOADED_FAILED, new Object[]{});
                            }
                        }
                    });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_STATS_LOADED_FAILED, new Object[]{});
        }
    }

}
