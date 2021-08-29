package io.cgisca.godot.gpgs.leaderboards

interface LeaderBoardsListener {
    fun onCurrentPlayerLeaderBoardScoreLoadingFailed(leaderboardId: String)
    fun onCurrentPlayerLeaderBoardScoreLoaded(leaderboardId: String, scoreJson: String)
    fun onLeaderBoardScoreSubmitted(leaderboardId: String)
    fun onLeaderBoardScoreSubmittingFailed(leaderboardId: String)
}