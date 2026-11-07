package engine

import (
	"context"
	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
	"log"
	"strings"
	"time"
)

func RecoverState(api pb.BettingServiceClient) int {
	var resp *pb.RoundResponse
	var err error

	for attempts := 1; attempts <= 30; attempts++ {
		ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
		resp, err = api.GetLastRound(ctx, &pb.Empty{})
		cancel()

		if err == nil {
			break
		}

		if strings.Contains(err.Error(), "no rows in result set") {
			log.Println("Database has no previous rounds, starting fresh with round 1")
			return 1
		}

		log.Printf("Waiting for backend-go gRPC service to be ready... (attempt %d/30): %v\n", attempts, err)
		time.Sleep(2 * time.Second)
	}

	if err != nil {
		log.Printf("Could not contact backend-go after retries (%v), starting fresh with round 1\n", err)
		return 1
	}

	lastRound := int(resp.Round)
	status := resp.Status

	log.Printf("Recovery → Round: %d Status: %s\n", lastRound, status)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	switch status {

	case "OPEN":
		log.Printf("Refunding OPEN round %d\n", lastRound)
		api.RefundRound(ctx, &pb.RefundRequest{
			Round: int32(lastRound),
		})

	case "CLOSED":
		log.Printf("Refunding CLOSED round %d\n", lastRound)
		api.RefundRound(ctx, &pb.RefundRequest{
			Round: int32(lastRound),
		})

	case "COMPLETED":
		log.Println("Last round already completed")
	}

	return lastRound + 1
}
