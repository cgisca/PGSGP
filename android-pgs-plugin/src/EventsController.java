package org.godotengine.godot;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventsController {

    private Activity activity;
    private ConnectionController connectionController;
    private GodotCallbacksUtils godotCallbacksUtils;

    public EventsController(Activity activity, ConnectionController connectionController, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.connectionController = connectionController;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public void submitEvent(String eventId, int incrementBy) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount).increment(eventId, incrementBy);
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENT_SUBMITTED, new Object[]{eventId});
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENT_SUBMITTED_FAILED, new Object[]{eventId});
        }
    }

    public void loadEvents() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount)
                    .load(true)
                    .addOnCompleteListener(new OnCompleteListener<AnnotatedData<EventBuffer>>() {
                        @Override
                        public void onComplete(@NonNull Task<AnnotatedData<EventBuffer>> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                EventBuffer events = task.getResult().get();
                                if (events != null) {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Event event : events) {
                                        jsonArray.put(eventInfoArray(event));
                                    }
                                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED, new Object[]{jsonArray.toString()});
                                } else {
                                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_EMPTY, new Object[]{});
                                }
                            } else {
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED_FAILED, new Object[]{});
                            }
                        }
                    });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED_FAILED, new Object[]{});
        }
    }

    public void loadEventById(String[] eventIds) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount)
                    .loadByIds(true, eventIds)
                    .addOnCompleteListener(new OnCompleteListener<AnnotatedData<EventBuffer>>() {
                        @Override
                        public void onComplete(@NonNull Task<AnnotatedData<EventBuffer>> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                EventBuffer events = task.getResult().get();
                                if (events != null) {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Event event : events) {
                                        jsonArray.put(eventInfoArray(event));
                                    }
                                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED, new Object[]{jsonArray.toString()});
                                } else {
                                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_EMPTY, new Object[]{});
                                }
                            } else {
                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED_FAILED, new Object[]{});
                            }
                        }
                    });
        } else {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.EVENTS_LOADED_FAILED, new Object[]{});
        }
    }

    private JSONObject eventInfoArray(Event event) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", event.getEventId());
            json.put("name", event.getName());
            json.put("value", event.getValue());
            json.put("description", event.getDescription());
            json.put("imgUrl", event.getIconImageUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
