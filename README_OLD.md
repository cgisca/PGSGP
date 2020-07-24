# Google Play Games Services Plugin for Godot 
This is an Android Play Games Services plugin for Godot Game Engine 3.2. 

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Godot](https://img.shields.io/badge/Godot%20Engine-3.2-blue.svg)](https://github.com/godotengine/godot/)
[![PGS](https://img.shields.io/badge/Play%20Games%20Services-19.0.0-green.svg)](https://developers.google.com/games/services/android/quickstart)
[![MIT license](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://lbesson.mit-license.org/)


### Supported features:
- Sign-in/Sign out
- Achievements
- Leaderboards
- Events
- Player Stats
- Saved Games

## Getting started
### Set up
- Set up the custom build environment for your project and enable it. See [Godot Engine docs](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html).
- Download and move `android-pgs-plugin` from the current repository into `res://android/` of your godot project.
- After downloading `android-pgs-plugin`, open it and go to `res` -> `values`. Search for `ids.xml` and open it. Replace `PUT_YOUR_APP_ID_HERE` with your Games Services Application ID for the `app_id` resource. (Don't forget to save file)
- In Godot Engine, go to `Project` -> `Project Settings`. Then on the tab `General` go to the `Android` section, and fill the `Modules` part with `org/godotengine/godot/PlayGameServices`. See [Godot Engine docs](
https://docs.godotengine.org/en/latest/tutorials/plugins/android/android_plugin.html#using-it-from-gdscript).
<br/>*Note: If you have already added one plugin to the `Modules` append the current one by separating them with comma (ex: `xx/xx/Module1,org/godotengine/godot/PlayGameServices`)
<br/>*Note 2: If you have already added other plugin that has `meta-data android:name="com.google.android.gms.version"` declared inside it's `AndroidManifest`, remove **below lines** from the `android-pgs-plugin`-> `AndroidManifest.conf` file.  (Don't forget to save file after removing below lines)

```xml
<meta-data android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />
```

### Latest version
Latest version could be found [here (1.2.0)](https://github.com/cgisca/PGSGP/releases/tag/1.2.0).

### How to use
First step is plugin initialization
```gdscript
var play_games_services
# Check if plugin was added to the project
if Engine.has_singleton("PlayGameServices"):
  play_games_services = Engine.get_singleton("PlayGameServices")
	
  # Initialize plugin by calling init method and passing to it get_instance_id() and a boolean to enable/disable displaying game pop-ups
  
  var show_popups := true # For example, your game can display a “Welcome back” or an “Achievements unlocked” pop-up. true for enabling it.
  var enable_save_games := true # If you want to enable Saved Games functionality. true for enabling it. If enabled, it will require additional Google Drive API permission from the user.
  play_games_services.init(get_instance_id(), show_popups, enable_save_games)
```
After what plugin was initialized you can use supported features
#### Sign-in / Sign out
##### Sign-in

```gdscript
play_games_services.sign_in()

# Callbacks:
func _on_sign_in_success(account_id: String) -> void:
	pass
  
func _on_sign_in_failed(error_code: int) -> void:
	pass

```
##### Sign out
```gdscript
play_games_services.sign_out()

# Callbacks:
func _on_sign_out_success():
	pass
  
func _on_sign_out_failed():
	pass
```
#### Achievements
##### Unlock Achievement
```gdscript
play_games_services.unlock_achievement("ACHIEVEMENT_ID")

# Callbacks:
func _on_achievement_unlocked(achievement: String):
	pass

func _on_achievement_unlocking_failed(achievement: String):
	pass
```
##### Increment Achievement
```gdscript
var step = 1
play_games_services.increment_achievement("ACHIEVEMENT_ID", step)

# Callbacks:
func _on_achievement_incremented(achievement: String):
	pass

func _on_achievement_incrementing_failed(achievement: String):
	pass
```
##### Reveal Achievement
```gdscript
play_games_services.reveal_achievement("ACHIEVEMENT_ID")

# Callbacks:
func _on_achievement_revealed(achievement: String):
	pass

func _on_achievement_revealing_failed(achievement: String):
	pass
```
##### Show Achievements List
```gdscript
play_games_services.show_achievements()
```
#### Leaderboards
##### Submit leaderboard score
```gdscript
var score = 1234
play_games_services.submit_leaderboard_score("LEADERBOARD_ID", score)

# Callbacks:
func _on_leaderboard_score_submitted(leaderboard_id: String):
	pass

func _on_leaderboard_score_submitting_failed(leaderboard_id: String):
	pass
```
##### Show leaderboard
```gdscript
play_games_services.show_leaderboard("LEADERBOARD_ID")


play_games_services.show_all_leaderboards()
```
#### Player connection
```gdscript
play_games_services.is_player_connected()

#Callback:
func _on_player_is_already_connected(is_connected: bool):
	pass
```
#### Events
##### Submit event
```gdscript
var increment_by := 2
play_games_services.submit_event("EVENT_ID", increment_by)

# Callbacks:
func _on_event_submitted(event_id: String):
	pass
	
func _on_event_submitted_failed(event_id: String):
	pass
```
##### Load events
```gdscript
# Load all events
play_games_services.load_events()
# Or load events by given ids
play_games_services.load_events_by_id(["EVENT_ID_1", "EVENT_ID_2", ...])

# Callbacks:
# If there is at least one event, following callback will be triggered:
func _on_events_loaded(events_array):
	# Parse received string json of events using parse_json
	var available_events = parse_json(events_array)
	# Iterate through the events_list to retrieve data for specific events
	for event in available_events:
		var event_id = event["id"] # you can get event id using 'id' key
		var event_name = event["name"] # you can get event name using 'name' key
		var event_desc = event["description"] # you can get event name using 'description' key 
		var event_img = event["imgUrl"] # you can get event name using 'imgUrl' key
		var event_value = event["value"] # you can get event name using 'value' key  
	
# Triggered if there are no events:
func _on_events_empty():
	pass

# Triggered if something went wrong:
func _on_events_loading_failed():
	pass

```
#### Player Stats
```gdscript
var force_refresh := true # If true, this call will clear any locally cached data and attempt to fetch the latest data from the server.
play_games_services.load_player_stats(force_refresh)

# Callbacks:	
func _on_player_stats_loaded(stats):
	var stats_dictionary: Dictionary = parse_json(stats)
	# Using below keys you can retrieve data about a player’s in-game activity
	stats_dictionary["avg_session_length"] # Average session length
	stats_dictionary["days_last_played"] # Days since last played
	stats_dictionary["purchases"] # Number of purchases
	stats_dictionary["sessions"] # Number of sessions
	stats_dictionary["session_percentile"] # Session percentile
	stats_dictionary["spend_percentile"] # Spend percentile

func _on_player_stats_loading_failed():
	pass
```
#### Saved Games
##### Save game snapshot
```gdscript
var data_to_save: Dictionary = {
		"name": "John", 
		"age": 22,
		"height": 1.82,
		"is_gamer": true
	}
play_games_services.save_snapshot("SNAPSHOT_NAME", to_json(data_to_save), "DESCRIPTION")

# Callbacks:
func _on_game_saved_success():
	pass
	
func _on_game_saved_fail():
	pass
```
##### Load game snapshot
```gdscript
play_games_services.load_snapshot("SNAPSHOT_NAME")

# Callbacks:
func _on_game_load_success(data):
	var game_data: Dictionary = parse_json(data)
	var name = game_data["name"]
	var age = game_data["age"]
	#...
	
	
func _on_game_load_fail():
	pass
```
##### Show saved snapshots screen
```gdscript
var allow_add_button := true
var allow_delete_button := true
var max_saved_games_snapshots := 5
play_games_services.show_saved_games("SNAPSHOT_NAME", allow_add_button, allow_delete_button, max_saved_games_snapshots)

#Godot callback	
# If user clicked on add new snapshot button on the screen with all saved snapshots, below callback will be triggered:
func _on_create_new_snapshot(name):
	var game_data_to_save: Dictionary = {
		"name": "John", 
		"age": 22,
		"height": 1.82,
		"is_gamer": true
	}
	play_games_services.save_snapshot(name, to_json(game_data_to_save), "DESCRIPTION")

```
## Troubleshooting
Check `adb logcat` for debuging.
To filter only Godot messages use next command:
`adb logcat -s godot`

