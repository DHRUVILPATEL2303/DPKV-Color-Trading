package services

import (
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"errors"
)

type AdminService struct {
	repo *postgres.AdminRepository
}

func NewAdminService(repo *postgres.AdminRepository) *AdminService {
	return &AdminService{repo: repo}

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
