package grpc

import (
	"log"
	"os"

	pb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/enginepb"

	"google.golang.org/grpc"
)

type EngineClient struct {
	Client pb.EngineServiceClient
}

func NewEngineClient() *EngineClient {

	addr := os.Getenv("ENGINE_SERVER_GRPC_URL")
	if addr == "" {
		addr = "localhost:8083"
	}

	conn, err := grpc.Dial(addr, grpc.WithInsecure())
	if err != nil {
		log.Fatal("Failed to connect to engine:", err)
	}

	return &EngineClient{
		Client: pb.NewEngineServiceClient(conn),
	}
}
