package engine

import (
	"context"
	"en/internal/publisher"
	"encoding/json"
	"fmt"
	"log"
	"time"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"

	"github.com/redis/go-redis/v9"
)

func StartGameLoop(m *Manager, store *BetStore, publisher *publisher.Publisher, rdb *redis.Client, api pb.BettingServiceClient) {
	var roundID int
	ctx := context.Background()
	val, err := rdb.Get(ctx, "current_round").Int()
	if err != nil {
		roundID = 1
	} else {
		roundID = val + 1
	}

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

		betResult, err := api.UpdateBetResult(context.Background(), &pb.UpdateBetRequest{
			Round: int64(roundID),
			Color: result,
		})
		if err != nil || !betResult.Success {
			log.Println(err)
			log.Println("for round %s betting result for updaterequest %s", roundID, betResult)
		}

		data := map[string]any{
			"round":  roundID,
			"result": result,
		}

		jsonData, _ := json.Marshal(data)
		fmt.Println(string(jsonData))
		rdb.LPush(ctx, "recent_rounds", jsonData)
		rdb.LTrim(ctx, "recent_rounds", 0, 9)

		go SettleBetsWorkerPool(bets, result, api)

		store.Clear()
		if err := rdb.Set(ctx, "current_round", roundID, 0).Err(); err != nil {
			log.Println("Redis SET error:", err)
		}
		roundID++
	}
}
