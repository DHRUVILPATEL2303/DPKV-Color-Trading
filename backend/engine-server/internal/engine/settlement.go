package engine

import (
	"context"
	"fmt"
	"sync"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
)

func SettleBetsWorkerPool(
	bets []Bet,
	result string,
	api pb.BettingServiceClient,
) {

	const workerCount = 20

	jobs := make(chan Bet, len(bets))
	var wg sync.WaitGroup

	for i := 0; i < workerCount; i++ {

		wg.Add(1)

		go func(workerID int) {
			defer wg.Done()

			for bet := range jobs {

				if bet.Color != result {
					continue
				}

				winAmount := bet.Amount * 2

				_, err := api.CreditAmount(context.Background(), &pb.CreditRequest{
					UserId: int32(bet.UserID),
					Amount: int64(winAmount),
				})

				if err != nil {
					fmt.Println("Worker", workerID, "error:", err)
				}
			}
		}(i)
	}

	for _, bet := range bets {
		jobs <- bet
	}

	close(jobs)

	wg.Wait()

	fmt.Println("All settlements completed ")
}
