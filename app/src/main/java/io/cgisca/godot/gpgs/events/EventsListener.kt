package io.cgisca.godot.gpgs.events

interface EventsListener {
    fun onEventSubmitted(eventId: String)
    fun onEventSubmittingFailed(eventId: String)
    fun onEventsLoaded(eventsJson: String)
    fun onEventsEmpty()
    fun onEventsLoadingFailed()
}