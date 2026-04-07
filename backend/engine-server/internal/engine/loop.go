package engine

import (
	"fmt"
	"time"
)

func StartGameLoop(m *Manager, store *BetStore) {

	roundID := 1

	for {

		fmt.Println("Starting round:", roundID)
		m.StartNewRound(roundID)

		for i := 0; i < 60; i++ {

			if i == 55 {
				fmt.Println("Closing betting")
				m.CloseBetting()
			}

			time.Sleep(1 * time.Second)
		}

		fmt.Println("Round ended:", roundID)

		bets := store.GetBets()
		result := CalculateResult(bets)

		fmt.Println("Result:", result)

		// settle wallet

		store.Clear()
		roundID++
	}
}
