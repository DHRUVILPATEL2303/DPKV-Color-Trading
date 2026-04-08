package services

import (
	"Color-Trading/backend/backend-go/internal/models"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
)

type BetsService struct {
	repo *postgres.BetRepository
}

func NewBetsService(repo *postgres.BetRepository) *BetsService {
	return &BetsService{repo: repo}

}
func (s *BetsService) SaveBet(userId int32, round int64, amount int64, color string) error {
	err := s.repo.SaveBet(int32(userId), int64(round), int64(amount), color)
	if err != nil {
		return err
	}
	return nil
}
func (s *BetsService) GetAllBetsHistory(userId int32) ([]*models.Bet, error) {
	return s.repo.GetAllBetsHistory(userId)
}
