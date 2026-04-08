package main

import (
	"log"
	"net"

	"en/internal/engine"
	grpc2 "en/internal/grpc"

	pb "en/Color-Trading/backend/engine-server/proto/enginepb"

	"google.golang.org/grpc"
)

func main() {

	manager := engine.NewManager()
	store := engine.NewBetStore()

	apiClient := grpc2.NewClient()

	engineInstance := engine.NewEngine(manager, store, apiClient.Betting)

	go engine.StartGameLoop(manager, store)

	go startGRPC(engineInstance)

	select {}
}
func startGRPC(engineInstance *engine.Engine) {

	lis, err := net.Listen("tcp", ":8083")
	if err != nil {
		log.Fatal(err)
	}

	grpcServer := grpc.NewServer()

	server := grpc2.NewGrpcServer(engineInstance)

	pb.RegisterEngineServiceServer(grpcServer, server)

	log.Println("Engine gRPC running on :8083")

	if err := grpcServer.Serve(lis); err != nil {
		log.Fatal(err)
	}
}
