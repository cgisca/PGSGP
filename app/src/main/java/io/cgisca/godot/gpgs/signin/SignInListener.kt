package io.cgisca.godot.gpgs.signin

interface SignInListener {
    fun onSignedInSuccessfully(accountId: String)
    fun onSignInFailed(statusCode: Int)
    fun onSignOutSuccess()
    fun onSignOutFailed()
}