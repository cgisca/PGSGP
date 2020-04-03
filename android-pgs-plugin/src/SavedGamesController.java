package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

public class SavedGamesController {

    static final int RC_SAVED_GAMES = 9009;

    private Activity activity;
    private GodotCallbacksUtils godotCallbacksUtils;
    private ConnectionController connectionController;

    SavedGamesController(Activity activity, GodotCallbacksUtils godotCallbacksUtils, ConnectionController connectionController) {
        this.activity = activity;
        this.godotCallbacksUtils = godotCallbacksUtils;
        this.connectionController = connectionController;
    }

    protected void showSavedGamesUI(String title, boolean allowAddBtn, boolean allowDeleteBtn, int maxNumberOfSavedGamesToShow) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            SnapshotsClient snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
            Task<Intent> intentTask = snapshotsClient.getSelectSnapshotIntent(title, allowAddBtn, allowDeleteBtn, maxNumberOfSavedGamesToShow);

            intentTask.addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    activity.startActivityForResult(intent, RC_SAVED_GAMES);
                }
            });
        }
    }

    private void writeSnapshot(Snapshot snapshot, byte[] data, String desc) {
        snapshot.getSnapshotContents().writeBytes(data);
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription(desc)
                .build();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            SnapshotsClient snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
            Task<SnapshotMetadata> task = snapshotsClient.commitAndClose(snapshot, metadataChange);
            task.addOnCompleteListener(new OnCompleteListener<SnapshotMetadata>() {
                @Override
                public void onComplete(@NonNull Task<SnapshotMetadata> task) {
                    if (task.isSuccessful()) {
                        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_SUCCESS, new Object[]{});
                    } else {
                        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_FAILED, new Object[]{});
                    }
                }
            });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_FAILED, new Object[]{});
        }
    }


    protected void saveSnapshot(String gameName, final String dataToSave, final String description) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            SnapshotsClient snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
            int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;
            
            snapshotsClient.open(gameName, true, conflictResolutionPolicy)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_FAILED, new Object[]{});
                        }
                    }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, Pair<Snapshot, byte[]>>() {
                @Override
                public Pair<Snapshot, byte[]> then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                    SnapshotsClient.DataOrConflict<Snapshot> snapshot = task.getResult();
                    if (snapshot != null && snapshot.getData() != null) {
                        return new Pair<>(snapshot.getData(), toByteArray(dataToSave));
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Pair<Snapshot, byte[]>>() {
                @Override
                public void onComplete(@NonNull Task<Pair<Snapshot, byte[]>> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Snapshot snapshot = task.getResult().first;
                        byte[] data = task.getResult().second;
                        writeSnapshot(snapshot, data, description);
                    } else {
                        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_FAILED, new Object[]{});
                    }
                }
            });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_FAILED, new Object[]{});
        }
    }

    void loadSnapshot(String gameName) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            SnapshotsClient snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
            int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

            snapshotsClient.open(gameName, true, conflictResolutionPolicy)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_LOAD_FAIL, new Object[]{});
                        }
                    }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, byte[]>() {
                @Override
                public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                    SnapshotsClient.DataOrConflict<Snapshot> snapshot = task.getResult();
                    try {
                        if (snapshot != null && snapshot.getData() != null) {
                            return snapshot.getData().getSnapshotContents().readFully();
                        }
                        return null;
                    } catch (IOException e) {
                        Log.e("SavedGamesController", "Error while reading Snapshot.", e);
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful()) {
                        String data = toStringData(task.getResult());
                        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_LOAD_SUCCESS, new Object[]{data});
                    } else {
                        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_LOAD_FAIL, new Object[]{});
                    }
                }
            });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_LOAD_FAIL, new Object[]{});
        }
    }

    void createNewSnapshot(String currentSaveName) {
        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SAVED_GAME_CREATE_SNAPSHOT, new Object[]{currentSaveName});
    }

    private byte[] toByteArray(String data) {
        return data.getBytes();
    }

    private String toStringData(byte[] bytes) {
        return new String(bytes);
    }
}

