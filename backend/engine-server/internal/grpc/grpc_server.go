package grpc

import (
	"context"
	pb "en/Color-Trading/backend/engine-server/proto/enginepb"
	"en/internal/engine"
)

type GrpcServer struct {
	pb.UnimplementedEngineServiceServer
	engine *engine.Engine
}

func NewGrpcServer(engine *engine.Engine) *GrpcServer {
	return &GrpcServer{
		engine: engine,
	}
}

func (s *GrpcServer) RequestPlaceBet(ctx context.Context, req *pb.PlaceBetRequest) (*pb.PlaceBetResponse, error) {

	err := s.engine.PlaceBet(
		int(req.UserId),
		int(req.Amount),
		req.Color,
	)

	if err != nil {
		return &pb.PlaceBetResponse{
			Success: false,
			Message: err.Error(),
		}, nil
	}

	return &pb.PlaceBetResponse{
		Success: true,
		Message: "bet placed",
	}, nil
}
