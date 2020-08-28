package io.cgisca.godot.gpgs.stats

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.gson.Gson
import io.cgisca.godot.gpgs.ConnectionController
import io.cgisca.godot.gpgs.model.PlayerStats

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
                        val playerStats = PlayerStats(
                            stats!!.averageSessionLength.toDouble(),
                            stats.daysSinceLastPlayed,
                            stats.numberOfPurchases,
                            stats.numberOfSessions,
                            stats.sessionPercentile.toDouble(),
                            stats.spendPercentile.toDouble()
                        )

                        playerStatsListener.onPlayerStatsLoaded(Gson().toJson(playerStats))
                    } else {
                        playerStatsListener.onPlayerStatsLoadingFailed()
                    }
                }
        } else {
            playerStatsListener.onPlayerStatsLoadingFailed()
        }
    }
}