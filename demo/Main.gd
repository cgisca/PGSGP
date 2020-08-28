extends Control

#REPLACE BELOW IDS WITH YOUR OWN
const UNLOCK_ACHIEVEMENT = "CgkIqt-jg_MWEAIQAQ"
const REVEAL_ACHIEVEMENT = "CgkIqt-jg_MWEAIQAQ"
const INCREMENT_ACHIEVEMENT = "CgkIqt-jg_MWEAIQAQ"
const SET_ACHIEVEMENT_STEPS = "CgkIqt-jg_MWEAIQAQ"
const LEADERBOARD_ID = "CgkIqt-jg_MWEAIQAQ"

var play_games_services

func _ready():
#	Plugin init
	if Engine.has_singleton("GodotPlayGamesServices"):
		play_games_services = Engine.get_singleton("GodotPlayGamesServices")

		play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
		play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
		play_games_services.connect("_on_sign_out_success", self, "_on_sign_out_success")
		play_games_services.connect("_on_sign_out_failed", self, "_on_sign_out_failed")
		play_games_services.connect("_on_achievement_unlocked", self, "_on_achievement_unlocked")
		play_games_services.connect("_on_achievement_unlocking_failed", self, "_on_achievement_unlocking_failed")
		play_games_services.connect("_on_achievement_revealed", self, "_on_achievement_revealed")
		play_games_services.connect("_on_achievement_revealing_failed", self, "_on_achievement_revealing_failed")
		play_games_services.connect("_on_achievement_incremented", self, "_on_achievement_incremented")
		play_games_services.connect("_on_achievement_incrementing_failed", self, "_on_achievement_incrementing_failed")
		play_games_services.connect("_on_achievement_steps_set", self, "_on_achievement_steps_set")
		play_games_services.connect("_on_achievement_steps_setting_failed", self, "_on_achievement_steps_setting_failed")
		play_games_services.connect("_on_leaderboard_score_submitted", self, "_on_leaderboard_score_submitted")
		play_games_services.connect("_on_leaderboard_score_submitting_failed", self, "_on_leaderboard_score_submitting_failed")
		play_games_services.connect("_on_game_saved_success", self, "_on_game_saved_success")
		play_games_services.connect("_on_game_saved_fail", self, "_on_game_saved_fail")
		play_games_services.connect("_on_game_load_success", self, "_on_game_load_success")
		play_games_services.connect("_on_game_load_fail", self, "_on_game_load_fail")
		play_games_services.connect("_on_create_new_snapshot", self, "_on_create_new_snapshot")
		play_games_services.connect("_on_player_info_loaded", self, "_on_player_info_loaded")
		play_games_services.connect("_on_player_info_loading_failed", self, "_on_player_info_loading_failed")
	
		play_games_services.init(true)
#		play_games_services.initWithSavedGames(true, "SAVE_GAME_NAME") # Use this init if you want saved games feature to be enabled


# Sign-in/sign-out methods
func sign_in() -> void:
	if play_games_services:
		play_games_services.signIn()


func sign_out() -> void:
	if play_games_services:
		play_games_services.signOut()


func check_if_signed_in() -> void:
	if play_games_services:
		var is_signed_in: bool = play_games_services.isSignedIn()
		print("Signed in: %s"%is_signed_in)	


# Achievements methods
func unlock_achievement() -> void:
	if play_games_services:
		play_games_services.unlockAchievement(UNLOCK_ACHIEVEMENT)


func reveal_achievement() -> void:
	if play_games_services:
		play_games_services.revealachievement(REVEAL_ACHIEVEMENT) 


func increment_achievement() -> void:
	if play_games_services:
		var step = 2
		play_games_services.incrementAchievement(INCREMENT_ACHIEVEMENT, step) 


func set_achievement_steps() -> void:
	if play_games_services:
		var steps = 5
		play_games_services.setAchievementsSteps(SET_ACHIEVEMENT_STEPS, steps)


func show_achievements() -> void:
	if play_games_services:
		play_games_services.showAchievements()


# Leaderboards methods
func show_leaderboard() -> void:
	if play_games_services:
		play_games_services.showLeaderBoard(LEADERBOARD_ID) 


func show_all_leaderboards() -> void:
	if play_games_services:
		play_games_services.showAllLeaderBoards()


func submit_leaderboard_score() -> void:
	if play_games_services:
		var score = 1234
		play_games_services.submitLeaderBoardScore(LEADERBOARD_ID, score) 


#Save game methods
func save_game() -> void:
	var data_to_save: Dictionary = {
		"name": "John", 
		"age": 22,
		"height": 1.82,
		"is_gamer": true
		}
		
	if play_games_services:
		play_games_services.saveSnapshot("SNAPSHOT_NAME", to_json(data_to_save), "DESCRIPTION")


func show_saved_games() -> void:
	if play_games_services:
		play_games_services.showSavedGames("SNAPSHOT_NAME", true, true, 5)


func load_player_info() -> void:
	if play_games_services:
		play_games_services.loadPlayerInfo()


# CALLBACKS
# Sign-in / sign-out callbacks
func _on_sign_in_success(account_id: String) -> void:
	print("Sign in success %s"%account_id)

func _on_sign_in_failed(error_code: int) -> void:
	print("Sign in failed %s"%error_code)

func _on_sign_out_success() -> void:
	print("Sign out success")

func _on_sign_out_failed() -> void:
	print("Sign out failed")

# Achievements callbacks
func _on_achievement_unlocked(achievement: String) -> void:
	print("Achievement %s unlocked"%achievement)

func _on_achievement_unlocking_failed(achievement: String) -> void:
	print("Achievement %s unlocking failed "%achievement)

func _on_achievement_revealed(achievement: String) -> void:
	print("Achievement %s revealed"%achievement)

func _on_achievement_revealing_failed(achievement: String) -> void:
	print("Achievement %s revealing failed"%achievement)

func _on_achievement_incremented(achievement: String) -> void:
	print("Achievement %s incremented"%achievement)

func _on_achievement_incrementing_failed(achievement: String) -> void:
	print("Achievement %s incrementing failed"%achievement)

func _on_achievement_steps_set(achievement: String) -> void:
	print("Achievement %s steps set"%achievement)

func _on_achievement_steps_setting_failed(achievement: String) -> void:
	print("Achievement %s steps setting failed"%achievement)


# Leaderboards callbacks
func _on_leaderboard_score_submitted(leaderboard_id: String) -> void:
	print("LeaderBoard %s, score submitted"%leaderboard_id)

func _on_leaderboard_score_submitting_failed(leaderboard_id: String) -> void:
	print("LeaderBoard %s, score submitting failed"%leaderboard_id)


# Saved game Callbacks:
func _on_game_saved_success():
	print("Game saved success")

func _on_game_saved_fail():
	print("Game saved fail")

func _on_game_load_success(data):
	var game_data: Dictionary = parse_json(data)
	print(data)
	print("=====")
	print(parse_json(data))

func _on_game_load_fail():
	print("Game load fail")

func _on_create_new_snapshot(name:String):
	print("Create new snapshot %s"%name)


# Player Info Callbacks
func _on_player_info_loaded(player_info: String):
	var player_info_dictionary: Dictionary = parse_json(player_info)
	print(player_info_dictionary)

func _on_player_info_loading_failed():
	print("Player info loading failed")


###### Buttons callbacks #############
func _on_SignInButton_pressed():
	sign_in()


func _on_SignOutButton_pressed():
	sign_out()


func _on_CheckIfSignedInButton_pressed():
	check_if_signed_in()
	

func _on_SaveGameButton_pressed():
	save_game()


func _on_ShowSavedGamesButton_pressed():
	show_saved_games()


func _on_Button_pressed():
	if play_games_services:
		play_games_services.loadSnapshot("SNAPSHOT_NAME")


func _on_ShowLeaderBoardsButton_pressed():
	show_all_leaderboards()


func _on_AchievementUnlockButton_pressed():
	unlock_achievement()
