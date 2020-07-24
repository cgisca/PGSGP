package io.cgisca.godot.gpgs.stats

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import io.cgisca.godot.gpgs.ConnectionController
import org.json.JSONException
import org.json.JSONObject

class PlayerStatsController(
    private val activity: Activity,
    private val playerStatsListener: PlayerStatsListener,
    private val connectionController: ConnectionController
) {

    fun checkPlayerStats(forceRefresh: Boolean) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getPlayerStatsClient(activity, googleSignInAccount)
                .loadPlayerStats(forceRefresh)
                .addOnCompleteListener { task ->
                    val result = task.result
                    if (task.isSuccessful && result != null && result.get() != null) {
                        val stats = result.get()
                        val json = JSONObject()
                        try {
                            json.put("avg_session_length", stats!!.averageSessionLength.toDouble())
                            json.put("days_last_played", stats.daysSinceLastPlayed)
                            json.put("purchases", stats.numberOfPurchases)
                            json.put("sessions", stats.numberOfSessions)
                            json.put("session_percentile", stats.sessionPercentile.toDouble())
                            json.put("spend_percentile", stats.spendPercentile.toDouble())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        playerStatsListener.onPlayerStatsLoaded(json.toString())
                    } else {
                        playerStatsListener.onPlayerStatsLoadingFailed()
                    }
                }
        } else {
            playerStatsListener.onPlayerStatsLoadingFailed()
        }
    }
}