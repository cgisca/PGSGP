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
import org.json.JSONException;
import org.json.JSONObject;

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
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getPlayerStatsClient(activity, googleSignInAccount)
                    .loadPlayerStats(forceRefresh)
                    .addOnCompleteListener(new OnCompleteListener<AnnotatedData<PlayerStats>>() {
                        @Override
                        public void onComplete(@NonNull Task<AnnotatedData<PlayerStats>> task) {
                            AnnotatedData<PlayerStats> result = task.getResult();
                            if (task.isSuccessful() && result != null && result.get() != null) {
                                PlayerStats stats = result.get();
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("avg_session_length", stats.getAverageSessionLength());
                                    json.put("days_last_played", stats.getDaysSinceLastPlayed());
                                    json.put("purchases", stats.getNumberOfPurchases());
                                    json.put("sessions", stats.getNumberOfSessions());
                                    json.put("session_percentile", stats.getSessionPercentile());
                                    json.put("spend_percentile", stats.getSpendPercentile());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_STATS_LOADED,
                                        new Object[]{json.toString()});
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
