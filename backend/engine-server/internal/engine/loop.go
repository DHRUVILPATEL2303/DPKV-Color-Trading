package engine

import (
	"context"
	"en/internal/publisher"
	"fmt"
	"log"
	"time"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
)

func StartGameLoop(m *Manager, store *BetStore, publisher *publisher.Publisher, api pb.BettingServiceClient) {

	roundID := 1

	for {

		fmt.Println("Starting round:", roundID)

		m.StartNewRound(roundID)

		publisher.Publish("game_updates", map[string]any{
			"type":     "ROUND_START",
			"round_id": roundID,
		})

		for i := 0; i < 60; i++ {

			publisher.Publish("game_updates", map[string]any{
				"type":         "TIMER",
				"round_id":     roundID,
				"seconds_left": 60 - i,
			})

			if i == 55 {
				fmt.Println("Closing betting")
				m.CloseBetting()

				err := publisher.Publish("game_updates", map[string]any{
					"type": "BETTING_CLOSED",
				})
				if err != nil {
					return
				}
			}

			time.Sleep(1 * time.Second)
		}

		fmt.Println("Round ended:", roundID)

		bets := store.GetBets()
		result := CalculateResult(bets)

		fmt.Println("Result:", result)

		err := publisher.Publish("game_updates", map[string]any{
			"type":     "RESULT",
			"round_id": roundID,
			"result":   result,
		})
		if err != nil {
			return
		}

		betResult, err := api.UpdateBetResult(context.Background(), &pb.UpdateBetRequest{
			Round: int64(roundID),
			Color: result,
		})
		if err != nil || !betResult.Success {
			log.Println(err)
			log.Println("for round %s betting result for updaterequest %s", roundID, betResult)
		}
		go SettleBetsWorkerPool(bets, result, api)

		store.Clear()

		roundID++
	}
}
