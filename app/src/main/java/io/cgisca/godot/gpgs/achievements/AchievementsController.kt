package io.cgisca.godot.gpgs.achievements

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import io.cgisca.godot.gpgs.ConnectionController

class AchievementsController(
    private val activity: Activity,
    private val achievementsListener: AchievementsListener,
    private val connectionController: ConnectionController
) {

    companion object {
        const val RC_ACHIEVEMENT_UI = 9003
    }

    fun unlockAchievement(achievementName: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).unlock(achievementName)
            achievementsListener.onAchievementUnlocked(achievementName)
        } else {
            achievementsListener.onAchievementUnlockingFailed(achievementName)
        }
    }

    fun revealAchievement(achievementName: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).reveal(achievementName)
            achievementsListener.onAchievementRevealed(achievementName)
        } else {
            achievementsListener.onAchievementRevealingFailed(achievementName)
        }
    }

    fun incrementAchievement(achievementName: String, step: Int) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).increment(achievementName, step)
            achievementsListener.onAchievementIncremented(achievementName)
        } else {
            achievementsListener.onAchievementIncrementingFailed(achievementName)
        }
    }

    fun showAchievements() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount)
                .achievementsIntent
                .addOnSuccessListener { intent ->
                    activity.startActivityForResult(intent, RC_ACHIEVEMENT_UI)
                }
        }
    }
}