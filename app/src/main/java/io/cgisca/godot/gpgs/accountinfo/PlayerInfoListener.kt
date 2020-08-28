package io.cgisca.godot.gpgs.accountinfo

interface PlayerInfoListener {
    fun onPlayerInfoLoadingFailed()
    fun onPlayerInfoLoaded(response: String)
}