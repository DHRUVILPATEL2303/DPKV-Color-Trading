package engine

import (
	"context"
	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
	"log"
	"time"
)

func RecoverState(api pb.BettingServiceClient) int {

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	resp, err := api.GetLastRound(ctx, &pb.Empty{})
	if err != nil {
		log.Println("No previous round found, starting fresh")
		return 1
	}

	lastRound := int(resp.Round)
	status := resp.Status

	log.Printf("Recovery → Round: %d Status: %s\n", lastRound, status)

	switch status {

	case "OPEN":
		log.Println("Refunding OPEN round")
		api.RefundRound(ctx, &pb.RefundRequest{
			Round: int32(lastRound),
		})

	case "CLOSED":
		log.Println("CLOSED round found")

		api.RefundRound(ctx, &pb.RefundRequest{
			Round: int32(lastRound),
		})

	case "COMPLETED":
		log.Println("Last round already completed")
	}

	return lastRound + 1
}
