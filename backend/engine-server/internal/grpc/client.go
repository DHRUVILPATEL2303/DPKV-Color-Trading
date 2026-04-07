package grpc

import (
	"log"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"

	"google.golang.org/grpc"
)

type Client struct {
	Betting pb.BettingServiceClient
}

func NewClient() *Client {

	conn, err := grpc.Dial("localhost:8082", grpc.WithInsecure())
	if err != nil {
		log.Fatal("gRPC connection failed:", err)
	}

	return &Client{
		Betting: pb.NewBettingServiceClient(conn),
	}
}
