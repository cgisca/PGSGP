package io.cgisca.godot.gpgs.events

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.event.Event
import io.cgisca.godot.gpgs.ConnectionController
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EventsController(
    private val activity: Activity,
    private val eventsListener: EventsListener,
    private val connectionController: ConnectionController
) {

    fun submitEvent(eventId: String, incrementBy: Int) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount).increment(eventId, incrementBy)
            eventsListener.onEventSubmitted(eventId)
        } else {
            eventsListener.onEventSubmittingFailed(eventId)
        }
    }

    fun loadEvents() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount)
                .load(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val events = task.result!!.get()
                        if (events != null) {
                            val jsonArray = JSONArray()
                            for (event in events) {
                                jsonArray.put(eventInfoArray(event))
                            }
                            eventsListener.onEventsLoaded(jsonArray.toString())
                        } else {
                            eventsListener.onEventsEmpty()
                        }
                    } else {
                        eventsListener.onEventsLoadingFailed()
                    }
                }
        } else {
            eventsListener.onEventsLoadingFailed()
        }
    }

    fun loadEventById(eventIds: Array<String>) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (connectionController.isConnected().first && googleSignInAccount != null) {
            Games.getEventsClient(activity, googleSignInAccount)
                .loadByIds(true, *eventIds)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val events = task.result!!.get()
                        if (events != null) {
                            val jsonArray = JSONArray()
                            for (event in events) {
                                jsonArray.put(eventInfoArray(event))
                            }
                            eventsListener.onEventsLoaded(jsonArray.toString())
                        } else {
                            eventsListener.onEventsEmpty()
                        }
                    } else {
                        eventsListener.onEventsLoadingFailed()
                    }
                }
        } else {
            eventsListener.onEventsLoadingFailed()
        }
    }

    private fun eventInfoArray(event: Event): JSONObject? {
        val json = JSONObject()
        try {
            json.put("id", event.eventId)
            json.put("name", event.name)
            json.put("value", event.value)
            json.put("description", event.description)
            json.put("imgUrl", event.iconImageUrl)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }
}