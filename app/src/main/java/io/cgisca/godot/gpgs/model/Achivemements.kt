package io.cgisca.godot.gpgs.model

import com.google.gson.annotations.SerializedName

data class AchievementInfo(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("state") val state: Int,
        @SerializedName("type") val type: Int,
        @SerializedName("current_steps") val currentSteps: Int?,
        @SerializedName("total_steps") val totalSteps: Int?,
        @SerializedName("xp") val xp: Long,
)
