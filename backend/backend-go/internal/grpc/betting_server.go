package grpc

import (
	pb "Color-Trading/backend/backend-go/Color-Trading/backend/backend-go/proto/bettingpb"
	"Color-Trading/backend/backend-go/internal/services"
	_ "Color-Trading/backend/backend-go/internal/services"
	"context"
	_ "context"
)

type BettingServer struct {
	pb.UnimplementedBettingServiceServer
	walletService services.WalletService
}

func NewBettingServer(walletService services.WalletService) *BettingServer {
	return &BettingServer{
		walletService: walletService,
	}
}

func (b *BettingServer) PlaceBet(ctx context.Context, req *pb.BetRequest) (*pb.BetResponse, error) {
	err := b.walletService.DeductAmount(int(req.Amount), int(req.UserId))
	if err != nil {
		return &pb.BetResponse{
			Success: false,
			Message: err.Error(),
		}, nil
	}

	return &pb.BetResponse{
		Success: true,
		Message: "Bet Placed Successfully",
	}, nil

}
func (b *BettingServer) CreditAmount(ctx context.Context, req *pb.CreditRequest) (*pb.CreditResponse, error) {
	err := b.walletService.AddAmount(int(req.Amount), int(req.UserId))
	if err != nil {
		return &pb.CreditResponse{
			Success: false,
			Amount:  req.Amount,
		}, err
	}

	return &pb.CreditResponse{
		Success: true,
		Amount:  req.Amount,
	}, nil
}
