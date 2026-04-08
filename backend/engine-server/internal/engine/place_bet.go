package engine

import (
	"context"
	"errors"
	"time"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
)

func (e *Engine) PlaceBet(userID int, amount int, color string) error {

	round := e.manager.GetRound()

	if round == nil {
		return errors.New("no active round")
	}

	if !round.IsOpen {
		return errors.New("betting closed")
	}

	elapsed := time.Since(round.StartTime).Seconds()
	if elapsed >= 55 {
		return errors.New("betting closed (last 5 sec)")
	}

	resp, err := e.api.PlaceBet(context.Background(), &pb.BetRequest{
		UserId: int32(userID),
		Amount: int64(int32(amount)),
		Color:  color,
	})

	if err != nil || !resp.Success {
		return errors.New("wallet deduction failed")
	}
	_, err = e.api.SaveBet(context.Background(), &pb.SaveBetRequest{
		UserId:      int32(userID),
		RoundNumber: int32(round.ID),
		Amount:      int64(amount),
		Color:       color,
	})
	if err != nil {
		return errors.New("failed to save betting")
	}
	e.store.AddBet(Bet{
		UserID: userID,
		Amount: amount,
		Color:  color,
	})

	return nil
}
