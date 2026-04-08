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
	betsService   services.BetsService
	roundsService services.RoundsService
}

func NewBettingServer(walletService services.WalletService, service services.BetsService, roundsService services.RoundsService) *BettingServer {
	return &BettingServer{
		walletService: walletService,
		betsService:   service,
		roundsService: roundsService,
	}
}

func (b *BettingServer) PlaceBet(ctx context.Context, req *pb.BetRequest) (*pb.BetResponse, error) {
	err := b.betsService.PlaceBet(
		int(req.UserId),
		int(req.Round),
		int(req.Amount),
		req.Color,
	)
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
func (b *BettingServer) SaveBet(ctx context.Context, req *pb.SaveBetRequest) (*pb.SaveBetResponse, error) {
	err := b.betsService.SaveBet(req.UserId, int64(req.RoundNumber), req.Amount, req.Color)
	if err != nil {
		return &pb.SaveBetResponse{
			Success: false,
		}, err
	}

	return &pb.SaveBetResponse{
		Success: true,
	}, nil
}
func (b *BettingServer) UpdateBetResult(ctx context.Context, req *pb.UpdateBetRequest) (*pb.UpdateBetResponse, error) {
	err := b.betsService.UpdateBet(req.Round, req.Color)
	if err != nil {
		return &pb.UpdateBetResponse{
			Success: false,
		}, err
	}

	return &pb.UpdateBetResponse{
		Success: true,
	}, nil
}
func (b *BettingServer) GetLastRound(ctx context.Context, req *pb.Empty) (*pb.RoundResponse, error) {
	number, s, err := b.roundsService.GetLastRoundNumber()
	if err != nil {
		return &pb.RoundResponse{}, err

	}

	return &pb.RoundResponse{
		Round:  int32(number),
		Status: s,
	}, nil

}
func (b *BettingServer) CreateRound(ctx context.Context, req *pb.CreateRoundRequest) (*pb.Empty, error) {
	err := b.roundsService.CreateRound(int(req.Round))
	if err != nil {
		return &pb.Empty{}, err
	}

	return &pb.Empty{}, nil
}
func (b *BettingServer) UpdateRoundStatus(ctx context.Context, req *pb.UpdateRoundStatusRequest) (*pb.Empty, error) {
	err := b.roundsService.UpdateStaus(int(req.Round), req.Status)
	if err != nil {
		return &pb.Empty{}, err
	}
	return &pb.Empty{}, nil
}
func (b *BettingServer) SaveRoundResult(ctx context.Context, req *pb.SaveRoundResultRequest) (*pb.Empty, error) {
	err := b.roundsService.SaveResult(int(req.Round), req.WinColor)
	if err != nil {
		return &pb.Empty{}, err
	}
	return &pb.Empty{}, nil
}
func (b *BettingServer) RefundRound(ctx context.Context, req *pb.RefundRequest) (*pb.Empty, error) {
	err := b.roundsService.RefundRound(int(req.Round))
	if err != nil {
		return &pb.Empty{}, err
	}
	return &pb.Empty{}, nil
}
