package main

import "en/internal/engine"

func main() {

	manager := engine.NewManager()
	store := engine.NewBetStore()

	go engine.StartGameLoop(manager, store)

	select {}
}
