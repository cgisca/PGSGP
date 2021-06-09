package io.cgisca.godot.gpgs.model

import com.google.gson.annotations.SerializedName

data class LeaderboardScore(
    @SerializedName("rank") var rank: Long,
    @SerializedName("score") var score: Long,
    @SerializedName("scoreHolder") var scoreHolder: String,
)
