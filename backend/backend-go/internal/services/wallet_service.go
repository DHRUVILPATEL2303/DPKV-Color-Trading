package services

import (
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"errors"
)

type WalletService struct {
	repo     postgres.WalletRepository
	userRepo postgres.UserRepository
}

func NewWalletService(walletRepo *postgres.WalletRepository, userRepo *postgres.UserRepository) *WalletService {
	return &WalletService{
		repo:     *walletRepo,
		userRepo: *userRepo,
	}
}

func (w *WalletService) AddAmount(amount int, userId int) error {
	if amount <= 0 {
		return errors.New("amount must be greater than zero")

	}

	err := w.repo.UpdateBalance(userId, amount)
	if err != nil {
		return err
	}

	return nil
}

func (w *WalletService) DeductAmount(amount int, userId int) error {
	if amount <= 0 {
		return errors.New("amount must be greater than zero")
	}

	user, err := w.userRepo.FindUserByID(userId)
	if err != nil {
		return err
	}

	if int(user.Balance) < amount {
		return errors.New("insufficient balance")
	}

	err = w.repo.UpdateBalance(userId, -amount)
	if err != nil {
		return err
	}
	return nil
}
