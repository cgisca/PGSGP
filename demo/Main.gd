extends Node

var play_games_services

func _ready():
#	Plugin init
	if(Engine.has_singleton("PlayGameServices")):
		play_games_services = Engine.get_singleton("PlayGameServices")
		
		play_games_services.init(get_instance_id())


# Sign-in/sign-out methods
func sign_in() -> void:
	if play_games_services:
		play_games_services.sign_in()


func sign_out() -> void:
	if play_games_services:
		play_games_services.sign_out()


# Connection methods
func is_player_connected() -> void:
	if play_games_services:
		play_games_services.is_player_connected()


# Achievements methods
func unlock_achievement() -> void:
	if play_games_services:
		play_games_services.unlock_achievement("ACHIEVEMENT_ID")


func reveal_achievement() -> void:
	if play_games_services:
		play_games_services.reveal_achievement("ACHIEVEMENT_ID")


func increment_achievement() -> void:
	if play_games_services:
		var step = 1 
		play_games_services.increment_achievement("ACHIEVEMENT_ID", step)


func show_achievements() -> void:
	if play_games_services:
		play_games_services.show_achievements()


# Leaderboards methods
func show_leaderboard() -> void:
	if play_games_services:
		play_games_services.show_leaderboard("LEADERBOARD_ID")


func submit_leaderboard_score() -> void:
	if play_games_services:
		var score = 1234
		play_games_services.submit_leaderboard_score("LEADERBOARD_ID", score)


# Callbacks
# Sign-in / sign-out callbacks
func _on_sign_in_success() -> void:
	pass

func _on_sign_in_failed(error_code: int) -> void:
	pass

func _on_sign_out_success() -> void:
	pass

func _on_sign_out_failed() -> void:
	pass


# Connection callbacks
func _on_player_is_already_connected(is_connected: bool) -> void:
	pass


# Achievements callbacks
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


# Leaderboards callbacks
func _on_leaderboard_score_submitted(leaderboard_id: String) -> void:
	pass

func _on_leaderboard_score_submitting_failed(leaderboard_id: String) -> void:
	pass
