package services

import (
	"Color-Trading/backend/backend-go/internal/models"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"errors"
)

type AdminService struct {
	repo      *postgres.AdminRepository
	roundRepo *postgres.RoundsRepository
}

func NewAdminService(repo *postgres.AdminRepository, repository *postgres.RoundsRepository) *AdminService {
	return &AdminService{repo: repo, roundRepo: repository}

}

func (service *AdminService) AddFunds(userId int32, amount int64, adminId int64) error {
	if amount <= 0 {
		return errors.New("amount must be greater than 0")
	}
	return service.repo.AddFunds(userId, amount, adminId)
}

func (s *AdminService) DeductFunds(userID int32, amount int64, adminID int64) error {
	if amount <= 0 {
		return errors.New("amount must be greater than 0")
	}
	return s.repo.DeductFunds(userID, amount, adminID)
}
func (s *AdminService) GetAdminLogs(page int, limit int) ([]*models.AdminLog, int, error) {
	if page < 1 {
		page = 1
	}
	if limit < 1 {
		limit = 20
	}
	return s.repo.GetAdminLogs(page, limit)
}
func (s *AdminService) GetLastRounds(limit int) ([]*models.Round, error) {
	return s.roundRepo.GetLastRounds(limit)
}

func (s *AdminService) GetRounds(page int, limit int) ([]*models.Round, int, error) {
	return s.roundRepo.GetRounds(page, limit)
}
