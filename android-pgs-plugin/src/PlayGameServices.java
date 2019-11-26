package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class PlayGameServices extends Godot.SingletonBase {

    private Activity appActivity;
    private Godot activity;

    private SignInController signInController;
    private GodotCallbacksUtils godotCallbacksUtils;
    private ConnectionController connectionController;
    private AchievementsController achievementsController;
    private LeaderboardsController leaderboardsController;
    private EventsController eventsController;
    private PlayerStatsController playerStatsController;

    private GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
    private GoogleSignInClient googleSignInClient;

    public PlayGameServices(Activity appActivity) {
        this.appActivity = appActivity;
        this.activity = (Godot) appActivity;

        godotCallbacksUtils = new GodotCallbacksUtils();
        connectionController = new ConnectionController(appActivity, signInOptions);
        signInController = new SignInController(appActivity, godotCallbacksUtils, connectionController);
        achievementsController = new AchievementsController(appActivity, connectionController, godotCallbacksUtils);
        leaderboardsController = new LeaderboardsController(appActivity, godotCallbacksUtils, connectionController);
        eventsController = new EventsController(appActivity, connectionController, godotCallbacksUtils);
        playerStatsController = new PlayerStatsController(appActivity, connectionController, godotCallbacksUtils);

        googleSignInClient = GoogleSignIn.getClient(appActivity, signInOptions);

        registerClass("PlayGameServices", new String[]
                {
                        "init",
                        "sign_in",
                        "sign_out",
                        "is_player_connected",
                        "show_achievements",
                        "unlock_achievement",
                        "reveal_achievement",
                        "increment_achievement",
                        "show_leaderboard",
                        "submit_leaderboard_score",
                        "submit_event",
                        "load_events",
                        "load_events_by_id",
                        "load_player_stats"
                });
    }

    static public Godot.SingletonBase initialize(Activity activity) {
        return new PlayGameServices(activity);
    }

    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SignInController.RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInController.onSignInActivityResult(googleSignInResult);
        } else if (requestCode == AchievementsController.RC_ACHIEVEMENT_UI || requestCode == LeaderboardsController.RC_LEADERBOARD_UI) {
            boolean isConnected = connectionController.isConnected();
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_CONNECTED, new Object[]{isConnected});
        }
    }

    public void init(final int instanceId) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                godotCallbacksUtils.setGodotInstanceId(instanceId);
            }
        });
    }

    public void sign_in() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signInController.signIn(googleSignInClient);
            }
        });
    }

    public void sign_out() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signInController.signOut(googleSignInClient);
            }
        });
    }

    public void is_player_connected() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_CONNECTED, new Object[]{connectionController.isConnected()});
            }
        });
    }

    public void show_achievements() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                achievementsController.showAchievements();
            }
        });
    }

    public void unlock_achievement(final String achievement) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                achievementsController.unlockAchievement(achievement);
            }
        });
    }

    public void reveal_achievement(final String achievement) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                achievementsController.revealAchievement(achievement);
            }
        });
    }

    public void increment_achievement(final String achievement, final int step) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                achievementsController.incrementAchievement(achievement, step);
            }
        });
    }

    public void show_leaderboard(final String leaderboardId) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leaderboardsController.showLeaderboard(leaderboardId);
            }
        });
    }

    public void submit_leaderboard_score(final String leaderboardId, final int score) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leaderboardsController.submitScore(leaderboardId, score);
            }
        });
    }
    
    public void submit_event(final String eventId, final int incrementBy) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eventsController.submitEvent(eventId, incrementBy);
            }
        });
    }

    public void load_events() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eventsController.loadEvents();
            }
        });
    }

    public void load_events_by_id(final String[] ids) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eventsController.loadEventById(ids);
            }
        });
    }

    public void load_player_stats(final boolean forceRefresh) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerStatsController.checkPlayerStats(forceRefresh);
            }
        });
    }
}