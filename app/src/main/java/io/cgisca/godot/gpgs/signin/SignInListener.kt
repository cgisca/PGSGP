package io.cgisca.godot.gpgs.signin

interface SignInListener {
    fun onSignedInSuccessfully(userProfile: UserProfile)
    fun onSignInFailed(statusCode: Int)
    fun onSignOutSuccess()
    fun onSignOutFailed()
}