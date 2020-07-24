package io.cgisca.godot.gpgs.signin

import android.app.Activity
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.games.Games
import io.cgisca.godot.gpgs.ConnectionController

class SignInController(
    private var activity: Activity,
    private var signInListener: SignInListener,
    private var connectionController: ConnectionController
) {

    companion object {
        const val RC_SIGN_IN = 77
    }

    private var showPlayPopups = true

    fun setShowPopups(enablePopUps: Boolean) {
        showPlayPopups = enablePopUps
    }

    fun signIn(googleSignInClient: GoogleSignInClient) {
        val connection: Pair<Boolean, String> = connectionController.isConnected()
        if (connection.first) {
            signInListener.onSignedInSuccessfully(connection.second)
            enablePopUps()
        } else {
            googleSignInClient
                .silentSignIn()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val googleSignInAccount = task.result
                        var accId = ""
                        googleSignInAccount?.id?.let {
                            accId = it
                        }

                        signInListener.onSignedInSuccessfully(accId)
                        enablePopUps()
                    } else {
                        val intent = googleSignInClient.signInIntent
                        activity.startActivityForResult(intent, RC_SIGN_IN)
                    }
                }
        }
    }

    fun onSignInActivityResult(googleSignInResult: GoogleSignInResult?) {
        if (googleSignInResult != null && googleSignInResult.isSuccess) {
            val googleSignInAccount = googleSignInResult.signInAccount
            var accId = ""
            googleSignInAccount?.id?.let {
                accId = it
            }
            enablePopUps()
            signInListener.onSignedInSuccessfully(accId)
        } else {
            var statusCode = Int.MIN_VALUE
            googleSignInResult?.status?.let {
                statusCode = it.statusCode
            }
            signInListener.onSignInFailed(statusCode)
        }
    }

    fun signOut(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                signInListener.onSignOutSuccess()
            } else {
                signInListener.onSignOutFailed()
            }
        }
    }

    private fun enablePopUps() {
        if (showPlayPopups) {
            val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity)
            if (lastSignedInAccount != null) {
                Games.getGamesClient(activity, lastSignedInAccount)
                    .setViewForPopups(activity.findViewById(android.R.id.content))
            }
        }
    }
}