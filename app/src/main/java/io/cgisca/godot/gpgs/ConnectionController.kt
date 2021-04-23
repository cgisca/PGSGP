package io.cgisca.godot.gpgs

import android.app.Activity
import android.util.Log
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.cgisca.godot.gpgs.signin.UserProfile

class ConnectionController(
    private val activity: Activity,
    private var signInOptions: GoogleSignInOptions
) {

    fun isConnected(): Pair<Boolean, UserProfile> {
        val userProfile = UserProfile(null, null, null, null)
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
            ?: return Pair(false, userProfile)

        if (!googleSignInAccount.isExpired) {
            Log.i("godot","Sign in data is valid")
            userProfile.let {
                it.displayName = googleSignInAccount.displayName
                it.email = googleSignInAccount.email
                it.token = googleSignInAccount.idToken
                it.id = googleSignInAccount.id
            }
            return Pair(GoogleSignIn.hasPermissions(googleSignInAccount, *signInOptions.scopeArray), userProfile)
        }else{
            Log.i("godot","Sign in data has expired")
            return Pair(false, userProfile)
        }
    }
}