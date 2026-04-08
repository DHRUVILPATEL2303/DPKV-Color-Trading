package grpc

import (
	"log"

	pb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/enginepb"

	"google.golang.org/grpc"
)

type EngineClient struct {
	Client pb.EngineServiceClient
}

func NewEngineClient() *EngineClient {

	conn, err := grpc.Dial("localhost:8083", grpc.WithInsecure())
	if err != nil {
		log.Fatal("Failed to connect to engine:", err)
	}

	return &EngineClient{
		Client: pb.NewEngineServiceClient(conn),
	}
}
