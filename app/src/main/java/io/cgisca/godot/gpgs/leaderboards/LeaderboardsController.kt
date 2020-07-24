package io.cgisca.godot.gpgs.leaderboards

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import io.cgisca.godot.gpgs.ConnectionController

class LeaderboardsController(
    private val activity: Activity,
    private val leaderBoardsListener: LeaderBoardsListener,
    private val connectionController: ConnectionController
) {

    companion object {
        const val RC_LEADERBOARD_UI = 9004
    }

    fun submitScore(leaderboardId: String, score: Int) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount).submitScore(leaderboardId, score.toLong())
            leaderBoardsListener.onLeaderBoardScoreSubmitted(leaderboardId)
        } else {
            leaderBoardsListener.onLeaderBoardScoreSubmittingFailed(leaderboardId)
        }
    }

    fun showLeaderboard(leaderboardId: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount)
                .getLeaderboardIntent(leaderboardId)
                .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_LEADERBOARD_UI) }
        }
    }

    fun showAllLeaderboards() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getLeaderboardsClient(activity, googleSignInAccount)
                .allLeaderboardsIntent
                .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_LEADERBOARD_UI) }
        }
    }
}