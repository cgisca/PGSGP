package io.cgisca.godot.gpgs.achievements

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.achievement.Achievement
import com.google.gson.Gson
import io.cgisca.godot.gpgs.ConnectionController
import io.cgisca.godot.gpgs.model.AchievementInfo

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

    fun setAchievementSteps(achievementName: String, steps: Int) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).setSteps(achievementName, steps)
            achievementsListener.onAchievementStepsSet(achievementName)
        } else {
            achievementsListener.onAchievementStepsSettingFailed(achievementName)
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

    fun loadAchievementInfo(forceReload: Boolean) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getAchievementsClient(activity, googleSignInAccount).load(forceReload)
                .addOnCompleteListener { task ->
                    val achievementData = task.result?.get()
                    if (task.isSuccessful && achievementData != null) {

                        val list = ArrayList<AchievementInfo>()
                        for(a in achievementData) {

                            val type = a.getType()
                            val current_steps = when(type == Achievement.TYPE_INCREMENTAL) {
                                true -> a.getCurrentSteps()
                                false -> null
                            }

                            val total_steps = when(type == Achievement.TYPE_INCREMENTAL) {
                                true -> a.getTotalSteps()
                                false -> null
                            }

                            list.add(AchievementInfo(
                                a.getAchievementId(),
                                a.getName(),
                                a.getDescription(),
                                a.getState(),
                                type,
                                current_steps,
                                total_steps,
                                a.getXpValue()
                            ))
                        }

                        achievementsListener.onAchievementInfoLoaded(Gson().toJson(list))
                    } else {
                        achievementsListener.onAchievementInfoLoadingFailed()
                    }
                }
        }
        else {
            achievementsListener.onAchievementInfoLoadingFailed()
        }
    }
}