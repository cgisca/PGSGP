package io.cgisca.godot.gpgs.signin

data class UserProfile(
    var displayName: String?,
    var email: String?,
    var token: String?,
    var authCode: String?,
    var id: String?) {
}