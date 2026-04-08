package services

import "Color-Trading/backend/backend-go/internal/repositories/postgres"

type RoundsService struct {
	repo *postgres.RoundsRepository
}

func NewRoundsService(repo *postgres.RoundsRepository) *RoundsService {
	return &RoundsService{repo: repo}
}

func (s *RoundsService) GetLastRoundNumber() (int, string, error) {
	round, s2, err := s.repo.GetLastRound()
	if err != nil {
		return 0, "", err
	}
	return round, s2, nil

}
func (s *RoundsService) CreateRound(round int) error {
	return s.repo.CreateRound(round)

}
func (s *RoundsService) UpdateStaus(round int, status string) error {
	return s.repo.UpdateStatus(round, status)
}
func (s *RoundsService) SaveResult(round int, result string) error {
	return s.repo.SaveResult(round, result)
}
func (s *RoundsService) RefundRound(round int) error {
	return s.repo.RefundRound(round)
}
