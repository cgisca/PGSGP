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
        const val NOT_CONNECTED = 1
        const val NOT_SIGNED_IN = 2
        const val WRITE_TASK_FAILED = 3
        const val OPEN_SNAPSHOT_FAILURE = 4
        const val ON_TASK_COMPLETE_FAILURE = 5
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
        if (!connectionController.isConnected().first) {
            savedGamesListener.onSavedGameFailed(NOT_CONNECTED)
        }
        else if (googleSignInAccount == null) {
            savedGamesListener.onSavedGameFailed(NOT_SIGNED_IN)
        }
        else {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val commitTask = snapshotsClient.commitAndClose(snapshot, metadataChange)
            commitTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    savedGamesListener.onSavedGameSuccess()
                } else {
                    savedGamesListener.onSavedGameFailed(WRITE_TASK_FAILED)
                }
            }
        }
    }

    fun saveSnapshot(
        gameName: String,
        dataToSave: String,
        description: String
    ) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (! connectionController.isConnected().first) {
            savedGamesListener.onSavedGameFailed(NOT_CONNECTED)
        }
        else if (googleSignInAccount == null) {
            savedGamesListener.onSavedGameFailed(NOT_SIGNED_IN)
        }
        else {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
            val createIfNotFound = true
            snapshotsClient.open(gameName, createIfNotFound, conflictResolutionPolicy)
                .addOnFailureListener {
                    savedGamesListener.onSavedGameFailed(OPEN_SNAPSHOT_FAILURE)
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
                        savedGamesListener.onSavedGameFailed(ON_TASK_COMPLETE_FAILURE)
                    }
                }
        }
    }

    fun loadSnapshot(gameName: String) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (! connectionController.isConnected().first) {
            savedGamesListener.onSavedGameLoadFailed(NOT_CONNECTED)
        }
        else if (googleSignInAccount == null) {
            savedGamesListener.onSavedGameLoadFailed(NOT_SIGNED_IN)
        }
        else {
            val snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount)
            val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
            val createIfNotFound = true
            snapshotsClient.open(gameName, createIfNotFound, conflictResolutionPolicy)
                .addOnFailureListener {
                    savedGamesListener.onSavedGameLoadFailed(OPEN_SNAPSHOT_FAILURE)
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
                        savedGamesListener.onSavedGameLoadFailed(ON_TASK_COMPLETE_FAILURE)
                    }
                }
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