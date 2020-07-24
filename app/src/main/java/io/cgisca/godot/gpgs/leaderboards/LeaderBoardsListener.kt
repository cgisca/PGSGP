package io.cgisca.godot.gpgs.leaderboards

interface LeaderBoardsListener {
    fun onLeaderBoardScoreSubmitted(leaderboardId: String)
    fun onLeaderBoardScoreSubmittingFailed(leaderboardId: String)
}