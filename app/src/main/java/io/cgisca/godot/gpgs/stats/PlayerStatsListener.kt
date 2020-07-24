package io.cgisca.godot.gpgs.stats

interface PlayerStatsListener {
    fun onPlayerStatsLoaded(statsJson: String)
    fun onPlayerStatsLoadingFailed()
}