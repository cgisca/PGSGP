package org.godotengine.godot;

import com.godot.game.R;
import org.godotengine.godot.GodotLib;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.SnapshotMetadata;

import java.math.BigInteger;
import java.util.Random;

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
    private SavedGamesController savedGamesController;

    private GoogleSignInClient googleSignInClient;

    public PlayGameServices(Activity appActivity) {
        this.appActivity = appActivity;
        this.activity = (Godot) appActivity;

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
                        "show_all_leaderboards",
                        "submit_leaderboard_score",
                        "submit_event",
                        "load_events",
                        "load_events_by_id",
                        "load_player_stats",
                        "show_saved_games",
                        "save_snapshot",
                        "load_snapshot"
                });
    }

    private void initializePlayGameServices(boolean enableSaveGamesFunctionality) {
        GoogleSignInOptions signInOptions = null;
         
        if (enableSaveGamesFunctionality) {
            GoogleSignInOptions.Builder signInOptionsBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
            signInOptionsBuilder.requestScopes(Drive.SCOPE_APPFOLDER).requestId();
            signInOptions = signInOptionsBuilder.build();
        } else {
            signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        }
         
        godotCallbacksUtils = new GodotCallbacksUtils();
        connectionController = new ConnectionController(appActivity, signInOptions, godotCallbacksUtils);
        signInController = new SignInController(appActivity, godotCallbacksUtils, connectionController);
        achievementsController = new AchievementsController(appActivity, connectionController, godotCallbacksUtils);
        leaderboardsController = new LeaderboardsController(appActivity, godotCallbacksUtils, connectionController);
        eventsController = new EventsController(appActivity, connectionController, godotCallbacksUtils);
        playerStatsController = new PlayerStatsController(appActivity, connectionController, godotCallbacksUtils);
        savedGamesController = new SavedGamesController(appActivity, godotCallbacksUtils, connectionController);

        googleSignInClient = GoogleSignIn.getClient(appActivity, signInOptions);
    }

    static public Godot.SingletonBase initialize(Activity activity) {
        return new PlayGameServices(activity);
    }

    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SignInController.RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInController.onSignInActivityResult(googleSignInResult);
        } else if (requestCode == AchievementsController.RC_ACHIEVEMENT_UI || requestCode == LeaderboardsController.RC_LEADERBOARD_UI) {
            Pair<Boolean, String> isConnected = connectionController.isConnected();
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_CONNECTED, new Object[]{isConnected.first, isConnected.second});
        } else if (requestCode == SavedGamesController.RC_SAVED_GAMES) {
            if (data != null) {
                if (data.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA)) {
                    SnapshotMetadata snapshotMetadata = data.getParcelableExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA);
                    if (snapshotMetadata != null) {
                        savedGamesController.loadSnapshot(snapshotMetadata.getUniqueName());
                    }
                } else if (data.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_NEW)) {
                    String unique = new BigInteger(281, new Random()).toString(13);
                    String currentSaveName = appActivity.getString(R.string.default_game_name) + unique;

                    savedGamesController.createNewSnapshot(currentSaveName);
                }
            }
        }
    }


    public void init(final int instanceId, final boolean enablePopups, final boolean enableSaveGames) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializePlayGameServices(enableSaveGames);

                godotCallbacksUtils.setGodotInstanceId(instanceId);
                signInController.setShowPopups(enablePopups);
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
                connectionController.checkIsConnected();
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

    public void show_all_leaderboards() {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leaderboardsController.showAllLeaderboards();
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
    
    public void show_saved_games(final String title, final boolean allowAddBtn, final boolean allowDeleteBtn, final int maxGamesListItems) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                savedGamesController.showSavedGamesUI(title, allowAddBtn, allowDeleteBtn, maxGamesListItems);
            }
        });
    }

    public void save_snapshot(final String title, final String dataToSave, final String description) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                savedGamesController.saveSnapshot(title, dataToSave, description);
            }
        });
    }

    public void load_snapshot(final String title) {
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                savedGamesController.loadSnapshot(title);
            }
        });
    }
}
