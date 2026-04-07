package grpc

import (
	"log"

	pb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/bettingpb"

	"google.golang.org/grpc"
)

type Client struct {
	Betting pb.BettingServiceClient
}

func NewClientGrpc() *Client {

	conn, err := grpc.Dial("localhost:8082", grpc.WithInsecure())
	if err != nil {
		log.Fatal("Failed to connect to gRPC:", err)
	}

	client := pb.NewBettingServiceClient(conn)

	return &Client{
		Betting: client,
	}
}
