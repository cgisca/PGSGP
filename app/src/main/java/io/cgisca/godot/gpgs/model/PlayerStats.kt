package io.cgisca.godot.gpgs.model

import com.google.gson.annotations.SerializedName

data class PlayerStats(
    @SerializedName("avg_session_length") val avgSessionLength: Double,
    @SerializedName("days_last_played") val daysSinceLastPlayed: Int,
    @SerializedName("purchases") val numberOfPurchases: Int,
    @SerializedName("sessions") val numberOfSessions: Int,
    @SerializedName("session_percentile") val sessionPercentile: Double,
    @SerializedName("spend_percentile") val spendPercentile: Double
)
