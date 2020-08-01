# Google Play Games Services Plugin for Godot 
This is an Android Play Games Services plugin for Godot Game Engine 3.2.2+. 

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Godot](https://img.shields.io/badge/Godot%20Engine-3.2.2-blue.svg)](https://github.com/godotengine/godot/)
[![PGS](https://img.shields.io/badge/Play%20Games%20Services-19.0.0-green.svg)](https://developers.google.com/games/services/android/quickstart)
[![MIT license](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://lbesson.mit-license.org/)


If you want to use the old plugin version visit [Old README file](https://github.com/cgisca/PGSGP/blob/master/README_OLD.md).


### Supported features:
- Sign-in/Sign out
- Achievements
- Leaderboards
- Events
- Player Stats
- Saved Games

## Getting started
Before using this plugin please follow instructions on [Setting Up Google Play Games Services](https://developers.google.com/games/services/console/enabling) official guide.
### Set up
- Download `GodotPlayGamesServices.release.aar` and `GodotPlayGamesServices.gdap` from [releases](https://github.com/cgisca/PGSGP/releases) page.
- Move the plugin configuration file (`GodotPlayGamesServices.gdap`) and the binary (`GodotPlayGamesServices.release.aar`) downloaded from the previous step to the Godot project's res://android/plugins directory.
- Enable plugin by accessing `Project` -> `Export`, Plugins section. Follow the [image](https://docs.godotengine.org/en/stable/_images/android_export_preset_plugins_section.png).
- Go to res://android/build directory. Add below lines to `AndroidManifest.xml`:
```xml	
    <meta-data android:name="com.google.android.gms.games.APP_ID"
   	    android:value="@string/app_id" />
   
   	<meta-data android:name="com.google.android.gms.version"
   	   android:value="@integer/google_play_services_version"/>
```
- In the same res://android/build directory,(if it is not already created) create  `res` -> `values` -> `Strings.xml`. Add below lines to `Strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    	<string name="app_id">ADD_YOUR_APP_ID</string>
</resources>
``` 
Replace ADD_YOUR_APP_ID with the app id that was generated after following instructions on [Setting Up Google Play Games Services](https://developers.google.com/games/services/console/enabling)

Check demo project. In order demo project to work, replace <string name="app_id">ADD_YOUR_APP_ID</string> with your own app id, and in Main.gd add your ids for achievements and leaderboards.

### How to use
First step is plugin initialization
```gdscript
var play_games_services
# Check if plugin was added to the project
if Engine.has_singleton("GodotPlayGamesServices"):
  play_games_services = Engine.get_singleton("GodotPlayGamesServices")
	
  # Initialize plugin by calling init method and passing to it a boolean to enable/disable displaying game pop-ups
  
  var show_popups := true 
  play_games_services.init(show_popups)
  # For enabling saved games functionality use below initialization instead
  # play_games_services.initWithSavedGames(show_popups, "SavedGamesName")
  
  # Connect callbacks (Use only those that you need)
  play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success") # account_id: String
  play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed") # error_code: int
  play_games_services.connect("_on_sign_out_success", self, "_on_sign_out_success") # no params
  play_games_services.connect("_on_sign_out_failed", self, "_on_sign_out_failed") # no params
  play_games_services.connect("_on_achievement_unlocked", self, "_on_achievement_unlocked") # achievement: String
  play_games_services.connect("_on_achievement_unlocking_failed", self, "_on_achievement_unlocking_failed") # achievement: String
  play_games_services.connect("_on_achievement_revealed", self, "_on_achievement_revealed") # achievement: String
  play_games_services.connect("_on_achievement_revealing_failed", self, "_on_achievement_revealing_failed") # achievement: String
  play_games_services.connect("_on_achievement_incremented", self, "_on_achievement_incremented") # achievement: String
  play_games_services.connect("_on_achievement_incrementing_failed", self, "_on_achievement_incrementing_failed") # achievement: String
  play_games_services.connect("_on_leaderboard_score_submitted", self, "_on_leaderboard_score_submitted") # leaderboard_id: String
  play_games_services.connect("_on_leaderboard_score_submitting_failed", self, "_on_leaderboard_score_submitting_failed") # leaderboard_id: String
  play_games_services.connect("_on_game_saved_success", self, "_on_game_saved_success") # no params
  play_games_services.connect("_on_game_saved_fail", self, "_on_game_saved_fail") # no params
  play_games_services.connect("_on_game_load_success", self, "_on_game_load_success") # data: String
  play_games_services.connect("_on_game_load_fail", self, "_on_game_load_fail") # no params
  play_games_services.connect("_on_create_new_snapshot", self, "_on_create_new_snapshot") # name: String
	
```
After what plugin was initialized you can use supported features
#### Sign-in / Sign out
##### Sign-in

```gdscript
play_games_services.signIn()

# Callbacks:
func _on_sign_in_success(account_id: String) -> void:
	pass
  
func _on_sign_in_failed(error_code: int) -> void:
	pass

```
##### Sign out
```gdscript
play_games_services.signOut()

# Callbacks:
func _on_sign_out_success():
	pass
  
func _on_sign_out_failed():
	pass
```
#### Achievements
##### Unlock Achievement
```gdscript
play_games_services.unlockAchievement("ACHIEVEMENT_ID")

# Callbacks:
func _on_achievement_unlocked(achievement: String):
	pass

func _on_achievement_unlocking_failed(achievement: String):
	pass
```
##### Increment Achievement
```gdscript
var step = 1
play_games_services.incrementAchievement("ACHIEVEMENT_ID", step)

# Callbacks:
func _on_achievement_incremented(achievement: String):
	pass

func _on_achievement_incrementing_failed(achievement: String):
	pass
```
##### Set Achievement Steps
```gdscript
var steps = 3
play_games_services.setAchievementSteps("ACHIEVEMENT_ID", steps)

# Callbacks:
func _on_achievement_steps_set(achievement: String):
	pass

func _on_achievement_steps_setting_failed(achievement: String):
	pass
```
##### Reveal Achievement
```gdscript
play_games_services.revealAchievement("ACHIEVEMENT_ID")

# Callbacks:
func _on_achievement_revealed(achievement: String):
	pass

func _on_achievement_revealing_failed(achievement: String):
	pass
```
##### Show Achievements List
```gdscript
play_games_services.showAchievements()
```
#### Leaderboards
##### Submit leaderboard score
```gdscript
var score = 1234
play_games_services.submitLeaderBoardScore("LEADERBOARD_ID", score)

# Callbacks:
func _on_leaderboard_score_submitted(leaderboard_id: String):
	pass

func _on_leaderboard_score_submitting_failed(leaderboard_id: String):
	pass
```
##### Show leaderboard
```gdscript
play_games_services.showLeaderBoard("LEADERBOARD_ID")


play_games_services.showAllLeaderBoards()
```

#### Events
##### Submit event
```gdscript
var increment_by := 2
play_games_services.submitEvent("EVENT_ID", increment_by)

# Callbacks:
func _on_event_submitted(event_id: String):
	pass
	
func _on_event_submitted_failed(event_id: String):
	pass
```
##### Load events
```gdscript
# Load all events
play_games_services.loadEvents()
# Or load events by given ids
play_games_services.loadEventsById(["EVENT_ID_1", "EVENT_ID_2", ...])

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
play_games_services.loadPlayerStats(force_refresh)

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
play_games_services.saveSnapshot("SNAPSHOT_NAME", to_json(data_to_save), "DESCRIPTION")

# Callbacks:
func _on_game_saved_success():
	pass
	
func _on_game_saved_fail():
	pass
```
##### Load game snapshot
```gdscript
play_games_services.loadSnapshot("SNAPSHOT_NAME")

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
var saved_games_screen_title := "TITLE"
play_games_services.showSavedGames(saved_games_screen_title, allow_add_button, allow_delete_button, max_saved_games_snapshots)

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

