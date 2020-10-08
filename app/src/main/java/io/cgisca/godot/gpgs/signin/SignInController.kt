package io.cgisca.godot.gpgs.signin

import android.app.Activity
import android.util.Log
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
        val userProfile = UserProfile(null, null, null, null)
        val connection: Pair<Boolean, UserProfile> = connectionController.isConnected()
        if (connection.first) {
            Log.i("godot","Using cached signin data")
            signInListener.onSignedInSuccessfully(connection.second)
            enablePopUps()
        } else {
            Log.i("godot","Using new signin data")
            googleSignInClient
                .silentSignIn()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val googleSignInAccount = task.result
                        if (googleSignInAccount != null) {
                            userProfile.let {
                                it.displayName = googleSignInAccount.displayName
                                it.email = googleSignInAccount.email
                                it.token = googleSignInAccount.idToken
                                it.id = googleSignInAccount.id
                            }
                        }

                        signInListener.onSignedInSuccessfully(userProfile)
                        enablePopUps()
                    } else {
                        val intent = googleSignInClient.signInIntent
                        activity.startActivityForResult(intent, RC_SIGN_IN)
                    }
                }
        }
    }

    fun onSignInActivityResult(googleSignInResult: GoogleSignInResult?) {
        val userProfile = UserProfile(null, null, null, null)
        if (googleSignInResult != null && googleSignInResult.isSuccess) {
            val googleSignInAccount = googleSignInResult.signInAccount
            if (googleSignInAccount != null) {
                userProfile.let {
                    it.displayName = googleSignInAccount.displayName
                    it.email = googleSignInAccount.email
                    it.token = googleSignInAccount.idToken
                    it.id = googleSignInAccount.id
                }
            }
            enablePopUps()
            signInListener.onSignedInSuccessfully(userProfile)
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

    fun isSignedIn(): Boolean {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        return connectionController.isConnected().first && googleSignInAccount != null
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