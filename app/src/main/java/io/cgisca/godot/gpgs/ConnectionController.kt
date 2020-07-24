package io.cgisca.godot.gpgs

import android.app.Activity
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ConnectionController(
    private val activity: Activity,
    private var signInOptions: GoogleSignInOptions
) {

    fun isConnected(): Pair<Boolean, String> {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        var accId = ""

        googleSignInAccount?.id?.let {
            accId = it
        }
        return Pair(GoogleSignIn.hasPermissions(googleSignInAccount, *signInOptions.scopeArray), accId)
    }
}