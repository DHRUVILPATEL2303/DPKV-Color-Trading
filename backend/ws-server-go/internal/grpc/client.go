package grpc

import (
	"log"
	"os"

	pb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/bettingpb"

	"google.golang.org/grpc"
)

type Client struct {
	Betting pb.BettingServiceClient
}

func NewClientGrpc() *Client {

	addr := os.Getenv("BACKEND_GO_GRPC_URL")
	if addr == "" {
		addr = "localhost:8082"
	}

	conn, err := grpc.Dial(addr, grpc.WithInsecure())
	if err != nil {
		log.Fatal("Failed to connect to gRPC:", err)
	}

	client := pb.NewBettingServiceClient(conn)

	return &Client{
		Betting: client,
	}
}
