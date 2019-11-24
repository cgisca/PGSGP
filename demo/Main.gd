extends Node

var play_games_services

func _ready():
#	Plugin init
	if(Engine.has_singleton("GoogleGameServices")):
		play_games_services = Engine.get_singleton("GoogleGameServices")
		
		play_games_services.init(get_instance_id())


func sign_in() -> void:
	if play_games_services:
		play_games_services.sign_in()


func sign_out() -> void:
	if play_games_services:
		play_games_services.sign_out()


# Callbacks
func _on_sign_in_success() -> void:
	pass


func _on_sign_in_failed(error_code) -> void:
	pass


func _on_sign_out_success() -> void:
	pass


func _on_sign_out_failed() -> void:
	pass
