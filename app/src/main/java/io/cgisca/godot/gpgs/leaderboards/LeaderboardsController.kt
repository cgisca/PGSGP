package io.cgisca.godot.gpgs.leaderboards

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_FRIENDS
import com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_PUBLIC
import com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_ALL_TIME
import com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_DAILY
import com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_WEEKLY
import com.google.gson.Gson
import io.cgisca.godot.gpgs.ConnectionController
import io.cgisca.godot.gpgs.model.LeaderboardScore
import java.util.Locale

class LeaderboardsController(
    private val activity: Activity,
    private val leaderBoardsListener: LeaderBoardsListener,
    private val connectionController: ConnectionController
) {

    companion object {
        const val RC_LEADERBOARD_UI = 9004
    }

    fun retrieveLeaderboardScore(leaderboardId: String, span: String, leaderboardCollection: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)

        var collection = COLLECTION_PUBLIC

        if (leaderboardCollection.lowercase(Locale.ROOT).contains("friends")) {
            collection = COLLECTION_FRIENDS
        }

        var finalSpan = TIME_SPAN_ALL_TIME;

        if (span.lowercase(Locale.ROOT).contains("weekly")) {
            finalSpan = TIME_SPAN_WEEKLY
        } else if (span.lowercase(Locale.ROOT).contains("daily")) {
            finalSpan = TIME_SPAN_DAILY
        }

        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Log.i("godot", "-------------------\n\n THE LEADERBOARD:\n ${leaderboardId}\n\n")

            Games.getLeaderboardsClient(activity, googleSignInAccount)
                .loadCurrentPlayerLeaderboardScore(leaderboardId, finalSpan, collection)
                .addOnSuccessListener { lbScore ->
//                    val scores = lbScores.get();

                    val leaderboardScore = LeaderboardScore (
                        -1, 0, "Unknown"
                    )

                    if (lbScore != null) {
                        val score = lbScore.get();
                        if (score != null) {
                            Log.i("godot", "-------------------\n\n THE RESULT:\n ${score.rank}\n\n")
                        }

                        if (score != null) {
                            leaderboardScore.rank = score.rank
                            leaderboardScore.score = score.rawScore
                            leaderboardScore.scoreHolder = score.scoreHolderDisplayName
                        }
                    }

                    leaderBoardsListener.onCurrentPlayerLeaderBoardScoreLoaded(leaderboardId, Gson().toJson(leaderboardScore))
                }
                .addOnFailureListener {reason ->
                    Log.i("godot", "-------------------\n\n FAILURE REASON:\n ${reason}\n\n")
                    Log.i("godot", "-------------------\n\n FAILURE REASON:\n ${reason.message}\n\n")

                    leaderBoardsListener.onCurrentPlayerLeaderBoardScoreLoadingFailed(leaderboardId)
                }

        }

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