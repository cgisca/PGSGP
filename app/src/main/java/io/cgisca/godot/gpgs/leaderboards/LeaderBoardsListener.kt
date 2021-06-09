package io.cgisca.godot.gpgs.leaderboards

interface LeaderBoardsListener {
    fun onCurrentPlayerLeaderBoardScoreLoadingFailed()
    fun onCurrentPlayerLeaderBoardScoreLoaded(scoreJson: String)
    fun onLeaderBoardScoreSubmitted(leaderboardId: String)
    fun onLeaderBoardScoreSubmittingFailed(leaderboardId: String)
}