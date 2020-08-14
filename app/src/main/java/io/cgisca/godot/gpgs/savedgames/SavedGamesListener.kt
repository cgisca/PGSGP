package io.cgisca.godot.gpgs.savedgames

interface SavedGamesListener {
    fun onSavedGameSuccess()
    fun onSavedGameFailed(statusCode: Int)
    fun onSavedGameLoadFailed(statusCode: Int)
    fun onSavedGameLoadSuccess(data: String)
    fun onSavedGameCreateSnapshot(currentSaveName: String)
}