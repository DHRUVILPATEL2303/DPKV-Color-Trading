package grpc

import (
	"log"
	"os"

	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type Client struct {
	Betting pb.BettingServiceClient
}

func NewClient() *Client {
	addr := os.Getenv("BACKEND_GO_GRPC_URL")
	if addr == "" {
		addr = "localhost:8082"
	}

	conn, err := grpc.Dial(
		addr,
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		log.Fatal("gRPC connection failed:", err)
	}

	return &Client{
		Betting: pb.NewBettingServiceClient(conn),
	}
}
