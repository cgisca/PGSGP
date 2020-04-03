package org.godotengine.godot;

import org.godotengine.godot.GodotLib;

public class GodotCallbacksUtils {

    /**
     * Sign in callbacks
     */
    public static final String SIGNIN_SUCCESSFUL = "_on_sign_in_success";
    public static final String SIGNIN_FAILED = "_on_sign_in_failed";
    public static final String SIGN_OUT_SUCCESS = "_on_sign_out_success";
    public static final String SIGN_OUT_FAILED = "_on_sign_out_failed";
    /**
     * Connection callbacks
     */
    public static final String PLAYER_CONNECTED = "_on_player_is_already_connected";
    /**
     * Achievements callbacks
     */
    public static final String ACHIEVEMENT_UNLOCKED = "_on_achievement_unlocked";
    public static final String ACHIEVEMENT_UNLOCKED_FAILED = "_on_achievement_unlocking_failed";
    public static final String ACHIEVEMENT_REVEALED = "_on_achievement_revealed";
    public static final String ACHIEVEMENT_REVEALED_FAILED = "_on_achievement_revealing_failed";
    public static final String ACHIEVEMENT_INCREMENTED = "_on_achievement_incremented";
    public static final String ACHIEVEMENT_INCREMENTED_FAILED = "_on_achievement_incrementing_failed";
    /**
     * Leaderboards callbacks
     */
    public static final String LEADERBOARD_SCORE_SUBMITTED = "_on_leaderboard_score_submitted";
    public static final String LEADERBOARD_SCORE_SUBMITTED_FAILED = "_on_leaderboard_score_submitting_failed";
    /**
     * Events callbacks
     */
    public static final String EVENT_SUBMITTED = "_on_event_submitted";
    public static final String EVENT_SUBMITTED_FAILED = "_on_event_submitting_failed";
    public static final String EVENTS_LOADED = "_on_events_loaded";
    public static final String EVENTS_EMPTY = "_on_events_empty";
    public static final String EVENTS_LOADED_FAILED = "_on_events_loading_failed";
    /**
     * Player stats callbacks
     */
    public static final String PLAYER_STATS_LOADED = "_on_player_stats_loaded";
    public static final String PLAYER_STATS_LOADED_FAILED = "_on_player_stats_loading_failed";
    /**
     * Saved games callbacks
     */
    public static final String SAVED_GAME_SUCCESS = "_on_game_saved_success";
    public static final String SAVED_GAME_FAILED = "_on_game_saved_fail";
    public static final String SAVED_GAME_LOAD_SUCCESS = "_on_game_load_success";
    public static final String SAVED_GAME_LOAD_FAIL = "_on_game_load_fail";
    public static final String SAVED_GAME_CREATE_SNAPSHOT = "_on_create_new_snapshot";

    private int godotInstanceId;

    public void setGodotInstanceId(int godotInstanceId) {
        this.godotInstanceId = godotInstanceId;
    }

    public void invokeGodotCallback(String callbackName, Object[] args) {
        GodotLib.calldeferred(godotInstanceId, callbackName, args);
    }
}
