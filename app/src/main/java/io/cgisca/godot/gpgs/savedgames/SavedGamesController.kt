package io.cgisca.godot.gpgs.savedgames

import android.app.Activity
import android.util.Log
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.SnapshotsClient.DataOrConflict
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import com.google.android.gms.tasks.Continuation
import io.cgisca.godot.gpgs.ConnectionController
import java.io.IOException

class SavedGamesController(
    private val activity: Activity,
    private val savedGamesListener: SavedGamesListener,
    private val connectionController: ConnectionController
) {

    companion object {
        const val RC_SAVED_GAMES = 9009
    }

    fun showSavedGamesUI(
        title: String,
        allowAddBtn: Boolean,
        allowDeleteBtn: Boolean,
        maxNumberOfSavedGamesToShow: Int
    ) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val intentTask = snapshotsClient.getSelectSnapshotIntent(
                title,
                allowAddBtn,
                allowDeleteBtn,
                maxNumberOfSavedGamesToShow
            )
            intentTask.addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_SAVED_GAMES) }
        }
    }

    private fun writeSnapshot(
        snapshot: Snapshot,
        data: ByteArray?,
        desc: String
    ) {
        snapshot.snapshotContents.writeBytes(data)
        val metadataChange = SnapshotMetadataChange.Builder()
            .setDescription(desc)
            .build()
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val commitTask = snapshotsClient.commitAndClose(snapshot, metadataChange)
            commitTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    savedGamesListener.onSavedGameSuccess()
                } else {
                    savedGamesListener.onSavedGameFailed()
                }
            }
        } else {
            savedGamesListener.onSavedGameFailed()
        }
    }

    fun saveSnapshot(
        gameName: String,
        dataToSave: String,
        description: String
    ) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
            snapshotsClient.open(gameName, true, conflictResolutionPolicy)
                .addOnFailureListener {
                    savedGamesListener.onSavedGameFailed()
                }
                .continueWith<Pair<Snapshot, ByteArray>>(
                    Continuation<DataOrConflict<Snapshot>, Pair<Snapshot, ByteArray>> { task ->
                        val snapshot = task.result
                        snapshot?.data?.let {
                            return@Continuation Pair(it, toByteArray(dataToSave))
                        }
                        null
                    })
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val snapshot = task.result!!.first
                        val data = task.result!!.second
                        writeSnapshot(snapshot, data, description)
                    } else {
                        savedGamesListener.onSavedGameFailed()
                    }
                }
        } else {
            savedGamesListener.onSavedGameFailed()
        }
    }

    fun loadSnapshot(gameName: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
            snapshotsClient.open(gameName, true, conflictResolutionPolicy)
                .addOnFailureListener {
                    savedGamesListener.onSavedGameLoadFailed()
                }
                .continueWith<ByteArray>(Continuation { task ->
                    val snapshot = task.result
                    try {
                        snapshot?.data?.let {
                            return@Continuation it.snapshotContents.readFully()
                        }
                        return@Continuation null
                    } catch (e: IOException) {
                        Log.e("SavedGamesController", "Error while reading Snapshot.", e)
                    }
                    null
                })
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let {
                            val data = toStringData(it)
                            savedGamesListener.onSavedGameLoadSuccess(data)
                        }
                    } else {
                        savedGamesListener.onSavedGameLoadFailed()
                    }
                }
        } else {
            savedGamesListener.onSavedGameLoadFailed()
        }
    }

    fun createNewSnapshot(currentSaveName: String) {
        savedGamesListener.onSavedGameCreateSnapshot(currentSaveName)
    }

    private fun toByteArray(data: String): ByteArray {
        return data.toByteArray()
    }

    private fun toStringData(bytes: ByteArray): String {
        return String(bytes)
    }
}