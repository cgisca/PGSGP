package io.cgisca.godot.gpgs.achievements

interface AchievementsListener {
    fun onAchievementUnlocked(achievementName: String)
    fun onAchievementUnlockingFailed(achievementName: String)
    fun onAchievementRevealed(achievementName: String)
    fun onAchievementRevealingFailed(achievementName: String)
    fun onAchievementIncremented(achievementName: String)
    fun onAchievementIncrementingFailed(achievementName: String)
}