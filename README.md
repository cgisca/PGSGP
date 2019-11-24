# Android Google Play Games Services Plugin for Godot 
This is an Android Play Games Services plugin for Godot Game Engine 3.2 and higher. 

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Godot](https://img.shields.io/badge/Godot%20Engine-3.2-blue.svg)](https://github.com/godotengine/godot/)
[![PGS](https://img.shields.io/badge/Play%20Games%20Services-19.0.0-green.svg)](https://developers.google.com/games/services/android/quickstart)
[![MIT license](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://lbesson.mit-license.org/)


### Supported features:
- Sign-in/Sign out
- Achievements
- Leaderboards

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


### How to use
First step is plugin initialization
```GdScript
var play_games_services
# Check if plugin was added to the project
if Engine.has_singleton("PlayGameServices"):
  play_games_services = Engine.get_singleton("PlayGameServices")
	
  # Initialize plugin by calling init method and passing to it get_instance_id()
  play_games_services.init(get_instance_id())
```
After what plugin was initialized you can use supported features
##### Sign-in / Sign out
```GdScript
play_games_services.sign_in()

play_games_services.sign_out()
```
```GdScript
#Godot callbacks received
func _on_sign_in_success() -> void:
	pass
  
func _on_sign_in_failed(error_code: int) -> void:
	pass

func _on_sign_out_success() -> void:
	pass
  
func _on_sign_out_failed() -> void:
	pass
```
##### Achievements
```GdScript
play_games_services.unlock_achievement("ACHIEVEMENT_ID")

play_games_services.reveal_achievement("ACHIEVEMENT_ID")

var step = 1
play_games_services.increment_achievement("ACHIEVEMENT_ID", step)

play_games_services.show_achievements()
```
```GdScript
#Godot callbacks received
func _on_achievement_unlocked(achievement: String) -> void:
	pass

func _on_achievement_unlocking_failed(achievement: String) -> void:
	pass

func _on_achievement_revealed(achievement: String) -> void:
	pass

func _on_achievement_revealing_failed(achievement: String) -> void:
	pass

func _on_achievement_incremented(achievement: String) -> void:
	pass

func _on_achievement_incrementing_failed(achievement: String) -> void:
	pass
```
##### Leaderboards
```GdScript
var score = 1234
play_games_services.submit_leaderboard_score("LEADERBOARD_ID", score)

play_games_services.show_leaderboard("LEADERBOARD_ID")
```
```GdScript
#Godot callbacks received
func _on_leaderboard_score_submitted(leaderboard_id: String) -> void:
	pass

func _on_leaderboard_score_submitting_failed(leaderboard_id: String) -> void:
	pass
```
##### Player connection
```GdScript
play_games_services.is_player_connected()
```
```GdScript
#Godot callbacks received
func _on_player_is_already_connected(is_connected: bool) -> void:
	pass
```
