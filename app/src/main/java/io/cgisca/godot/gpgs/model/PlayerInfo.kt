package io.cgisca.godot.gpgs.model

import com.google.gson.annotations.SerializedName

data class PlayerInfo(
    @SerializedName("player_id") val playerId: String,
    @SerializedName("display_name") val displayName: String,
    val name: String?,
    @SerializedName("icon_image_url") val iconImageUrl: String?,
    @SerializedName("hi_res_image_url") val hiResImageUrl: String?,
    val title: String?,
    @SerializedName("banner_image_landscape_url") val bannerImageLandscapeUrl: String?,
    @SerializedName("banner_image_portrait_url") val bannerImagePortraitUrl: String?,
    @SerializedName("level_info") val levelInfo: PlayerLevelInfo?
)

data class PlayerLevelInfo(
    @SerializedName("current_xp_total") val currentXpTotal: Long,
    @SerializedName("last_level_up_timestamp") val lastLevelUpTimestamp: Long,
    @SerializedName("current_level") val currentLevel: PlayerLevel?,
    @SerializedName("next_level") val nextLevel: PlayerLevel?
)

data class PlayerLevel(
    @SerializedName("level_number") val levelNumber: Int,
    @SerializedName("min_xp") val minXp: Long,
    @SerializedName("max_xp") val maxXp: Long
)