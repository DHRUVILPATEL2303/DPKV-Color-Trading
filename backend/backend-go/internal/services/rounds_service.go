package services

import "Color-Trading/backend/backend-go/internal/repositories/postgres"

type RoundsService struct {
	repo *postgres.RoundsRepository
}

func NewRoundsService(repo *postgres.RoundsRepository) *RoundsService {
	return &RoundsService{repo: repo}
}

func (s *RoundsService) GetLastRoundNumber() int {
	number := s.repo.GetLastRoundNumber()
	return number

}
